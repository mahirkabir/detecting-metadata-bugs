package engine;

import models.EvalResult;
import parser.ASTConditionalAndExp;
import parser.ASTConditionalEqExp;
import parser.ASTConditionalOrExp;
import parser.ASTExpression;
import parser.ASTFunctionOrId;
import parser.ASTSimExp;

public interface IEngineEvaluate {
    /**
     * Evaulate Boolean Expression
     * 
     * @param expr
     * @return true or false
     */
    boolean evalBooleanExpr(ASTExpression expr);

    /**
     * Evaluate Or Expression
     * 
     * @param orExp
     * @return true or false
     */
    boolean evalOperator(ASTConditionalOrExp orExp);

    /**
     * Evaluate And Expression
     * 
     * @param andExp
     * @return true or false
     */
    boolean evalOperator(ASTConditionalAndExp andExp);

    /**
     * Evaluate Eq Expression
     * 
     * @param eqExp
     * @return true or false
     */
    boolean evalOperator(ASTConditionalEqExp eqExp);

    /**
     * Evaluate Simple Expression
     * 
     * @param simExp
     * @return EvalResult object
     */
    EvalResult evalSimExp(ASTSimExp simExp);

    /**
     * Evaluate Functions
     * 
     * @param funcExp
     * @return EvalResult object
     */
    EvalResult evalFunction(ASTFunctionOrId funcExp);

    /**
     * Evaluate Id
     * 
     * @param idExp
     * @return EvalResult Object
     */
    EvalResult evalId(ASTFunctionOrId idExp);
}
