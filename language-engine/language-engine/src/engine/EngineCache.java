package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ClassItem;
import models.DataResult;
import utils.Constants;

public class EngineCache implements IEngineCache {
    private Map<String, DataResult> mapResultCache;
    private Map<String, ClassItem> mapClass;

    public EngineCache() {
        super();
        this.mapResultCache = new HashMap<>();
        this.mapClass = new HashMap<>();
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

    @Override
    public void saveClasses(List<ClassItem> classItems) {
        for (ClassItem item : classItems) {
            this.mapClass.put(item.getFqn(), item);
        }
    }

    @Override
    public void editClass(String classFQN, ClassItem classItem) {
        this.mapClass.put(classFQN, classItem);
    }

    @Override
    public DataResult<List<ClassItem>> getClasses() {
        List<ClassItem> classes = new ArrayList<>();
        for (Map.Entry<String, ClassItem> entry : this.mapClass.entrySet())
            classes.add(entry.getValue());
        return new DataResult<List<ClassItem>>(Constants.TYPE_CLASS_LIST, classes);
    }

    @Override
    public DataResult<ClassItem> getClass(String classFQN) {
        ClassItem item = this.mapClass.get(classFQN);
        return new DataResult<ClassItem>(Constants.TYPE_CLASS, item);
    }
}
