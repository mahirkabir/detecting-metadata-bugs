package models;

import java.util.List;

import com.github.javaparser.ast.body.FieldDeclaration;

public class MethodItem {
	private String name;
	private String type;
	private String className;
	private String accessModifier;
	private String declType;

	public String getDeclType() {
		return declType;
	}

	public void setDeclType(String declType) {
		this.declType = declType;
	}

	public MethodItem(String name, String type, String className) {
		super();

		this.name = name;
		this.type = type;
		this.className = className;
	}

	public String getName() {
		return name;
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

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
