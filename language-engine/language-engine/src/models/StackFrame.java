package models;

import java.util.HashMap;
import java.util.Map;

public class StackFrame {
    private Map<String, EvalResult> mapVariables;
    private Map<String, EvalResult> mapFunctions;

    public StackFrame() {
        super();
        this.mapVariables = new HashMap<String, EvalResult>();
        this.mapFunctions = new HashMap<String, EvalResult>();
    }

    public Map<String, EvalResult> getMapVariables() {
        return this.mapVariables;
    }

    public void setMapVariables(Map<String, EvalResult> mapVariables) {
        this.mapVariables = mapVariables;
    }

    public Map<String, EvalResult> getMapFunctions() {
        return this.mapFunctions;
    }

    public void setMapFunctions(Map<String, EvalResult> mapFunctions) {
        this.mapFunctions = mapFunctions;
    }
}
