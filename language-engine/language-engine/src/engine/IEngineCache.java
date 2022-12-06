package engine;

import models.DataResult;

public interface IEngineCache {
    /**
     * Add result of the function call in cache
     * 
     * @param functionCall
     * @param result
     */
    void addFunctionCall(String functionCall, DataResult result);

    /**
     * Fetch result of the function call from the cache
     * 
     * @param functionCall
     * @return
     */
    DataResult fetchFunctionCall(String functionCall);
}
