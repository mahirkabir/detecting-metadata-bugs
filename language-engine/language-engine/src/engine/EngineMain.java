package engine;

import parser.ASTExpression;
import parser.ASTIfStmnt;
import parser.ASTStart;
import parser.Eg12;

public class EngineMain {
	public static void main(String args[]) {
		IEngineEvaluate evaluator = new EngineEvaluate();

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

			ASTIfStmnt ifStmnt = (ASTIfStmnt) n.jjtGetChild(1);
			ASTExpression ifExpr = (ASTExpression) ifStmnt.jjtGetChild(0);
			boolean result = evaluator.evalBooleanExpr(ifExpr);
			System.out.println(result);

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
