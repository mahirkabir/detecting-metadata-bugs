package engine;

public interface IEngineVersionControl {
    /**
     * Check if filepath is ignored by version control
     * 
     * @param filepath
     * @return
     */
    Boolean isNonIgnoredFile(String filepath);
}
