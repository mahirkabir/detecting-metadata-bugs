package models;

public class VariableItem extends FieldItem {
    private String parentMethod;

    public String getParentMethod() {
        return parentMethod;
    }

    public void setParentMethod(String parentMethodName) {
        this.parentMethod = parentMethodName;
    }

    public VariableItem() {
        super();
    }
}
