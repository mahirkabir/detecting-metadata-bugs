package engine;

public class EngineFactory {
    private static String projectPath;
    private static IEngineAssert engineAssert;
    private static IEngineCache engineCache;
    private static IEngineDecl engineDecl;
    private static IEngineEvaluate evaluator;
    private static IEngineFor engineFor;
    private static IEngineFunctions engineFunctions;

    public static String getProjectPath() {
        return projectPath;
    }

    /**
     * Return the prepared engine for assert
     * @return
     */
    public static IEngineAssert getEngineAssert() {
        return engineAssert;
    }

    /**
     * Return the prepared engine for caching
     * @return
     */
    public static IEngineCache getEngineCache() {
        return engineCache;
    }

    /**
     * Return the prepared engine for declaration
     * @return
     */
    public static IEngineDecl getEngineDecl() {
        return engineDecl;
    }

    /**
     * Return the prepared engine for evaluation
     * @return
     */
    public static IEngineEvaluate getEvaluator() {
        return evaluator;
    }

    /**
     * Return the prepared engine for FOR
     * @return
     */
    public static IEngineFor getEngineFor() {
        return engineFor;
    }

    /**
     * Return the prepared engine for functions
     * @return
     */
    public static IEngineFunctions getEngineFunctions() {
        return engineFunctions;
    }

    public static void setEngineAssert(IEngineAssert engineAssert) {
        EngineFactory.engineAssert = engineAssert;
    }

    public static void setEngineCache(IEngineCache engineCache) {
        EngineFactory.engineCache = engineCache;
    }

    public static void setEngineDecl(IEngineDecl engineDecl) {
        EngineFactory.engineDecl = engineDecl;
    }

    public static void setEvaluator(IEngineEvaluate engineEvaluate) {
        EngineFactory.evaluator = engineEvaluate;
    }

    public static void setProjectPath(String projectPath) {
        EngineFactory.projectPath = projectPath;
    }

    public static void setEngineFor(IEngineFor engineFor) {
        EngineFactory.engineFor = engineFor;
    }

    public static void setEngineFunctions(IEngineFunctions engineFunctions) {
        EngineFactory.engineFunctions = engineFunctions;
    }
}
