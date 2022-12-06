package engine;

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
        EngineFactory.setEngineAssert(new EngineAssert());
        EngineFactory.setEngineCache(new EngineCache());
        EngineFactory.setEngineDecl(new EngineDecl());
        EngineFactory.setEvaluator(new EngineEvaluate());
        EngineFactory.setEngineFor(new EngineFor());
        EngineFactory.setEngineFunctions(new EngineFunctions());
    }

    private static IEngineDecl engineDecl;

    public static void main(String args[]) {
        EngineFactory.setProjectPath(args[1]);
        bindEngines();
        engineDecl = EngineFactory.getEngineDecl();

        Eg12 t;
        System.out.println("Reading from standard input...");
        try {
            t = new Eg12(new java.io.FileInputStream(args[0]));
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Java Parser Version 1.0.2:  File " + args[0] + " not found.");
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
            System.out.println("Oops.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
