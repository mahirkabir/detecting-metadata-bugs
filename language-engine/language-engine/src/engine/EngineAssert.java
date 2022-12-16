package engine;

import java.util.ArrayList;

import com.github.javaparser.utils.Pair;

import models.DataResult;
import parser.ASTAssertStmnt;
import parser.ASTExpression;
import parser.ASTFunctionOrId;
import parser.ASTIdentifier;
import parser.ASTSimExp;
import parser.ASTType;
import parser.Node;
import utils.Constants;
import utils.Helper;

public class EngineAssert implements IEngineAssert {

    private IEngineDecl engineDecl;
    private IEngineEvaluate evaluator;
    private IEngineFunctions engineFunctions;

    public EngineAssert() {
        super();
        this.engineDecl = EngineFactory.getEngineDecl();
        this.evaluator = EngineFactory.getEvaluator();
        this.engineFunctions = EngineFactory.getEngineFunctions();
    }

    @Override
    public void process(ASTAssertStmnt assertStmnt) {
        engineDecl.createFrame();
        ASTExpression assertExp = (ASTExpression) assertStmnt.jjtGetChild(0);
        Node assertExpChild = assertExp.jjtGetChild(0);
        if (assertExpChild.toString().equals(Constants.SIMPLE_EXP)) {
            ASTSimExp assertSimExp = (ASTSimExp) assertExpChild;
            Node firstChild = assertSimExp.jjtGetChild(0);
            if (firstChild.toString().equals(Constants.EXISTS)) {
                ASTExpression iterationExp = (ASTExpression) assertSimExp.jjtGetChild(1);
                Pair<ASTType, ASTIdentifier> pIterator = Helper.getIterator(iterationExp);
                ASTExpression containerExp = (ASTExpression) assertSimExp.jjtGetChild(2);
                DataResult containerValue = Helper.getContainer(containerExp);

                String iteratorType = pIterator.a.getType();
                String iteratorVar = pIterator.b.getIdentifier();
                ArrayList containerList = (ArrayList) containerValue.getResult();

                boolean assertPass = false;
                for (Object element : containerList) {
                    DataResult iteratorCurrValue = Helper.typeCastValue(iteratorType, element);
                    engineDecl.declareVariable(iteratorVar, iteratorCurrValue);
                    ASTExpression booleanExp = (ASTExpression) assertSimExp.jjtGetChild(3);
                    assertPass |= this.evaluator.evalBooleanExpr(booleanExp);
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

}
