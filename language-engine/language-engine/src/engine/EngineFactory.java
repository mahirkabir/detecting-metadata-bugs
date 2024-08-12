package engine;

/*
 * Factory class containing all the engine and helper classes
 */
public class EngineFactory {
    private static String projectPath;
    private static String projectCommitId;

    private static String runningRule;
    private static String outputPath;
    private static String logPath;

    private static IEngineAssert engineAssert;
    private static IEngineCache engineCache;
    private static IEngineDecl engineDecl;
    private static IEngineEvaluate evaluator;
    private static IEngineFor engineFor;
    private static IEngineFunctions engineFunctions;
    private static IEngineVersionControl engineVersionControl;

    /**
     * Get the project's path for which we are running our analysis
     */
    public static String getProjectPath() {
        return projectPath;
    }

    /**
     * Return the prepared engine for assert
     * 
     * @return
     */
    public static IEngineAssert getEngineAssert() {
        return engineAssert;
    }

    /**
     * Return the prepared engine for caching
     * 
     * @return
     */
    public static IEngineCache getEngineCache() {
        return engineCache;
    }

    /**
     * Return the prepared engine for declaration
     * 
     * @return
     */
    public static IEngineDecl getEngineDecl() {
        return engineDecl;
    }

    /**
     * Return the prepared engine for evaluation
     * 
     * @return
     */
    public static IEngineEvaluate getEvaluator() {
        return evaluator;
    }

    /**
     * Return the prepared engine for FOR
     * 
     * @return
     */
    public static IEngineFor getEngineFor() {
        return engineFor;
    }

    /**
     * Return the prepared engine for functions
     * 
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

    /**
     * Return the rule that is currently getting executed
     * 
     * @return
     */
    public static String getRunningRule() {
        return runningRule;
    }

    /**
     * Set the rule that is currently getting execute
     * 
     * @param runningRule
     */
    public static void setRunningRule(String runningRule) {
        EngineFactory.runningRule = runningRule;
    }

    /**
     * Get the output file path
     * 
     * @return
     */
    public static String getOutputPath() {
        return outputPath;
    }

    /**
     * Set the Output file path
     * 
     * @param outputPath
     */
    public static void setOutputPath(String outputPath) {
        EngineFactory.outputPath = outputPath;
    }

    /**
     * Get the Log file path
     * 
     * @return
     */
    public static String getLogPath() {
        return logPath;
    }

    /**
     * Set the Log file path
     * 
     * @param logPath
     */
    public static void setLogPath(String logPath) {
        EngineFactory.logPath = logPath;
    }

    /**
     * Get the commit id of the project version that is being investigated
     * 
     * @return
     */
    public static String getProjectCommitId() {
        return projectCommitId;
    }

    /**
     * Set the commit id of the project version that is being investigated
     * 
     * @param projectCommitId
     */
    public static void setProjectCommitId(String projectCommitId) {
        EngineFactory.projectCommitId = projectCommitId;
    }

    /**
     * Set the engine for version control
     * 
     * @param engineVersionControl
     */
    public static void setEngineVersionControl(IEngineVersionControl engineVersionControl) {
        EngineFactory.engineVersionControl = engineVersionControl;
    }

    /**
     * Get the engine for version control
     * 
     * @return
     */
    public static IEngineVersionControl getEngineVersionControl() {
        return EngineFactory.engineVersionControl;
    }
}
