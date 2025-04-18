package engine;

import models.BooleanItem;
import models.DataResult;
import parser.ASTConditionalAndExp;
import parser.ASTConditionalEqExp;
import parser.ASTConditionalOrExp;
import parser.ASTExpression;
import parser.ASTFunctionOrId;
import parser.ASTIdentifier;
import parser.ASTLiteral;
import parser.ASTSimExp;
import parser.Node;
import utils.Constants;
import utils.Helper;
import utils.Logger;

public class EngineEvaluate implements IEngineEvaluate {

    public EngineEvaluate() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see engine.IEngineEvaluate#evalBooleanExpr(parser.ASTExpression)
     * First child can be OR/AND/EQ
     */
    @Override
    public boolean evalBooleanExpr(ASTExpression expr) {
        Logger.log("Expression");
        Node operatorOrIdNode = expr.jjtGetChild(0);
        boolean result = false;
        switch (operatorOrIdNode.toString()) {
            case Constants.CONDITIONAL_OR_EXP:
                result = evalOperator((ASTConditionalOrExp) operatorOrIdNode);
                break;
            case Constants.CONDITIONAL_AND_EXP:
                result = evalOperator((ASTConditionalAndExp) operatorOrIdNode);
                break;
            case Constants.CONDITIONAL_EQ_EXP:
                result = evalOperator((ASTConditionalEqExp) operatorOrIdNode);
                break;
            case Constants.SIMPLE_EXP: {
                BooleanItem currRes = (BooleanItem) evalSimExp((ASTSimExp) operatorOrIdNode).getResult();
                result = currRes.getValue();
            }
                break;
        }
        return result;
    }

    /**
     * Apply OR operator on all child nodes (Or always comes over AND, EQ)
     * 
     * @param orExp
     * @return
     */
    private boolean evalOperator(ASTConditionalOrExp orExp) {
        Logger.log("ConditionalOrExp");
        boolean result = false;
        int totalChildren = orExp.jjtGetNumChildren();
        for (int i = 0; i < totalChildren; ++i) {
            Node childNode = orExp.jjtGetChild(i);
            switch (childNode.toString()) {
                case Constants.CONDITIONAL_AND_EXP:
                    result |= evalOperator((ASTConditionalAndExp) childNode);
                    break;
                case Constants.CONDITIONAL_EQ_EXP:
                    result |= evalOperator((ASTConditionalEqExp) childNode);
                    break;
                case Constants.SIMPLE_EXP: {
                    BooleanItem currRes = (BooleanItem) evalSimExp((ASTSimExp) childNode).getResult();
                    result |= currRes.getValue();
                }
                    break;
            }
        }

        return result;
    }

    /**
     * Apply AND operator on all child nodes (Or always comes over EQ)
     * 
     * @param andExp
     * @return
     */
    private boolean evalOperator(ASTConditionalAndExp andExp) {
        Logger.log("ConditionalAndExp");
        boolean result = true;
        int totalChildren = andExp.jjtGetNumChildren();
        for (int i = 0; i < totalChildren; ++i) {
            Node childNode = andExp.jjtGetChild(i);
            switch (childNode.toString()) {
                case Constants.CONDITIONAL_EQ_EXP:
                    result &= evalOperator((ASTConditionalEqExp) childNode);
                    break;
                case Constants.SIMPLE_EXP: {
                    BooleanItem currRes = (BooleanItem) evalSimExp((ASTSimExp) childNode).getResult();
                    result &= currRes.getValue();
                }
                    break;
            }
        }

        return result;
    }

    /**
     * Apply EQ operator on all child nodes
     * 
     * @param eqExp
     * @return
     */
    private boolean evalOperator(ASTConditionalEqExp eqExp) {
        Logger.log("ConditionalEqExp");
        DataResult firstResult = evalSimExp((ASTSimExp) eqExp.jjtGetChild(0));
        DataResult secondResult = evalSimExp((ASTSimExp) eqExp.jjtGetChild(1));

        if (firstResult == null || secondResult == null)
            return firstResult != secondResult;

        return Helper.isEqual(firstResult, secondResult);
    }

    @Override
    public DataResult evalFunction(ASTFunctionOrId funcExp) {
        Logger.log("Function");
        IEngineFunctions engineFunctions = EngineFactory.getEngineFunctions();
        DataResult result = engineFunctions.callFunction(funcExp);
        return result;
    }

    @Override
    public DataResult evalId(ASTFunctionOrId idExp) {
        Logger.log("Id");
        IEngineDecl engineDecl = EngineFactory.getEngineDecl();
        ASTIdentifier id = ((ASTIdentifier) idExp.jjtGetChild(0));
        DataResult result = engineDecl.extractVariable(id.getIdentifier());
        return result;
    }

    public DataResult evalSimExp(ASTSimExp simExp) {
        Logger.log("SimExp");

        boolean isNot = false;
        while (simExp.jjtGetChild(0).toString().equals(Constants.EXPRESSION_NOT)) {
            // Processing all the Nots and moving on to the actual simple expression
            isNot = !isNot;
            simExp = (ASTSimExp) simExp.jjtGetChild(1);
        }

        DataResult result = null;
        Node child = simExp.jjtGetChild(0);
        if (child.toString().equals(Constants.FUNCTION_OR_ID))
            result = Helper.getFunctionOrIdValue((ASTFunctionOrId) child);
        else if (child.toString().equals(Constants.EXISTS))
            result = Helper.getExistsValue((ASTSimExp) simExp);
        else
            result = Helper.getLiteralValue((ASTLiteral) child);

        if (isNot && result != null && result.getType().equals(Constants.TYPE_BOOLEAN)) {
            BooleanItem currRes = (BooleanItem) result.getResult();
            currRes.setValue(!currRes.getValue());
            result.setResult(currRes);
        }

        return result;
    }

}
