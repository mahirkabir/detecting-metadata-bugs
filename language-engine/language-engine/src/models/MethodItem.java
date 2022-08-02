package models;

public class MethodItem extends JItem {
	private String className;
	private String accessModifier;
	private String declType;

	public String getDeclType() {
		return declType;
	}

	public void setDeclType(String declType) {
		this.declType = declType;
	}

	public String getAccessModifier() {
		return accessModifier;
	}

	public void setAccessModifier(String accessModifier) {
		this.accessModifier = accessModifier;
	}

	/**
	 * Get the class name that the method resides in
	 * 
	 * @return
	 */
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
