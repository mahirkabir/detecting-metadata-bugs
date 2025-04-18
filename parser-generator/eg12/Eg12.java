/* Eg12.java */
/* Generated By:JJTree&JavaCC: Do not edit this line. Eg12.java */
/** An Arithmetic Grammar. */
public class Eg12/*@bgen(jjtree)*/implements Eg12TreeConstants, Eg12Constants {/*@bgen(jjtree)*/
  protected JJTEg12State jjtree = new JJTEg12State();
  /** Main entry point. */
  public static void main(String args[]) {
    Eg12 t;
    System.out.println("Reading from standard input...");
    try {
      t = new Eg12(new java.io.FileInputStream(args[0]));
    } catch (java.io.FileNotFoundException e) {
      System.out.println("Java Parser Version 1.0.2:  File " + args[0] + " not found.");
      return;
    }
    try {
      ASTStart n = t.Start();
      Eg12Visitor v = new Eg12DumpVisitor();
      n.jjtAccept(v, null);
      System.out.println("Thank you.");
    } catch (Exception e) {
      System.out.println("Oops.");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

/** Main production. */
  final public ASTStart Start() throws ParseException {/*@bgen(jjtree) Start */
  ASTStart jjtn000 = new ASTStart(JJTSTART);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(RULE);
      Identifier();
      Block();
      jj_consume_token(0);
jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
{if ("" != null) return jjtn000;}
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
    throw new Error("Missing return statement in function");
}

  final public void Block() throws ParseException {
    jj_consume_token(LBRACE);
    Stmnt();
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case FOR:
    case IF:
    case ASSERT:
    case TYPES:{
      StmntSuffix();
      break;
      }
    default:
      jj_la1[0] = jj_gen;
      ;
    }
    jj_consume_token(RBRACE);
}

  final public void Stmnt() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case FOR:{
      ForStmnt();
      break;
      }
    case IF:{
      IfStmnt();
      break;
      }
    case ASSERT:{
      AssertStmnt();
      break;
      }
    case TYPES:{
      DeclStmnt();
      jj_consume_token(SEMICOLON);
      break;
      }
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
}

  final public void StmntSuffix() throws ParseException {
    Stmnt();
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case FOR:
    case IF:
    case ASSERT:
    case TYPES:{
      StmntSuffix();
      break;
      }
    default:
      jj_la1[2] = jj_gen;
      ;
    }
}

  final public void ForStmnt() throws ParseException {/*@bgen(jjtree) ForStmnt */
  ASTForStmnt jjtn000 = new ASTForStmnt(JJTFORSTMNT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(FOR);
      jj_consume_token(LPAREN);
      Expression();
      jj_consume_token(IN);
      Expression();
      jj_consume_token(RPAREN);
      Block();
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void IfStmnt() throws ParseException {/*@bgen(jjtree) IfStmnt */
  ASTIfStmnt jjtn000 = new ASTIfStmnt(JJTIFSTMNT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(IF);
      jj_consume_token(LPAREN);
      Expression();
      jj_consume_token(RPAREN);
      Block();
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case ELSE:{
        ElseStmnt();
        break;
        }
      default:
        jj_la1[3] = jj_gen;
        ;
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void ElseStmnt() throws ParseException {/*@bgen(jjtree) ElseStmnt */
  ASTElseStmnt jjtn000 = new ASTElseStmnt(JJTELSESTMNT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(ELSE);
      Block();
    } catch (Throwable jjte000) {
if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
}

  final public void AssertStmnt() throws ParseException {/*@bgen(jjtree) AssertStmnt */
  ASTAssertStmnt jjtn000 = new ASTAssertStmnt(JJTASSERTSTMNT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(ASSERT);
      jj_consume_token(LPAREN);
      Expression();
      jj_consume_token(RPAREN);
      jj_consume_token(LBRACE);
      MsgStmnt();
      jj_consume_token(SEMICOLON);
      jj_consume_token(RBRACE);
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void DeclStmnt() throws ParseException {/*@bgen(jjtree) DeclStmnt */
  ASTDeclStmnt jjtn000 = new ASTDeclStmnt(JJTDECLSTMNT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      Type();
      Identifier();
      jj_consume_token(ASSIGN);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case IDENTIFIER:{
        FunctionOrId();
        break;
        }
      case INTEGER_LITERAL:
      case FLOATING_POINT_LITERAL:
      case CHARACTER_LITERAL:
      case STRING_LITERAL:{
        Literal();
        break;
        }
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void Expression() throws ParseException {/*@bgen(jjtree) Expression */
  ASTExpression jjtn000 = new ASTExpression(JJTEXPRESSION);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      ConditionalOrExp();
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void ConditionalOrExp() throws ParseException {/*@bgen(jjtree) #ConditionalOrExp(> 1) */
  ASTConditionalOrExp jjtn000 = new ASTConditionalOrExp(JJTCONDITIONALOREXP);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      ConditionalAndExp();
      label_1:
      while (true) {
        if (jj_2_1(2)) {
          ;
        } else {
          break label_1;
        }
        jj_consume_token(OR);
        ConditionalAndExp();
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, jjtree.nodeArity() > 1);
          }
    }
}

  final public void ConditionalAndExp() throws ParseException {/*@bgen(jjtree) #ConditionalAndExp(> 1) */
  ASTConditionalAndExp jjtn000 = new ASTConditionalAndExp(JJTCONDITIONALANDEXP);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      ConditionalEqExp();
      label_2:
      while (true) {
        if (jj_2_2(2)) {
          ;
        } else {
          break label_2;
        }
        jj_consume_token(AND);
        ConditionalEqExp();
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, jjtree.nodeArity() > 1);
          }
    }
}

  final public void ConditionalEqExp() throws ParseException {/*@bgen(jjtree) #ConditionalEqExp(> 1) */
  ASTConditionalEqExp jjtn000 = new ASTConditionalEqExp(JJTCONDITIONALEQEXP);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      SimExp();
      label_3:
      while (true) {
        if (jj_2_3(2)) {
          ;
        } else {
          break label_3;
        }
        jj_consume_token(EQ);
        SimExp();
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, jjtree.nodeArity() > 1);
          }
    }
}

  final public void MsgStmnt() throws ParseException {/*@bgen(jjtree) MsgStmnt */
  ASTMsgStmnt jjtn000 = new ASTMsgStmnt(JJTMSGSTMNT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(MSG);
      jj_consume_token(LPAREN);
      StringLiteral();
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case COMMA:{
        MsgSuffix();
        break;
        }
      default:
        jj_la1[5] = jj_gen;
        ;
      }
      jj_consume_token(RPAREN);
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void MsgSuffix() throws ParseException {/*@bgen(jjtree) MsgSuffix */
  ASTMsgSuffix jjtn000 = new ASTMsgSuffix(JJTMSGSUFFIX);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(COMMA);
      SimExp();
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case COMMA:{
          ;
          break;
          }
        default:
          jj_la1[6] = jj_gen;
          break label_4;
        }
        jj_consume_token(COMMA);
        SimExp();
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void FunctionOrId() throws ParseException {/*@bgen(jjtree) FunctionOrId */
  ASTFunctionOrId jjtn000 = new ASTFunctionOrId(JJTFUNCTIONORID);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      Identifier();
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case LPAREN:{
        FunctionTail();
        break;
        }
      default:
        jj_la1[7] = jj_gen;
        ;
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void FunctionTail() throws ParseException {/*@bgen(jjtree) FunctionTail */
  ASTFunctionTail jjtn000 = new ASTFunctionTail(JJTFUNCTIONTAIL);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(LPAREN);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case EXISTS:
      case NOT:
      case INTEGER_LITERAL:
      case FLOATING_POINT_LITERAL:
      case CHARACTER_LITERAL:
      case STRING_LITERAL:
      case TYPES:
      case IDENTIFIER:
      case LPAREN:{
        Params();
        break;
        }
      default:
        jj_la1[8] = jj_gen;
        ;
      }
      jj_consume_token(RPAREN);
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void SimExp() throws ParseException {/*@bgen(jjtree) SimExp */
  ASTSimExp jjtn000 = new ASTSimExp(JJTSIMEXP);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case IDENTIFIER:{
        FunctionOrId();
        break;
        }
      case INTEGER_LITERAL:
      case FLOATING_POINT_LITERAL:
      case CHARACTER_LITERAL:
      case STRING_LITERAL:{
        Literal();
        break;
        }
      case TYPES:{
        Type();
        Identifier();
        break;
        }
      case LPAREN:{
        jj_consume_token(LPAREN);
        Expression();
        jj_consume_token(RPAREN);
        break;
        }
      case EXISTS:{
ASTExists jjtn001 = new ASTExists(JJTEXISTS);
            boolean jjtc001 = true;
            jjtree.openNodeScope(jjtn001);
        try {
          jj_consume_token(EXISTS);
        } finally {
if (jjtc001) {
              jjtree.closeNodeScope(jjtn001, true);
            }
        }
        jj_consume_token(LPAREN);
        Expression();
        jj_consume_token(IN);
        Expression();
        jj_consume_token(RPAREN);
        jj_consume_token(LPAREN);
        Expression();
        jj_consume_token(RPAREN);
        break;
        }
      case NOT:{
ASTNot jjtn002 = new ASTNot(JJTNOT);
            boolean jjtc002 = true;
            jjtree.openNodeScope(jjtn002);
        try {
          jj_consume_token(NOT);
        } finally {
if (jjtc002) {
              jjtree.closeNodeScope(jjtn002, true);
            }
        }
        SimExp();
        break;
        }
      default:
        jj_la1[9] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void Params() throws ParseException {/*@bgen(jjtree) Params */
  ASTParams jjtn000 = new ASTParams(JJTPARAMS);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      SimExp();
      label_5:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case COMMA:{
          ;
          break;
          }
        default:
          jj_la1[10] = jj_gen;
          break label_5;
        }
        jj_consume_token(COMMA);
        SimExp();
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void Literal() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case STRING_LITERAL:{
      StringLiteral();
      break;
      }
    case CHARACTER_LITERAL:{
      CharLiteral();
      break;
      }
    case INTEGER_LITERAL:{
      IntLiteral();
      break;
      }
    case FLOATING_POINT_LITERAL:{
      FloatLiteral();
      break;
      }
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
}

  final public void StringLiteral() throws ParseException {/*@bgen(jjtree) Literal */
        ASTLiteral jjtn000 = new ASTLiteral(JJTLITERAL);
        boolean jjtc000 = true;
        jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(STRING_LITERAL);
jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
jjtn000.setLitType("String");
                jjtn000.setLitValue(t.image);
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void CharLiteral() throws ParseException {/*@bgen(jjtree) Literal */
        ASTLiteral jjtn000 = new ASTLiteral(JJTLITERAL);
        boolean jjtc000 = true;
        jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(CHARACTER_LITERAL);
jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
jjtn000.setLitValue(t.image);
                jjtn000.setLitType("Character");
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void IntLiteral() throws ParseException {/*@bgen(jjtree) Literal */
        ASTLiteral jjtn000 = new ASTLiteral(JJTLITERAL);
        boolean jjtc000 = true;
        jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(INTEGER_LITERAL);
jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
jjtn000.setLitValue(t.image);
                jjtn000.setLitType("Integer");
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void FloatLiteral() throws ParseException {/*@bgen(jjtree) Literal */
        ASTLiteral jjtn000 = new ASTLiteral(JJTLITERAL);
        boolean jjtc000 = true;
        jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(FLOATING_POINT_LITERAL);
jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
jjtn000.setLitValue(t.image);
                jjtn000.setLitType("Float");
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void Identifier() throws ParseException {/*@bgen(jjtree) Identifier */
        ASTIdentifier jjtn000 = new ASTIdentifier(JJTIDENTIFIER);
        boolean jjtc000 = true;
        jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(IDENTIFIER);
jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
jjtn000.setIdentifier(t.image);
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void Type() throws ParseException {/*@bgen(jjtree) Type */
        ASTType jjtn000 = new ASTType(JJTTYPE);
        boolean jjtc000 = true;
        jjtree.openNodeScope(jjtn000);Token t;
    try {
      /*t = <FILE>
      	| t = "<" Identifier() ">"
      	| t = <CLASS>
      	| t = <METHOD>
      	| t = <FIELD>
      	| t = <STRING>
      	| t = <BOOL>
      	*/
              t = jj_consume_token(TYPES);
jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
jjtn000.setType(t.image);
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
}

  final public void Types() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case FILE:{
ASTFile jjtn001 = new ASTFile(JJTFILE);
          boolean jjtc001 = true;
          jjtree.openNodeScope(jjtn001);
      try {
        jj_consume_token(FILE);
      } finally {
if (jjtc001) {
            jjtree.closeNodeScope(jjtn001, true);
          }
      }
      break;
      }
    case LT:{
      jj_consume_token(LT);
      Identifier();
      jj_consume_token(GT);
      break;
      }
    case CLASS:{
ASTClass jjtn002 = new ASTClass(JJTCLASS);
            boolean jjtc002 = true;
            jjtree.openNodeScope(jjtn002);
      try {
        jj_consume_token(CLASS);
      } finally {
if (jjtc002) {
              jjtree.closeNodeScope(jjtn002, true);
            }
      }
      break;
      }
    case METHOD:{
ASTMethod jjtn003 = new ASTMethod(JJTMETHOD);
            boolean jjtc003 = true;
            jjtree.openNodeScope(jjtn003);
      try {
        jj_consume_token(METHOD);
      } finally {
if (jjtc003) {
              jjtree.closeNodeScope(jjtn003, true);
            }
      }
      break;
      }
    case FIELD:{
ASTField jjtn004 = new ASTField(JJTFIELD);
            boolean jjtc004 = true;
            jjtree.openNodeScope(jjtn004);
      try {
        jj_consume_token(FIELD);
      } finally {
if (jjtc004) {
              jjtree.closeNodeScope(jjtn004, true);
            }
      }
      break;
      }
    case STRING:{
ASTString jjtn005 = new ASTString(JJTSTRING);
            boolean jjtc005 = true;
            jjtree.openNodeScope(jjtn005);
      try {
        jj_consume_token(STRING);
      } finally {
if (jjtc005) {
              jjtree.closeNodeScope(jjtn005, true);
            }
      }
      break;
      }
    case BOOL:{
ASTBool jjtn006 = new ASTBool(JJTBOOL);
            boolean jjtc006 = true;
            jjtree.openNodeScope(jjtn006);
      try {
        jj_consume_token(BOOL);
      } finally {
if (jjtc006) {
              jjtree.closeNodeScope(jjtn006, true);
            }
      }
      break;
      }
    default:
      jj_la1[12] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
}

  private boolean jj_2_1(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_1()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_2()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_3()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_3R_Literal_361_58_22()
 {
    if (jj_3R_FloatLiteral_405_9_26()) return true;
    return false;
  }

  private boolean jj_3R_ConditionalAndExp_316_9_6()
 {
    if (jj_3R_ConditionalEqExp_321_9_7()) return true;
    return false;
  }

  private boolean jj_3R_Literal_361_9_16()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_Literal_361_9_19()) {
    jj_scanpos = xsp;
    if (jj_3R_Literal_361_27_20()) {
    jj_scanpos = xsp;
    if (jj_3R_Literal_361_43_21()) {
    jj_scanpos = xsp;
    if (jj_3R_Literal_361_58_22()) return true;
    }
    }
    }
    return false;
  }

  private boolean jj_3R_Literal_361_9_19()
 {
    if (jj_3R_StringLiteral_369_9_23()) return true;
    return false;
  }

  private boolean jj_3R_FloatLiteral_405_9_26()
 {
    if (jj_scan_token(FLOATING_POINT_LITERAL)) return true;
    return false;
  }

  private boolean jj_3R_SimExp_352_11_14()
 {
    if (jj_scan_token(NOT)) return true;
    return false;
  }

  private boolean jj_3R_SimExp_351_11_13()
 {
    if (jj_scan_token(EXISTS)) return true;
    return false;
  }

  private boolean jj_3R_SimExp_349_11_12()
 {
    if (jj_scan_token(LPAREN)) return true;
    return false;
  }

  private boolean jj_3R_SimExp_348_11_11()
 {
    if (jj_3R_Type_428_9_17()) return true;
    return false;
  }

  private boolean jj_3R_SimExp_347_11_10()
 {
    if (jj_3R_Literal_361_9_16()) return true;
    return false;
  }

  private boolean jj_3R_Literal_361_43_21()
 {
    if (jj_3R_IntLiteral_393_9_25()) return true;
    return false;
  }

  private boolean jj_3R_IntLiteral_393_9_25()
 {
    if (jj_scan_token(INTEGER_LITERAL)) return true;
    return false;
  }

  private boolean jj_3R_SimExp_346_9_9()
 {
    if (jj_3R_FunctionOrId_336_9_15()) return true;
    return false;
  }

  private boolean jj_3R_SimExp_346_9_8()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_SimExp_346_9_9()) {
    jj_scanpos = xsp;
    if (jj_3R_SimExp_347_11_10()) {
    jj_scanpos = xsp;
    if (jj_3R_SimExp_348_11_11()) {
    jj_scanpos = xsp;
    if (jj_3R_SimExp_349_11_12()) {
    jj_scanpos = xsp;
    if (jj_3R_SimExp_351_11_13()) {
    jj_scanpos = xsp;
    if (jj_3R_SimExp_352_11_14()) return true;
    }
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3_2()
 {
    if (jj_scan_token(AND)) return true;
    if (jj_3R_ConditionalEqExp_321_9_7()) return true;
    return false;
  }

  private boolean jj_3R_FunctionOrId_336_9_15()
 {
    if (jj_3R_Identifier_417_9_18()) return true;
    return false;
  }

  private boolean jj_3R_CharLiteral_381_9_24()
 {
    if (jj_scan_token(CHARACTER_LITERAL)) return true;
    return false;
  }

  private boolean jj_3R_Type_428_9_17()
 {
    if (jj_scan_token(TYPES)) return true;
    return false;
  }

  private boolean jj_3R_Literal_361_27_20()
 {
    if (jj_3R_CharLiteral_381_9_24()) return true;
    return false;
  }

  private boolean jj_3_1()
 {
    if (jj_scan_token(OR)) return true;
    if (jj_3R_ConditionalAndExp_316_9_6()) return true;
    return false;
  }

  private boolean jj_3_3()
 {
    if (jj_scan_token(EQ)) return true;
    if (jj_3R_SimExp_346_9_8()) return true;
    return false;
  }

  private boolean jj_3R_Identifier_417_9_18()
 {
    if (jj_scan_token(IDENTIFIER)) return true;
    return false;
  }

  private boolean jj_3R_StringLiteral_369_9_23()
 {
    if (jj_scan_token(STRING_LITERAL)) return true;
    return false;
  }

  private boolean jj_3R_ConditionalEqExp_321_9_7()
 {
    if (jj_3R_SimExp_346_9_8()) return true;
    return false;
  }

  /** Generated Token Manager. */
  public Eg12TokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[13];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static private int[] jj_la1_2;
  static {
	   jj_la1_init_0();
	   jj_la1_init_1();
	   jj_la1_init_2();
	}
	private static void jj_la1_init_0() {
	   jj_la1_0 = new int[] {0x2000b000,0x2000b000,0x2000b000,0x4000,0x1a200000,0x0,0x0,0x0,0x3a260000,0x3a260000,0x0,0x1a200000,0xc0000000,};
	}
	private static void jj_la1_init_1() {
	   jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x10,0x20000,0x20000,0x400,0x410,0x410,0x20000,0x0,0x20000f,};
	}
	private static void jj_la1_init_2() {
	   jj_la1_2 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,};
	}
  final private JJCalls[] jj_2_rtns = new JJCalls[3];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public Eg12(java.io.InputStream stream) {
	  this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Eg12(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source = new Eg12TokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 13; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
	  ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jjtree.reset();
	 jj_gen = 0;
	 for (int i = 0; i < 13; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public Eg12(java.io.Reader stream) {
	 jj_input_stream = new SimpleCharStream(stream, 1, 1);
	 token_source = new Eg12TokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 13; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
	if (jj_input_stream == null) {
	   jj_input_stream = new SimpleCharStream(stream, 1, 1);
	} else {
	   jj_input_stream.ReInit(stream, 1, 1);
	}
	if (token_source == null) {
 token_source = new Eg12TokenManager(jj_input_stream);
	}

	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jjtree.reset();
	 jj_gen = 0;
	 for (int i = 0; i < 13; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public Eg12(Eg12TokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 13; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(Eg12TokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jjtree.reset();
	 jj_gen = 0;
	 for (int i = 0; i < 13; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
	 Token oldToken;
	 if ((oldToken = token).next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 if (token.kind == kind) {
	   jj_gen++;
	   if (++jj_gc > 100) {
		 jj_gc = 0;
		 for (int i = 0; i < jj_2_rtns.length; i++) {
		   JJCalls c = jj_2_rtns[i];
		   while (c != null) {
			 if (c.gen < jj_gen) c.first = null;
			 c = c.next;
		   }
		 }
	   }
	   return token;
	 }
	 token = oldToken;
	 jj_kind = kind;
	 throw generateParseException();
  }

  @SuppressWarnings("serial")
  static private final class LookaheadSuccess extends java.lang.Error {
    @Override
    public Throwable fillInStackTrace() {
      return this;
    }
  }
  static private final LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
	 if (jj_scanpos == jj_lastpos) {
	   jj_la--;
	   if (jj_scanpos.next == null) {
		 jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
	   } else {
		 jj_lastpos = jj_scanpos = jj_scanpos.next;
	   }
	 } else {
	   jj_scanpos = jj_scanpos.next;
	 }
	 if (jj_rescan) {
	   int i = 0; Token tok = token;
	   while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
	   if (tok != null) jj_add_error_token(kind, i);
	 }
	 if (jj_scanpos.kind != kind) return true;
	 if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
	 return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
	 if (token.next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 jj_gen++;
	 return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
	 Token t = token;
	 for (int i = 0; i < index; i++) {
	   if (t.next != null) t = t.next;
	   else t = t.next = token_source.getNextToken();
	 }
	 return t;
  }

  private int jj_ntk_f() {
	 if ((jj_nt=token.next) == null)
	   return (jj_ntk = (token.next=token_source.getNextToken()).kind);
	 else
	   return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
	 if (pos >= 100) {
		return;
	 }

	 if (pos == jj_endpos + 1) {
	   jj_lasttokens[jj_endpos++] = kind;
	 } else if (jj_endpos != 0) {
	   jj_expentry = new int[jj_endpos];

	   for (int i = 0; i < jj_endpos; i++) {
		 jj_expentry[i] = jj_lasttokens[i];
	   }

	   for (int[] oldentry : jj_expentries) {
		 if (oldentry.length == jj_expentry.length) {
		   boolean isMatched = true;

		   for (int i = 0; i < jj_expentry.length; i++) {
			 if (oldentry[i] != jj_expentry[i]) {
			   isMatched = false;
			   break;
			 }

		   }
		   if (isMatched) {
			 jj_expentries.add(jj_expentry);
			 break;
		   }
		 }
	   }

	   if (pos != 0) {
		 jj_lasttokens[(jj_endpos = pos) - 1] = kind;
	   }
	 }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
	 jj_expentries.clear();
	 boolean[] la1tokens = new boolean[71];
	 if (jj_kind >= 0) {
	   la1tokens[jj_kind] = true;
	   jj_kind = -1;
	 }
	 for (int i = 0; i < 13; i++) {
	   if (jj_la1[i] == jj_gen) {
		 for (int j = 0; j < 32; j++) {
		   if ((jj_la1_0[i] & (1<<j)) != 0) {
			 la1tokens[j] = true;
		   }
		   if ((jj_la1_1[i] & (1<<j)) != 0) {
			 la1tokens[32+j] = true;
		   }
		   if ((jj_la1_2[i] & (1<<j)) != 0) {
			 la1tokens[64+j] = true;
		   }
		 }
	   }
	 }
	 for (int i = 0; i < 71; i++) {
	   if (la1tokens[i]) {
		 jj_expentry = new int[1];
		 jj_expentry[0] = i;
		 jj_expentries.add(jj_expentry);
	   }
	 }
	 jj_endpos = 0;
	 jj_rescan_token();
	 jj_add_error_token(0, 0);
	 int[][] exptokseq = new int[jj_expentries.size()][];
	 for (int i = 0; i < jj_expentries.size(); i++) {
	   exptokseq[i] = jj_expentries.get(i);
	 }
	 return new ParseException(token, exptokseq, tokenImage);
  }

  private boolean trace_enabled;

/** Trace enabled. */
  final public boolean trace_enabled() {
	 return trace_enabled;
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
	 jj_rescan = true;
	 for (int i = 0; i < 3; i++) {
	   try {
		 JJCalls p = jj_2_rtns[i];

		 do {
		   if (p.gen > jj_gen) {
			 jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
			 switch (i) {
			   case 0: jj_3_1(); break;
			   case 1: jj_3_2(); break;
			   case 2: jj_3_3(); break;
			 }
		   }
		   p = p.next;
		 } while (p != null);

		 } catch(LookaheadSuccess ls) { }
	 }
	 jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
	 JJCalls p = jj_2_rtns[index];
	 while (p.gen > jj_gen) {
	   if (p.next == null) { p = p.next = new JJCalls(); break; }
	   p = p.next;
	 }

	 p.gen = jj_gen + xla - jj_la; 
	 p.first = token;
	 p.arg = xla;
  }

  static final class JJCalls {
	 int gen;
	 Token first;
	 int arg;
	 JJCalls next;
  }

}
