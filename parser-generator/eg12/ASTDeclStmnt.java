/* Generated By:JJTree: Do not edit this line. ASTDeclStmnt.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTDeclStmnt extends SimpleNode {
  public ASTDeclStmnt(int id) {
    super(id);
  }

  public ASTDeclStmnt(Eg12 p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(Eg12Visitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=4681ec7292a8e237e15dfc073af45dd2 (do not edit this line) */
