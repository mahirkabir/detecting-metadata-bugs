package models;

import java.util.ArrayList;
import java.util.List;

public class ObjectCreationItem extends JItem {
    private String className;
    private String classFQN;
    private String declType;
    private List<ArgumentItem> arguments;

    public String getDeclType() {
        return declType;
    }

    public void setDeclType(String declType) {
        this.declType = declType;
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

    public List<ArgumentItem> getArguments() {
        return arguments;
    }

    public void addArgument(ArgumentItem argument) {
        if (this.arguments == null)
            this.arguments = new ArrayList<ArgumentItem>();
        this.arguments.add(argument);
    }

    public void setArguments(List<ArgumentItem> arguments) {
        this.arguments = arguments;
    }

    /**
     * Get the class FQN
     */
    public String getClassFQN() {
        return classFQN;
    }

    public void setClassFQN(String classFQN) {
        this.classFQN = classFQN;
    }
}
