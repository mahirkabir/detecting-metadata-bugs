package engine;

import java.util.List;

import models.ClassItem;
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

    /**
     * Save classes in cache
     */
    void saveClasses(List<ClassItem> classItems);

    /**
     * Edit info of class in cache
     * 
     * @param classFQN
     * @param classItem
     */
    void editClass(String classFQN, ClassItem classItem);

    /**
     * Get all classes from cache
     * 
     * @return
     */
    DataResult<List<ClassItem>> getClasses();

    /**
     * Get class info from cache
     * 
     * @param classFQN
     * @return
     */
    DataResult<ClassItem> getClass(String classFQN);
}
