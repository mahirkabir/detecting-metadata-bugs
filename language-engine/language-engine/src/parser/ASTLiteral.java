package parser;

public class ASTLiteral extends SimpleNode {
	private String value;
	private String type;

	/**
	 * Constructor.
	 * 
	 * @param id the id
	 */
	public ASTLiteral(int id) {
		super(id);
	}

	/**
	 * Set the Type of the Literal
	 * 
	 * @param n - Type
	 */
	public void setLitType(String n) {
		type = n;
	}

	/**
	 * Get the Type of the Literal
	 */
	public String getLitType() {
		return type;
	}

	/**
	 * Set the Value of the Literal
	 * 
	 * @param n - Value
	 */
	public void setLitValue(String n) {
		/* To remove the " at the start & end from the literal */
		if (n.startsWith("\"") && n.endsWith("\""))
			n = n.substring(1, n.length() - 1);
		value = n;
	}

	/**
	 * Get the Value of the Literal
	 */
	public String getLitValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 * 
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
