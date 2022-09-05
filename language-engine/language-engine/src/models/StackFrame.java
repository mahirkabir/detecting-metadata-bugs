package models;

import java.util.HashMap;
import java.util.Map;

public class StackFrame {
    private Map<String, DataResult> mapVariables;
    private Map<String, DataResult> mapFunctions;

    public StackFrame() {
        super();
        this.mapVariables = new HashMap<String, DataResult>();
        this.mapFunctions = new HashMap<String, DataResult>();
    }

    public Map<String, DataResult> getMapVariables() {
        return this.mapVariables;
    }

    public void setMapVariables(Map<String, DataResult> mapVariables) {
        this.mapVariables = mapVariables;
    }

    public Map<String, DataResult> getMapFunctions() {
        return this.mapFunctions;
    }

    public void setMapFunctions(Map<String, DataResult> mapFunctions) {
        this.mapFunctions = mapFunctions;
    }
}
