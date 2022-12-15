package engine;

import java.util.HashMap;
import java.util.Map;

import models.DataResult;

public class EngineCache implements IEngineCache {
    private Map<String, DataResult> mapResultCache;

    public EngineCache() {
        super();
        this.mapResultCache = new HashMap<>();
    }

    @Override
    public void addFunctionCall(String functionCall, DataResult result) {
        this.mapResultCache.put(functionCall, result);
    }

    @Override
    public DataResult fetchFunctionCall(String functionCall) {
        DataResult result = null;
        if (this.mapResultCache.containsKey(functionCall))
            result = this.mapResultCache.get(functionCall);
        return result;
    }
}
