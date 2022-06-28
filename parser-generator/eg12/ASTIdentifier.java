public class ASTIdentifier extends SimpleNode {
	private String name;

	/**
	* Constructor.
	* @param id the id
	*/
	public ASTIdentifier(int id) {
		super(id);
	}

	/**
	* Set the Identifier
	* @param n - Identifier
	*/
	public void setIdentifier(String n) {
		name = n;
	}
	
	/**
	* Get the Identifier
	*/
	public String getIdentifier() {
		return name;
	}

	/**
	* {@inheritDoc}
	* @see org.javacc.examples.jjtree.eg2.SimpleNode#toString()
	*/
	public String toString() {
		return "ID: " + name;
	}

	/** Accept the visitor. **/
	public Object jjtAccept(Eg12Visitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
