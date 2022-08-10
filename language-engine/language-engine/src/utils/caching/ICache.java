package utils.caching;

import java.util.List;

import javax.xml.crypto.Data;

import models.BooleanItem;
import models.ClassItem;
import models.DataResult;
import models.FileItem;
import models.StringItem;
import models.XMLItem;

public interface ICache {
    /**
     * Add result of the function call in cache
     * 
     * @param functionCall
     * @param result
     */
    void addFunctionCall(String functionCall, DataResult result);

    /**
     * Fetch result of the function call from the cache
     * @param functionCall
     * @return
     */
    DataResult fetchFunctionCall(String functionCall);
}
