package models;

import java.util.List;

import utils.Constants;

public class ClassItem extends JItem {
	private String javaFilename;
	private List<FieldItem> fields;
	private List<MethodItem> methods;
	private List<AnnotationItem> annotations;
	private List<InvocationItem> invocations;

	public ClassItem(String javaFilename) {
		super();
		super.setType(Constants.TYPE_CLASS);
		this.javaFilename = javaFilename;
	}

	public String getFilename() {
		return this.javaFilename;
	}

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