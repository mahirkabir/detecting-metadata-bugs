public class ASTLiteral extends SimpleNode {
	private String value;
	private String type;

	/**
	* Constructor.
	* @param id the id
	*/
	public ASTLiteral(int id) {
		super(id);
	}

	/**
	* Set the Type of the Literal
	* @param n - Type
	*/
	public void setLitType(String n) {
		type = n;
	}

	/**
	* Set the Value of the Literal
	* @param n - Value
	*/
	public void setLitValue(String n) {
		value = n;
	}

	/**
	* {@inheritDoc}
	* @see org.javacc.examples.jjtree.eg2.SimpleNode#toString()
	*/
	public String toString() {
		return type + ": " + value;
	}

	/** Accept the visitor. **/
	public Object jjtAccept(Eg12Visitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
