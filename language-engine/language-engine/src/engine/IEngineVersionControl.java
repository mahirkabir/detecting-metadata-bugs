package engine;

public interface IEngineVersionControl {
    /**
     * Check if filepath is tracked by version control
     * 
     * @param filepath
     * @return
     */
    Boolean isTrackedFile(String filepath);
}
