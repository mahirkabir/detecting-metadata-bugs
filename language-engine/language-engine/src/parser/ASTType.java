package parser;

public class ASTType extends SimpleNode {
	private String name;

	/**
	* Constructor.
	* @param id the id
	*/
	public ASTType(int id) {
		super(id);
	}

	/**
	* Set the Type
	* @param n - Type
	*/
	public void setType(String n) {
		name = n;
	}
	
	/**
	* Get the Type
	*/
	public String getType() {
		return name;
	}

	/**
	* {@inheritDoc}
	* @see org.javacc.examples.jjtree.eg2.SimpleNode#toString()
	*/
	public String toString() {
		return "Type: " + name;
	}

	/** Accept the visitor. **/
	public Object jjtAccept(Eg12Visitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
