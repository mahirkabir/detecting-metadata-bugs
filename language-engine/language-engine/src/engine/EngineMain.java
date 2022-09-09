package engine;

import java.util.ArrayList;

import com.github.javaparser.utils.Pair;

import models.DataResult;
import parser.ASTAssertStmnt;
import parser.ASTDeclStmnt;
import parser.ASTExpression;
import parser.ASTForStmnt;
import parser.ASTFunctionOrId;
import parser.ASTFunctionTail;
import parser.ASTIdentifier;
import parser.ASTIfStmnt;
import parser.ASTSimExp;
import parser.ASTStart;
import parser.ASTType;
import parser.Eg12;
import parser.Node;
import utils.Constants;
import utils.Helper;
import utils.caching.Cache;
import utils.caching.ICache;

public class EngineMain {
	private static IEngineDecl engineDecl;
	private static IEngineEvaluate evaluator;
	private static IEngineFunctions engineFunctions;
	private static ICache cache;

	public static void main(String args[]) {
		String projectPath = "C:\\Mahir\\VT\\Research\\detecting-metadata-bugs\\src\\test";

		engineDecl = new EngineDecl();
		evaluator = new EngineEvaluate(engineDecl);
		cache = new Cache(); // Note: Change here if we change the caching mechanism
		engineFunctions = new EngineFunctions(projectPath, cache, engineDecl);

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
				processNode(stmnt);
			}

			engineDecl.removeFrame();
			System.out.println("Thank you.");
		} catch (

		Exception e) {
			System.out.println("Oops.");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Process a node
	 * 
	 * @param node
	 */
	private static void processNode(Node node) {
		switch (node.toString()) {
			case Constants.IF_STMNT:
				ASTIfStmnt ifStmnt = (ASTIfStmnt) node;
				ASTExpression ifExpr = (ASTExpression) ifStmnt.jjtGetChild(0);
				boolean result = evaluator.evalBooleanExpr(ifExpr);
				System.out.println(result);
				break;

			case Constants.DECL_STMNT:
				ASTDeclStmnt declStmnt = (ASTDeclStmnt) node;
				engineDecl.declareVariable(declStmnt);
				break;

			case Constants.FOR_STMNT:
				ASTForStmnt forStmnt = (ASTForStmnt) node;
				System.out.println("Start: " + forStmnt);
				processFor(forStmnt);
				System.out.println("End: " + forStmnt);
				break;

			case Constants.ASSERT_STMNT:
				ASTAssertStmnt assertStmnt = (ASTAssertStmnt) node;
				System.out.println("Start: " + assertStmnt);
				processAssert(assertStmnt);
				System.out.println("End: " + assertStmnt);
				break;

		}
	}

	/**
	 * Process a for statement
	 * 
	 * @param forStmnt
	 */
	private static void processFor(ASTForStmnt forStmnt) {
		engineDecl.createFrame();
		ASTExpression forExp = (ASTExpression) forStmnt.jjtGetChild(0);
		Pair<ASTType, ASTIdentifier> pIterator = getIterator(forExp);
		ASTExpression forContExp = (ASTExpression) forStmnt.jjtGetChild(1);
		DataResult containerValue = getContainer(forContExp);

		String iteratorType = pIterator.a.getType();
		String iteratorVar = pIterator.b.getIdentifier();
		ArrayList containerList = (ArrayList) containerValue.getResult();
		int totalChildren = forStmnt.jjtGetNumChildren();

		for (Object element : containerList) {
			DataResult iteratorCurrValue = Helper.typeCastValue(iteratorType, element);
			engineDecl.declareVariable(iteratorVar, iteratorCurrValue);

			for (int i = 2; i < totalChildren; ++i) {
				Node forChild = forStmnt.jjtGetChild(i);
				processNode(forChild);
			}
		}
		engineDecl.removeFrame();
	}

	/**
	 * Process an assert statement
	 * 
	 * @param assertStmnt
	 */
	private static void processAssert(ASTAssertStmnt assertStmnt) {
		engineDecl.createFrame();
		ASTExpression assertExp = (ASTExpression) assertStmnt.jjtGetChild(0);
		Node assertExpChild = assertExp.jjtGetChild(0);
		if (assertExpChild.toString().equals(Constants.SIMPLE_EXP)) {
			ASTSimExp assertSimExp = (ASTSimExp) assertExpChild;
			Node firstChild = assertSimExp.jjtGetChild(0);
			if (firstChild.toString().equals(Constants.EXISTS)) {
				ASTExpression iterationExp = (ASTExpression) assertSimExp.jjtGetChild(1);
				Pair<ASTType, ASTIdentifier> pIterator = getIterator(iterationExp);
				ASTExpression containerExp = (ASTExpression) assertSimExp.jjtGetChild(2);
				DataResult containerValue = getContainer(containerExp);

				String iteratorType = pIterator.a.getType();
				String iteratorVar = pIterator.b.getIdentifier();
				ArrayList containerList = (ArrayList) containerValue.getResult();

				boolean assertPass = true;
				for (Object element : containerList) {
					DataResult iteratorCurrValue = Helper.typeCastValue(iteratorType, element);
					engineDecl.declareVariable(iteratorVar, iteratorCurrValue);
					ASTExpression booleanExp = (ASTExpression) assertSimExp.jjtGetChild(3);
					assertPass &= evaluator.evalBooleanExpr(booleanExp);
				}

				if (!assertPass) {
					System.out.println("Assert Failed");
				}
			} else {
				ASTFunctionOrId functionOrId = (ASTFunctionOrId) firstChild;
				if (functionOrId.jjtGetNumChildren() > 1) {
					DataResult assertResult = engineFunctions.callFunction(functionOrId);
					if (assertResult.getType().equals(Constants.TYPE_BOOLEAN)) {
						if (!(Boolean) assertResult.getResult()) {
							System.out.println("TODO: Assert failed");
						}
					}
				}
			}

		} else {
			System.out.println("TODO: Need to implement for assert(conditional expression)");
		}

		engineDecl.removeFrame();
	}

	/**
	 * Get loop container
	 * 
	 * @param containerExp
	 * @return
	 */
	private static DataResult getContainer(ASTExpression containerExp) {
		DataResult containerValue = null;

		ASTSimExp containerSimExp = (ASTSimExp) containerExp.jjtGetChild(0);
		ASTFunctionOrId containerFunctionOrId = (ASTFunctionOrId) containerSimExp.jjtGetChild(0);
		int totalChildren = containerFunctionOrId.jjtGetNumChildren();
		if (totalChildren == 1) {
			// Container is a variable
			ASTIdentifier containerId = (ASTIdentifier) containerFunctionOrId.jjtGetChild(0);
			String containerVarName = containerId.getIdentifier().toString();
			containerValue = engineDecl.extractVariable(containerVarName);
		} else if (totalChildren == 2) {
			// Container is a function call
			containerValue = engineFunctions.callFunction(containerFunctionOrId);
		}

		return containerValue;
	}

	/**
	 * Get loop iterator
	 * 
	 * @param iteratorExp
	 * @return
	 */
	private static Pair<ASTType, ASTIdentifier> getIterator(ASTExpression iteratorExp) {
		ASTSimExp iteratorSimExp = (ASTSimExp) iteratorExp.jjtGetChild(0);
		ASTType iteratorType = (ASTType) iteratorSimExp.jjtGetChild(0);
		ASTIdentifier iteratorId = (ASTIdentifier) iteratorSimExp.jjtGetChild(1);
		return new Pair<ASTType, ASTIdentifier>(iteratorType, iteratorId);
	}
}
