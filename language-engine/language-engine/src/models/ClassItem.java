package models;

import java.util.ArrayList;
import java.util.List;

import utils.Constants;

public class ClassItem extends JItem {
	private String javaFilePath;
	private List<FieldItem> fields;
	private List<MethodItem> methods;
	private List<AnnotationItem> annotations;
	private List<InvocationItem> invocations;

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