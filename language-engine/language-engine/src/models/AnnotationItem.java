package models;

import java.util.ArrayList;
import java.util.List;

public class AnnotationItem {
    private String className;
    private String annotationName;
    private List<AnnotationAttrItem> annotationAttrs;

    public AnnotationItem() {
        super();
        this.annotationAttrs = new ArrayList<AnnotationAttrItem>();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    public List<AnnotationAttrItem> getAnnotationAttrs() {
        return this.annotationAttrs;
    }

    public void setAnnotationAttrs(List<AnnotationAttrItem> annotationAttrs) {
        this.annotationAttrs = annotationAttrs;
    }
}
