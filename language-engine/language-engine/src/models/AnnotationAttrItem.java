package models;

public class AnnotationAttrItem {
    private String annotationAttrName;
    private String annotationAttrValue;

    public AnnotationAttrItem() {
        super();
    }

    public AnnotationAttrItem(String annKey, String annVal) {
        super();
        this.annotationAttrName = annKey;
        this.annotationAttrValue = annVal;
    }

    public String getAnnotationAttrName() {
        return this.annotationAttrName;
    }

    public void setAnnotationAttrName(String annotationAttrName) {
        this.annotationAttrName = annotationAttrName;
    }

    public String getAnnotationAttrValue() {
        return this.annotationAttrValue;
    }

    public void setAnnotationAttrValue(String annotationAttrValue) {
        this.annotationAttrValue = annotationAttrValue;
    }
}
