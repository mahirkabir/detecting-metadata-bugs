package parser;

/* Copyright (c) 2006, Sun Microsystems, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sun Microsystems, Inc. nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 *  This is an example of how the Visitor pattern might be used to
 *  implement the dumping code that comes with SimpleNode.  It's a bit
 *  long-winded, but it does illustrate a couple of the main points.
 *  <ol>
 *  <li> the visitor can maintain state between the nodes that it visits
 *  (for example the current indentation level).
 *  </li>
 *
 *  <li>if you don't implement a jjtAccept() method for a subclass of
 *  SimpleNode, then SimpleNode's acceptor will get called.
 *  </li>
 *  <li> the utility method childrenAccept() can be useful when
 *  implementing preorder or postorder tree walks.
 *  </li>
 *  </ol>
 *
 */

public class Eg12DumpVisitor implements Eg12Visitor
{
	private int indent = 0;

	private String indentString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < indent; ++i) {
			sb.append(' ');
		}
		return sb.toString();
	}

	public Object visit(SimpleNode node, Object data) {
		System.out.println(indentString() + node +
					   ": acceptor not unimplemented in subclass?");
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTStart node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTForStmnt node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTIfStmnt node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTAssertStmnt node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTDeclStmnt node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTExpression node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTElseStmnt node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTMsgStmnt node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTMsgSuffix node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTFunctionOrId node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTFunctionTail node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTSimExp node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTParams node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTLiteral node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTIdentifier node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	public Object visit(ASTType node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}
	
	public Object visit(ASTExists node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}
	
	public Object visit(ASTNot node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}
	
	public Object visit(ASTFile node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}
	
	public Object visit(ASTClass node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}
	
	public Object visit(ASTMethod node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}
	
	public Object visit(ASTField node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}
	
	public Object visit(ASTString node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}
	
	public Object visit(ASTBool node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}
	
	public Object visit(ASTConditionalOrExp node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}
	
	public Object visit(ASTConditionalAndExp node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}
	
	public Object visit(ASTConditionalEqExp node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}
}

/*end*/
