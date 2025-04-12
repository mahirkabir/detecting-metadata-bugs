package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.databind.ObjectMapper;

import models.Config;
import models.RuleSet;
import models.VersionCategory;
import parser.ASTStart;
import parser.Eg12;
import parser.Node;
import utils.Constants;
import utils.Helper;
import utils.Logger;

public class EngineMain {
    /**
     * Use DI to bind the factory engine interfaces with engine implementations
     * These engines will be accessed everywhere in the project through the factory
     */
    private static void bindEngines() {
        EngineFactory.setEngineDecl(new EngineDecl());
        EngineFactory.setEngineCache(new EngineCache());
        EngineFactory.setEvaluator(new EngineEvaluate());
        EngineFactory.setEngineFunctions(new EngineFunctions());
        EngineFactory.setEngineFor(new EngineFor());
        EngineFactory.setEngineAssert(new EngineAssert());
    }

    private static IEngineDecl engineDecl;

    public static void main(String[] args) {
        String datasetFolder = args[0];
        String currentProject = args[1];
        String commitId = args[2];
        String outputFolder = args[3];
        String rulesFolder = args[4];
        String logPath = args[5];
        String libraryRegexPatternFile = args[6];

        EngineFactory.setLogPath(logPath);
        EngineFactory.setLibraryRegexPatterns(fetchLibraryRegex(libraryRegexPatternFile));
        String processingLog = String.format("Processing project: %s, version: %s", currentProject, commitId);
        System.out.println(processingLog);
        Logger.log(processingLog);

        Path outputPath = Paths.get(outputFolder, currentProject + ".txt");
        EngineFactory.setOutputPath(outputPath.toString());

        Path projectPath = Paths.get(datasetFolder, currentProject);
        EngineFactory.setProjectPath(projectPath.toString());

        EngineFactory.setProjectCommitId(commitId);
        Helper.gitCheckout(projectPath.toString(), commitId);

        EngineFactory.setEngineVersionControl(new EngineVersionControl(projectPath.toString()));

        bindEngines();
        engineDecl = EngineFactory.getEngineDecl();

        LocalTime startCurrentTime = LocalTime.now();
        Logger.log("The current time is: " + startCurrentTime);

        try {
            Path rulesFolderPath = Paths.get(rulesFolder);
            File[] files = rulesFolderPath.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    String rule = file.getName();
                    EngineFactory.setRunningRule(rule);
                    String ruleLoc = file.getAbsolutePath();
                    System.out.println("Reading from standard input: " + ruleLoc);

                    Eg12 t;
                    try {
                        t = new Eg12(new java.io.FileInputStream(ruleLoc));
                    } catch (java.io.FileNotFoundException e) {
                        System.out.println("Java Parser Version 1.0.2: File " + ruleLoc + " not found.");
                        return;
                    }

                    try {
                        ASTStart n = t.Start();

                        engineDecl.createFrame();
                        int totalChildren = n.jjtGetNumChildren();
                        for (int i = 0; i < totalChildren; ++i) {
                            Node stmnt = n.jjtGetChild(i);
                            Helper.process(stmnt);
                        }

                        engineDecl.removeFrame();
                        System.out.println("Thank you.");

                    } catch (Exception e) {
                        Logger.log("Oops. Error running: " + file);
                        System.out.println("Oops. Error running: " + file);
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }

        LocalTime endCurrentTime = LocalTime.now();
        Logger.log("The current time is: " + endCurrentTime);
        String timeDiffLog = String.format("Project: %s\tVersion: %s\tTime Taken: %s",
                currentProject, commitId.substring(0, 6),
                calculateTimeDiff(startCurrentTime, endCurrentTime));
        Logger.log(timeDiffLog);
    }

    public static void main_ORG(String args[]) {
        String versionCategories = args[0];
        String datasetFolder = args[1];
        Config config = loadConfig(args[2]);
        EngineFactory.setLogPath(args[4]);
        EngineFactory.setLibraryRegexPatterns(fetchLibraryRegex(args[5]));

        Map<String, List<VersionCategory>> mapVersions = getProjectVersions(versionCategories);

        int maxVersionCnt = 0;
        for (Map.Entry<String, List<VersionCategory>> projVersion : mapVersions.entrySet())
            maxVersionCnt = Math.max(maxVersionCnt, projVersion.getValue().size());

        int versionIdx = 0;
        int cnt = 1; // Total: 71298

        Map<String, Set<String>> dictCheckedOut = new HashMap<>();

        while (versionIdx < maxVersionCnt) {

            for (Map.Entry<String, List<VersionCategory>> projVersion : mapVersions.entrySet()) {

                String currentProject = projVersion.getKey();
                List<VersionCategory> versions = projVersion.getValue();

                if (versionIdx < versions.size()) {
                    VersionCategory versionInfo = versions.get(versionIdx);
                    String processingLog = String.format("Processing project: %s, version: %s", currentProject,
                            versionInfo.getCommitId());
                    System.out.println(processingLog);
                    Logger.log(processingLog);

                    Path outputPath = Paths.get(args[3], currentProject + ".txt");
                    EngineFactory.setOutputPath(outputPath.toString());

                    Path projectPath = Paths.get(datasetFolder, currentProject);
                    EngineFactory.setProjectPath(projectPath.toString());
                    EngineFactory.setProjectCommitId(versionInfo.getCommitId());
                    EngineFactory.setEngineVersionControl(new EngineVersionControl(projectPath.toString()));

                    bindEngines();
                    engineDecl = EngineFactory.getEngineDecl();

                    List<RuleSet> rulesetsToRun = getRulesetsToRun(versionInfo, config);
                    LocalTime startCurrentTime = LocalTime.now();
                    Logger.log("The current time is: " + startCurrentTime);

                    rulesetsToRun.forEach(ruleset -> {
                        ruleset.run.forEach(rule -> {
                            if (okToRun(currentProject, rule)) {
                                String ppath = projectPath.toString();
                                String commitid = versionInfo.getCommitId();
                                if (!dictCheckedOut.containsKey(ppath)) {
                                    // dictCheckedOut.put(ppath, Set.of(commitid));
                                    // Helper.gitCheckout(ppath, commitid);
                                } else if (!dictCheckedOut.get(ppath).contains(commitid)) {
                                    Set<String> mutable = new HashSet<>(dictCheckedOut.get(ppath));
                                    mutable.add(commitid);
                                    // dictCheckedOut.put(ppath, mutable);
                                    // Helper.gitCheckout(ppath, commitid);
                                }

                                EngineFactory.setRunningRule(rule);
                                String ruleLoc = Paths.get(ruleset.dir, rule + ".txt").toString();
                                System.out.println("Reading from standard input: " + ruleLoc);

                                Eg12 t;
                                try {
                                    t = new Eg12(new java.io.FileInputStream(ruleLoc));
                                } catch (java.io.FileNotFoundException e) {
                                    System.out.println("Java Parser Version 1.0.2: File " + ruleLoc + " not found.");
                                    return;
                                }

                                try {
                                    ASTStart n = t.Start();

                                    engineDecl.createFrame();
                                    int totalChildren = n.jjtGetNumChildren();
                                    for (int i = 0; i < totalChildren; ++i) {
                                        Node stmnt = n.jjtGetChild(i);
                                        Helper.process(stmnt);
                                    }

                                    engineDecl.removeFrame();
                                    System.out.println("Thank you.");

                                } catch (Exception e) {
                                    Logger.log("Oops. Error running: " + rule);
                                    System.out.println("Oops. Error running: " + rule);
                                    System.out.println(e.getMessage());
                                    e.printStackTrace();
                                }
                            }

                        });
                    });

                    LocalTime endCurrentTime = LocalTime.now();
                    Logger.log("The current time is: " + endCurrentTime);
                    String timeDiffLog = String.format("Project: %s\tVersion: %s\tTime Taken: %s",
                            currentProject, versionInfo.getCommitId().substring(0, 6),
                            calculateTimeDiff(startCurrentTime, endCurrentTime));
                    Logger.log(timeDiffLog);
                    System.out.println("Processed version: " + cnt++);
                }
            }

            versionIdx++;
        }
    }

    /**
     * Calculate the time difference between start and end time of process
     * 
     * @param startCurrentTime
     * @param endCurrentTime
     * @return
     */
    private static String calculateTimeDiff(LocalTime startCurrentTime, LocalTime endCurrentTime) {
        Duration duration = Duration.between(startCurrentTime, endCurrentTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.toSeconds() % 60;
        return hours + "h " + minutes + "m " + seconds + "s";
    }

    /**
     * Read library regex patterns file and return the patterns
     * 
     * @param string
     * @return
     */
    private static List<String> fetchLibraryRegex(String regexFilePath) {
        try {
            return Files.lines(Paths.get(regexFilePath))
                    .map(String::trim)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            Logger.log("Error reading regex file: " + regexFilePath + " => " + ex.getMessage());
            return new ArrayList<String>();
        }
    }

    private static boolean okToRun(String currentProject, String rule) {
        return true;
        // Map<String, Set<String>> repoBugTable = new HashMap<>();

        // // repoBugTable.put("heroku-spike-tomcat", Set.of("beanClassExists"));
        // // repoBugTable.put("BooSpring", Set.of("beanClassExists", "setterMethod"));
        // // repoBugTable.put("spring-rest-security", Set.of("beanClassExists"));
        // // repoBugTable.put("daw02", Set.of("runwithNoTest"));
        // // repoBugTable.put("mongoservice", Set.of("beanClassExists"));
        // // repoBugTable.put("mobileiq", Set.of("beanClassExists", "setterMethod"));
        // // repoBugTable.put("MyTest", Set.of("beanClassExists"));
        // // repoBugTable.put("ACMEReservedMeetingRooms", Set.of("beanClassExists",
        // // "setterMethod", "runwithNoTest"));
        // // repoBugTable.put("FreeClassFinder_Server", Set.of("beanClassExists",
        // // "setterMethod"));
        // // repoBugTable.put("lushtext", Set.of("beanClassExists"));

        // // repoBugTable.put("OnzeVencedor", Set.of("beanClassExists",
        // "setterMethod"));
        // // repoBugTable.put("jbpm-multimodulo", Set.of("beanClassExists"));
        // // repoBugTable.put("jee_baseapp", Set.of("beanClassExists", "setterMethod",
        // // "methodExists"));
        // // repoBugTable.put("voice-control-tunnel",
        // // Set.of("beanExists", "xmlPathCheck", "runwithNoTest", "beanClassExists",
        // // "setterMethod",
        // // "constructorArgumentField", "constructorArgumentFieldType",
        // // "constructorIndexOutOfBound",
        // // "methodExists"));
        // // repoBugTable.put("eFood", Set.of("runwithNoTest"));
        // // repoBugTable.put("cas-webapp-jboss7",
        // // Set.of("beanExists", "beanClassExists", "setterMethod",
        // // "constructorArgumentField",
        // // "constructorArgumentFieldType", "constructorIndexOutOfBound",
        // // "methodExists"));
        // // repoBugTable.put("ett-integration", Set.of("runwithNoTest"));
        // // repoBugTable.put("test-ejb-arquiteture", Set.of("setterMethod"));
        // // repoBugTable.put("BacklogTool", Set.of("beanClassExists", "setterMethod",
        // // "constructorArgumentField",
        // // "constructorArgumentFieldType", "constructorIndexOutOfBound",
        // // "methodExists"));
        // // repoBugTable.put("aw2m-vulture", Set.of("beanClassExists", "setterMethod",
        // // "constructorArgumentField",
        // // "constructorArgumentFieldType", "constructorIndexOutOfBound"));
        // // repoBugTable.put("PurchaseManagement", Set.of("beanExists",
        // "runwithNoTest",
        // // "runwithNoParameters",
        // // "suiteclassesNoRunwith", "beanClassExists", "setterMethod"));
        // // repoBugTable.put("FarmaciaFuturo", Set.of("setterMethod"));
        // // repoBugTable.put("lite-framework", Set.of("runwithNoTest"));
        // // repoBugTable.put("feedAggregate", Set.of("beanClassExists",
        // "setterMethod",
        // // "methodExists"));
        // // repoBugTable.put("homesources", Set.of("beanClassExists"));
        // // repoBugTable.put("johnsully83_groovy",
        // // Set.of("beanExists", "beanClassExists", "setterMethod",
        // // "constructorArgumentField",
        // // "constructorArgumentFieldType", "constructorIndexOutOfBound",
        // // "methodExists"));
        // // repoBugTable.put("rest-app", Set.of("beanClassExists", "setterMethod",
        // // "methodExists"));
        // // repoBugTable.put("green-webflow",
        // // Set.of("importXMLIntoAnnotation", "beanExists", "beanClassExists",
        // // "setterMethod",
        // // "constructorArgumentField", "constructorArgumentFieldType",
        // // "constructorIndexOutOfBound"));
        // // repoBugTable.put("DataAnalyzePlatform",
        // // Set.of("beanExists", "runwithNoTest", "beanClassExists", "setterMethod",
        // // "methodExists"));
        // // repoBugTable.put("ComicRate", Set.of("beanClassExists", "setterMethod"));
        // // repoBugTable.put("springproject", Set.of("beanClassExists",
        // "setterMethod"));
        // // repoBugTable.put("trip-service", Set.of("beanClassExists", "setterMethod",
        // // "methodExists"));
        // // repoBugTable.put("bennu-renderers",
        // // Set.of("setterMethod", "runwithNoTest", "runwithNoParameters",
        // // "suiteclassesNoRunwith"));
        // // repoBugTable.put("kie-wb-distributions", Set.of("beanClassExists",
        // // "setterMethod"));
        // // repoBugTable.put("genetic-program",
        // // Set.of("beanClassExists", "constructorArgumentField",
        // // "constructorArgumentFieldType",
        // // "constructorIndexOutOfBound", "runwithNoTest", "runwithNoParameters",
        // // "suiteclassesNoRunwith",
        // // "suiteclassesNoTest"));

        // // repoBugTable.put("nhimeyecms",
        // // Set.of("beanExists", "setterMethod", "beanClassExists",
        // // "constructorArgumentField",
        // // "constructorArgumentFieldType", "constructorIndexOutOfBound",
        // // "runwithNoTest"));
        // // repoBugTable.put("Longminder", Set.of("setterMethod", "runwithNoTest"));
        // // repoBugTable.put("Dino", Set.of("beanExists", "runwithNoTest",
        // // "runwithNoParameters", "suiteclassesNoRunwith"));
        // // repoBugTable.put("aab-main", Set.of("beanClassExists", "setterMethod",
        // // "methodExists"));
        // // repoBugTable.put("chinabank-application2", Set.of("beanClassExists",
        // // "setterMethod", "methodExists"));
        // // repoBugTable.put("EduServer", Set.of("beanClassExists",
        // // "constructorArgumentField",
        // // "constructorArgumentFieldType", "constructorIndexOutOfBound",
        // "setterMethod",
        // // "methodExists"));

        // // repoBugTable.put("emite", Set.of("runwithNoTest"));
        // // repoBugTable.put("smorales-app-headbanging-ee6", Set.of("beanClassExists",
        // // "runwithNoTest",
        // // "runwithNoParameters", "suiteclassesNoRunwith", "setterMethod"));
        // // repoBugTable.put("spring-petclinic",
        // // Set.of("beanExists", "runwithNoTest", "runwithNoParameters",
        // // "suiteclassesNoRunwith", "beanClassExists",
        // // "setterMethod", "constructorArgumentField",
        // "constructorArgumentFieldType",
        // // "constructorIndexOutOfBound", "methodExists"));
        // // repoBugTable.put("wombat", Set.of("beanExists", "beanClassExists"));
        // // repoBugTable.put("training-djpk-2014-01", Set.of("beanExists",
        // // "xmlPathCheck", "beanClassExists",
        // // "setterMethod", "runwithNoTest", "runwithNoParameters",
        // // "suiteclassesNoRunwith"));

        // // repoBugTable.put("rop", Set.of("beanExists", "beanClassExists",
        // // "setterMethod"));
        // // repoBugTable.put("kolomet",
        // // Set.of("beanExists", "beanClassExists", "setterMethod",
        // // "constructorArgumentField",
        // // "constructorArgumentFieldType", "constructorIndexOutOfBound",
        // // "runwithNoTest",
        // // "runwithNoParameters", "suiteclassesNoRunwith"));
        // // repoBugTable.put("Lemo-Data-Management-Server", Set.of("setterMethod",
        // // "runwithNoTest"));
        // // repoBugTable.put("cipol",
        // // Set.of("beanExists", "beanClassExists", "setterMethod",
        // // "constructorArgumentField",
        // // "constructorArgumentFieldType", "constructorIndexOutOfBound",
        // // "runwithNoTest",
        // // "runwithNoParameters", "suiteclassesNoRunwith"));
        // // repoBugTable.put("aerogear-unifiedpush-server", Set.of("beanClassExists",
        // // "setterMethod", "runwithNoTest",
        // // "runwithNoParameters", "suiteclassesNoRunwith"));

        // // // original real bug repos
        // // repoBugTable.put("angular-js-spring-mybatis", Set.of("setterMethod",
        // // "beanClassExists"));
        // // repoBugTable.put("basis-webapp-sample", Set.of("setterMethod",
        // // "beanClassExists"));
        // repoBugTable.put("dspace-rest", Set.of("beanClassExists"));
        // // repoBugTable.put("MongoDBSpringRest",
        // // Set.of("constructorArgumentField", "setterMethod", "beanExists",
        // // "xmlPathCheck",
        // // "constructorIndexOutOfBound", "constructorArgumentFieldType",
        // // "beanClassExists"));
        // // repoBugTable.put("ShcUtils", Set.of("methodExists", "setterMethod",
        // // "beanClassExists"));
        // // repoBugTable.put("spring-vaadin", Set.of("runwithNoParameters",
        // // "setterMethod",
        // // "runwithNoTest", "suiteclassesNoRunwith", "beanClassExists"));
        // // repoBugTable.put("cv-web", Set.of("setterMethod", "beanClassExists"));
        // // repoBugTable.put("biyam_repository", Set.of("constructorArgumentField",
        // // "setterMethod",
        // // "methodExists", "constructorIndexOutOfBound",
        // "constructorArgumentFieldType",
        // // "beanClassExists"));
        // // repoBugTable.put("generica", Set.of("runwithNoParameters", "setterMethod",
        // // "runwithNoTest",
        // // "suiteclassesNoRunwith", "beanClassExists"));
        // // repoBugTable.put("collection-manager", Set.of("runwithNoParameters",
        // // "setterMethod",
        // // "runwithNoTest", "suiteclassesNoRunwith", "beanExists", "xmlPathCheck",
        // // "beanClassExists"));
        // // repoBugTable.put("Kognitywistyka", Set.of("setterMethod",
        // // "beanClassExists"));
        // // repoBugTable.put("LIBRARY", Set.of("setterMethod", "beanClassExists"));
        // // repoBugTable.put("enterprise-routing-system", Set.of("setterMethod",
        // // "beanClassExists"));
        // // repoBugTable.put("jarvis", Set.of("setterMethod", "beanClassExists"));
        // // repoBugTable.put("I377-esk", Set.of("methodExists", "setterMethod",
        // // "beanClassExists"));
        // // repoBugTable.put("FileExplorer",
        // // Set.of("setterMethod", "beanExists", "methodExists", "beanClassExists"));
        // repoBugTable.put("E2-Demo",
        // Set.of("runwithNoParameters", "constructorArgumentField", "setterMethod",
        // "runwithNoTest", "suiteclassesNoRunwith", "beanExists", "methodExists",
        // "constructorIndexOutOfBound", "constructorArgumentFieldType",
        // "beanClassExists"));
        // // repoBugTable.put("aioweb", Set.of("constructorArgumentField",
        // "setterMethod",
        // // "runwithNoParameters", "runwithNoTest", "suiteclassesNoRunwith",
        // // "beanExists", "xmlPathCheck",
        // // "constructorIndexOutOfBound", "methodExists",
        // "constructorArgumentFieldType",
        // // "beanClassExists"));
        // // repoBugTable.put("CWISE-Portal",
        // // Set.of("runwithNoParameters", "setterMethod", "constructorArgumentField",
        // // "runwithNoTest", "suiteclassesNoRunwith", "suiteclassesNoTest",
        // "beanExists",
        // // "xmlPathCheck",
        // // "constructorIndexOutOfBound", "methodExists",
        // "constructorArgumentFieldType",
        // // "beanClassExists"));

        // return repoBugTable.containsKey(currentProject)
        // && repoBugTable.get(currentProject).contains(rule);
    }

    /**
     * Load startup config
     * 
     * @param filepath
     * @return
     */
    private static Config loadConfig(String filepath) {
        Config config = Config.getInstance();

        try {
            JSONParser parser = new JSONParser();
            JSONObject root = (JSONObject) parser.parse(new FileReader(filepath));
            JSONArray rules = (JSONArray) root.get(Constants.JSON_KEY_RULESETS);
            ObjectMapper mapper = new ObjectMapper();

            for (Object ruleElm : rules) {
                RuleSet ruleSet = mapper.readValue(ruleElm.toString(), RuleSet.class);
                config.rulesets.add(ruleSet);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return config;
    }

    /**
     * Get <Project Name, List of Version Info> dictionary for each project
     * 
     * @param versionCategories
     * @return
     */
    private static Map<String, List<VersionCategory>> getProjectVersions(String versionCategories) {
        Map<String, List<VersionCategory>> mapVersions = new LinkedHashMap<String, List<VersionCategory>>();
        try (FileReader fr = new FileReader(versionCategories)) {
            BufferedReader br = new BufferedReader(fr);

            String currentProject = "";
            while (br.ready()) {
                String line = br.readLine();
                if (line.startsWith("Total versions"))
                    break;
                else if (line.contains(":")) {
                    currentProject = line.split(":")[0];
                    mapVersions.put(currentProject, new ArrayList<VersionCategory>());
                } else {
                    String parts[] = line.split("\t");
                    VersionCategory versionCategory = new VersionCategory();
                    versionCategory.setCommitId(parts[0]);
                    versionCategory.setCommitDate(parts[1].substring(1, parts[1].length() - 1));
                    versionCategory.setHasBeanChanges(parts[2].equals("Beans=1"));
                    versionCategory.setHasAnnotationChanges(parts[3].equals("Annotations=1"));
                    versionCategory.setHasJUnitsChanges(parts[4].equals("JUnits=1"));
                    mapVersions.get(currentProject).add(versionCategory);
                }
            }

            br.close();
        } catch (IOException e) {
            Logger.log("Error reading " + versionCategories + ": " + e.toString());
        }
        return mapVersions;
    }

    /**
     * Prepare list of rule categories to run depending on version info
     * 
     * @param versionInfo
     * @param config
     * @return
     */
    private static List<RuleSet> getRulesetsToRun(VersionCategory versionInfo, Config config) {
        List<RuleSet> rulesetsToRun = new ArrayList<RuleSet>();

        for (RuleSet ruleset : config.rulesets) {
            if (ruleset.category.equals(Constants.CATEGORY_GENERAL)
                    || (ruleset.category.equals(Constants.CATEGORY_BEANS) && versionInfo.hasBeanChanges())
                    || (ruleset.category.equals(Constants.CATEGORY_ANNOTATIONS) && versionInfo.hasAnnotationChanges())
                    || (ruleset.category.equals(Constants.CATEGORY_JUNITS) && versionInfo.hasJUnitsChanges()))
                rulesetsToRun.add(ruleset);
        }

        return rulesetsToRun;
    }
}
