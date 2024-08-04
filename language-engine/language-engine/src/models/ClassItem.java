package models;

import java.util.ArrayList;
import java.util.List;

import utils.Constants;

public class ClassItem extends JItem {
    private String javaFilePath;
    private List<FieldItem> fields;
    private List<MethodItem> methods;
    private List<MethodItem> constructors;

    private List<AnnotationItem> annotations;
    private List<InvocationItem> invocations;

    private List<VariableItem> variables;
    private List<ObjectCreationItem> objectCreations;

    private List<String> imports;

    public ClassItem(String javaFilePath) {
        super();
        super.setType(Constants.TYPE_CLASS);
        this.javaFilePath = javaFilePath;
    }

    public String getFilePath() {
        return this.javaFilePath;
    }

    public List<FieldItem> getFields() {
        return fields;
    }

    public void setFields(List<FieldItem> fields) {
        this.fields = fields;
    }

    public void addField(FieldItem field) {
        if (this.fields == null)
            this.fields = new ArrayList<FieldItem>();
        this.fields.add(field);
    }

    public List<MethodItem> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodItem> methods) {
        this.methods = methods;
    }

    public void addMethod(MethodItem method) {
        if (this.methods == null)
            this.methods = new ArrayList<MethodItem>();
        this.methods.add(method);
    }

    public List<MethodItem> getConstructors() {
        return constructors;
    }

    public void setConstructors(List<MethodItem> constructors) {
        this.constructors = constructors;
    }

    public void addConstructor(MethodItem method) {
        if (this.constructors == null)
            this.constructors = new ArrayList<MethodItem>();
        this.constructors.add(method);
    }

    public List<AnnotationItem> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<AnnotationItem> annotations) {
        this.annotations = annotations;
    }

    public void addAnnotation(AnnotationItem annotation) {
        if (this.annotations == null)
            this.annotations = new ArrayList<AnnotationItem>();
        this.annotations.add(annotation);
    }

    public List<InvocationItem> getInvocations() {
        return invocations;
    }

    public void setInvocations(List<InvocationItem> invocations) {
        this.invocations = invocations;
    }

    public void addInvocation(InvocationItem invocation) {
        if (this.invocations == null)
            this.invocations = new ArrayList<InvocationItem>();
        this.invocations.add(invocation);
    }

    public List<VariableItem> getVariables() {
        return variables;
    }

    public void setVariables(List<VariableItem> variables) {
        this.variables = variables;
    }

    public void addVariable(VariableItem variable) {
        if (this.variables == null)
            this.variables = new ArrayList<VariableItem>();
        this.variables.add(variable);
    }

    /**
     * Get all the object creations in the class
     * 
     * @return
     */
    public List<ObjectCreationItem> getObjectCreations() {
        return objectCreations;
    }

    public void setObjectCreations(List<ObjectCreationItem> objectCreations) {
        this.objectCreations = objectCreations;
    }

    public void addObjectCreation(ObjectCreationItem objectCreationItem) {
        if (this.objectCreations == null)
            this.objectCreations = new ArrayList<ObjectCreationItem>();
        this.objectCreations.add(objectCreationItem);
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public void addImport(String importedClass) {
        if (this.imports == null)
            this.imports = new ArrayList<String>();
        this.imports.add(importedClass);
    }
}