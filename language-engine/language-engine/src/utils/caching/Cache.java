package utils.caching;

import java.util.HashMap;
import java.util.Map;

import models.DataResult;

public class Cache implements ICache {

    private Map<String, DataResult> mapResultCache;

    public Cache() {
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
