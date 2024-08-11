package models;

import java.util.ArrayList;
import java.util.List;

public class MethodItem extends JItem {
    private String className;
    private String classFQN;
    private String accessModifier;
    private String declType;
    private List<AnnotationItem> annotations;
    private List<ParamItem> parameters;

    /**
     * Get the method annotations
     * 
     * @return
     */
    public List<AnnotationItem> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<AnnotationItem> annotations) {
        this.annotations = annotations;
    }

    public String getDeclType() {
        return declType;
    }

    public void setDeclType(String declType) {
        this.declType = declType;
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    /**
     * Get the class name that the method resides in
     * 
     * @return
     */
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Get the method-class FQN
     */
    public String getClassFQN() {
        return classFQN;
    }

    public void setClassFQN(String classFQN) {
        this.classFQN = classFQN;
    }

    public List<ParamItem> getParameters() {
        if (parameters == null)
            return new ArrayList<ParamItem>();
        return parameters;
    }

    public void addParameter(ParamItem param) {
        if (this.parameters == null)
            this.parameters = new ArrayList<ParamItem>();
        this.parameters.add(param);
    }

    public void setParameters(List<ParamItem> parameters) {
        this.parameters = parameters;
    }
}
