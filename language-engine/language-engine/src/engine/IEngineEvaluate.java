package engine;

import models.DataResult;
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
     * @return DataResult object
     */
    DataResult evalSimExp(ASTSimExp simExp);

    /**
     * Evaluate Functions
     * 
     * @param funcExp
     * @return DataResult object
     */
    DataResult evalFunction(ASTFunctionOrId funcExp);

    /**
     * Evaluate Id
     * 
     * @param idExp
     * @return DataResult Object
     */
    DataResult evalId(ASTFunctionOrId idExp);
}
