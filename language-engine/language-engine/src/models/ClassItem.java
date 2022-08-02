package models;

import java.util.List;

public class ClassItem extends JItem {
	private List<FieldItem> fields;
	private List<MethodItem> methods;
	private List<AnnotationItem> annotations;
	private List<InvocationItem> invocations;

	public List<FieldItem> getFields() {
		return fields;
	}

	public void setFields(List<FieldItem> fields) {
		this.fields = fields;
	}

	public List<MethodItem> getMethods() {
		return methods;
	}

	public void setMethods(List<MethodItem> methods) {
		this.methods = methods;
	}

	public List<AnnotationItem> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<AnnotationItem> annotations) {
		this.annotations = annotations;
	}

	public List<InvocationItem> getInvocations() {
		return invocations;
	}

	public void setInvocations(List<InvocationItem> invocations) {
		this.invocations = invocations;
	}
}