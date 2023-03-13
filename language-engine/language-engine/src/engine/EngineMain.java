package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static void main(String args[]) {
        String versionCategories = args[0];
        String datasetFolder = args[1];
        Config config = loadConfig(args[2]);

        Map<String, List<VersionCategory>> mapVersions = getProjectVersions(versionCategories);

        int maxVersionCnt = 0;
        for (Map.Entry<String, List<VersionCategory>> projVersion : mapVersions.entrySet())
            maxVersionCnt = Math.max(maxVersionCnt, projVersion.getValue().size());

        int versionIdx = 0;
        int cnt = 1; // Total: 71298

        while (versionIdx < maxVersionCnt) {

            for (Map.Entry<String, List<VersionCategory>> projVersion : mapVersions.entrySet()) {

                String currentProject = projVersion.getKey();
                List<VersionCategory> versions = projVersion.getValue();

                if (versionIdx < versions.size()) {
                    VersionCategory versionInfo = versions.get(versionIdx);
                    System.out
                            .println("Processing version: " + versionInfo.getCommitId() + " of project: "
                                    + currentProject);

                    Path outputPath = Paths.get("output", currentProject + ".txt");
                    EngineFactory.setOutputPath(outputPath.toString());

                    Path projectPath = Paths.get(datasetFolder, currentProject);
                    EngineFactory.setProjectPath(projectPath.toString());
                    Helper.gitCheckout(projectPath.toString(), versionInfo.getCommitId());

                    bindEngines();
                    engineDecl = EngineFactory.getEngineDecl();

                    List<RuleSet> rulesetsToRun = getRulesetsToRun(versionInfo, config);
                    rulesetsToRun.forEach(ruleset -> {
                        ruleset.run.forEach(rule -> {
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
                                System.out.println("Oops. Error running: " + rule);
                                System.out.println(e.getMessage());
                                e.printStackTrace();
                            }
                        });
                    });

                    System.out.println("Processed version: " + cnt++);
                }
            }

            versionIdx++;
        }

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
            System.out.println("Error reading " + versionCategories + ": " + e.toString());
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
