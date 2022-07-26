package engine;

import parser.ASTDeclStmnt;
import parser.ASTExpression;
import parser.ASTForStmnt;
import parser.ASTIfStmnt;
import parser.ASTStart;
import parser.Eg12;
import parser.Node;
import utils.Constants;

public class EngineMain {
	public static void main(String args[]) {
		IEngineDecl engineDecl = new EngineDecl();
		IEngineFor engineFor = new EngineFor(engineDecl);
		IEngineEvaluate evaluator = new EngineEvaluate(engineDecl);

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
			engineDecl.createFrame();

			int totalChildren = n.jjtGetNumChildren();
			for (int i = 0; i < totalChildren; ++i) {
				Node stmnt = n.jjtGetChild(i);

				switch (stmnt.toString()) {
					case Constants.IF_STMNT:
						ASTIfStmnt ifStmnt = (ASTIfStmnt) stmnt;
						ASTExpression ifExpr = (ASTExpression) ifStmnt.jjtGetChild(0);
						boolean result = evaluator.evalBooleanExpr(ifExpr);
						System.out.println(result);
						break;

					case Constants.DECL_STMNT:
						ASTDeclStmnt declStmnt = (ASTDeclStmnt) stmnt;
						engineDecl.declareVariable(declStmnt);
						break;

					case Constants.FOR_STMNT:
						ASTForStmnt forStmnt = (ASTForStmnt) stmnt;
						System.out.println(forStmnt);
						break;

					case Constants.ASSERT_STMNT:
						break;

				}
			}

			// Eg12Visitor v = new Eg12DumpVisitor();
			// n.jjtAccept(v, null);
			System.out.println("Thank you.");
		} catch (Exception e) {
			System.out.println("Oops.");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
