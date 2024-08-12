package models;

public class FieldItem extends JItem {
	private String className;
    private String classFQN;
	private String declType;
	private String accessModifier;

	public String getAccessModifier() {
		return accessModifier;
	}

	public void setAccessModifier(String accessModifier) {
		this.accessModifier = accessModifier;
	}

	/**
	 * Check if the variable is static/final
	 * 
	 * @return
	 */
	public String getDeclType() {
		return declType;
	}

	public void setDeclType(String declType) {
		this.declType = declType;
	}

	/**
	 * Get the class name that the variable resides in
	 * 
	 * @return
	 */
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

    /**
     * Get the field-class FQN
     */
    public String getClassFQN() {
        return classFQN;
    }

    public void setClassFQN(String classFQN) {
        this.classFQN = classFQN;
    }
}
