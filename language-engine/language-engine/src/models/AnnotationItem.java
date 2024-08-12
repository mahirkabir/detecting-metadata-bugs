package models;

import java.util.ArrayList;
import java.util.List;

public class AnnotationItem {
    private String parentEntity;
    private String annotationName;
    private String annotationType;
    private String classFQN;

    /**
     * Check if annotation is on method or class
     * 
     * @return
     */
    public String getAnnotationType() {
        return annotationType;
    }

    /**
     * Set if annotation is method annotation or class annotation
     * 
     * @param annotationType
     */
    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }

    private List<AnnotationAttrItem> annotationAttrs;

    public AnnotationItem() {
        super();
        this.annotationAttrs = new ArrayList<AnnotationAttrItem>();
    }

    public String getParentEntity() {
        return parentEntity;
    }

    public void setParentEntity(String className) {
        this.parentEntity = className;
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

    public void addAnnotationAttr(AnnotationAttrItem annotationAttrItem) {
        if (this.annotationAttrs == null)
            this.annotationAttrs = new ArrayList<>();
        this.annotationAttrs.add(annotationAttrItem);
    }

    /**
     * Get the annotation-class FQN
     */
    public String getClassFQN() {
        return classFQN;
    }

    public void setClassFQN(String classFQN) {
        this.classFQN = classFQN;
    }
}
