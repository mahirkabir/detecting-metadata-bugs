package models;

public class AnnotatedItem {
    private JItem entity;
    private String annotationName;
    private String javaFilePath;

    public JItem getEntity() {
        return entity;
    }

    public void setEntity(JItem entity) {
        this.entity = entity;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    public String getJavaFilePath() {
        return javaFilePath;
    }

    public void setJavaFilePath(String javaFilePath) {
        this.javaFilePath = javaFilePath;
    }
}
