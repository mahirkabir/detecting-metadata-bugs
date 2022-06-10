package models;

import java.util.List;

public class InvocationItem {
	private String caller; // Caller class name
	private String callee;
	private String invocationStmnt;
	private List<String> arguments;

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
}
