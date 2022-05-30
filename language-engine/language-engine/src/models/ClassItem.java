package models;

import java.util.List;

public class ClassItem {
	private String shortName;
	private String fqName;
	private List<FieldItem> fields;
	private List<MethodItem> methods;
	private List<String> annotations;
	private List<String> annoAttributes;
	private List<InvocationItem> invocations;
	
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public String getFqName() {
		return fqName;
	}
	public void setFqName(String fqName) {
		this.fqName = fqName;
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
	
	public List<String> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
	}
	
	public List<String> getAnnoAttributes() {
		return annoAttributes;
	}
	public void setAnnoAttributes(List<String> annoAttributes) {
		this.annoAttributes = annoAttributes;
	}
	
	public List<InvocationItem> getInvocations() {
		return invocations;
	}
	public void setInvocations(List<InvocationItem> invocations) {
		this.invocations = invocations;
	}
}