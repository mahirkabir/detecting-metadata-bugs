package engine;

import models.EvalResult;
import parser.ASTConditionalAndExp;
import parser.ASTConditionalEqExp;
import parser.ASTConditionalOrExp;
import parser.ASTExpression;
import parser.ASTFunctionOrId;
import parser.ASTSimExp;
import parser.Node;
import utils.Constants;
import utils.Helper;
import utils.Logger;

public class EngineEvaluate implements IEngineEvaluate {

    /*
     * (non-Javadoc)
     * 
     * @see engine.IEngineEvaluate#evalBooleanExpr(parser.ASTExpression)
     * First child can be OR/AND/EQ
     */
    @Override
    public boolean evalBooleanExpr(ASTExpression expr) {
        Logger.log("Expression");
        Node operatorNode = expr.jjtGetChild(0);
        boolean result = false;
        switch (operatorNode.toString()) {
            case Constants.CONDITIONAL_OR_EXP:
                result = evalOperator((ASTConditionalOrExp) operatorNode);
                break;
            case Constants.CONDITIONAL_AND_EXP:
                result = evalOperator((ASTConditionalAndExp) operatorNode);
                break;
            case Constants.CONDITIONAL_EQ_EXP:
                result = evalOperator((ASTConditionalEqExp) operatorNode);
                break;
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see engine.IEngineEvaluate#evalOperator(parser.ASTConditionalOrExp)
     * Apply OR operator on all child nodes (Or always comes over AND, EQ)
     */
    @Override
    public boolean evalOperator(ASTConditionalOrExp orExp) {
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
                case Constants.SIMPLE_EXP:
                    result |= evalSimExp((ASTSimExp) childNode).getResult().equals(Constants.BOOLEAN_TRUE);
                    break;
            }
        }

        return result;
    }

    @Override
    public boolean evalOperator(ASTConditionalAndExp andExp) {
        Logger.log("ConditionalAndExp");
        boolean result = true;
        int totalChildren = andExp.jjtGetNumChildren();
        for (int i = 0; i < totalChildren; ++i) {
            Node childNode = andExp.jjtGetChild(i);
            switch (childNode.toString()) {
                case Constants.CONDITIONAL_EQ_EXP:
                    result &= evalOperator((ASTConditionalEqExp) childNode);
                    break;
                case Constants.SIMPLE_EXP:
                    result &= evalSimExp((ASTSimExp) childNode).getResult().equals(Constants.BOOLEAN_TRUE);
                    break;
            }
        }

        return result;
    }

    @Override
    public boolean evalOperator(ASTConditionalEqExp eqExp) {
        Logger.log("ConditionalEqExp");
        EvalResult firstResult = evalSimExp((ASTSimExp) eqExp.jjtGetChild(0));
        EvalResult secondResult = evalSimExp((ASTSimExp) eqExp.jjtGetChild(1));
        return Helper.isEqual(firstResult, secondResult);
    }

    @Override
    public EvalResult evalFunction(ASTFunctionOrId funcExp) {
        Logger.log("Function");
        return new EvalResult();
    }

    @Override
    public EvalResult evalId(ASTFunctionOrId idExp) {
        Logger.log("Id");
        return new EvalResult();
    }

    @Override
    public EvalResult evalSimExp(ASTSimExp simExp) {
        Logger.log("SimExp");

        boolean isNot = false;
        while (simExp.jjtGetChild(0).toString().equals(Constants.EXPRESSION_NOT)) {
            // Processing all the Nots and moving on to the actual simple expression
            isNot = !isNot;
            simExp = (ASTSimExp) simExp.jjtGetChild(1);
        }

        Node functionOrIdNode = simExp.jjtGetChild(0);
        EvalResult result = new EvalResult();
        switch (functionOrIdNode.jjtGetNumChildren()) {
            case 1:
                // Only one child node exists. FunctionTail is not there. This child is an ID
                result = evalId((ASTFunctionOrId) functionOrIdNode);
                break;
            case 2:
                // Second child is FunctionTail. This child is a function
                result = evalFunction((ASTFunctionOrId) functionOrIdNode);
                break;
        }

        if (isNot && result.getType().equals(Constants.TYPE_BOOLEAN)) {
            if (result.getResult().equals(Constants.BOOLEAN_FALSE))
                result.setResult(Constants.BOOLEAN_TRUE);
            else
                result.setResult(Constants.BOOLEAN_FALSE);
        }

        return result;
    }

}
