package engine;

import java.io.FileReader;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import models.Config;
import parser.ASTStart;
import parser.Eg12;
import parser.Node;
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
        EngineFactory.setProjectPath(args[1]);
        bindEngines();
        engineDecl = EngineFactory.getEngineDecl();

        Config config = loadConfig(args[0]);

        for (String rule : config.rules.run) {
            String ruleLoc = Paths.get(config.rules.dir, rule + ".txt").toString();
            System.out.println("Reading from standard input: " + ruleLoc);

            Eg12 t;
            try {
                t = new Eg12(new java.io.FileInputStream(ruleLoc));
            } catch (java.io.FileNotFoundException e) {
                System.out.println("Java Parser Version 1.0.2:  File " + ruleLoc + " not found.");
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
            JSONObject rules = (JSONObject) root.get("rules");
            config.rules.dir = (String) rules.get("dir");
            JSONArray rulesToRun = (JSONArray) rules.get("run");
            for (Object elm : rulesToRun) {
                String rule = (String) elm;
                config.rules.run.add(rule);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return config;
    }
}
