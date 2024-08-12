package models;

import java.util.List;

public class InvocationItem {
	private String caller; // Caller class name
	private String callee;
	private String invocationStmnt;
	private List<String> arguments;
	private String className;
    private String classFQN;

	public String getInvocationLine() {
		return invocationStmnt;
	}

	public void setInvocationStmnt(String invocationLine) {
		this.invocationStmnt = invocationLine;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getCallee() {
		return callee;
	}

	public void setCallee(String callee) {
		this.callee = callee;
	}

	/**
	 * Get the class name that the invocation resides in
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
     * Get the class FQN
     */
    public String getClassFQN() {
        return classFQN;
    }

    public void setClassFQN(String classFQN) {
        this.classFQN = classFQN;
    }
}
