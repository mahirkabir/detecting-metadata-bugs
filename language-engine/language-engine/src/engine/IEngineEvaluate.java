package engine;

import models.DataResult;
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

    /**
     * Evaluate a simple expression
     * @param simExp
     * @return
     */
    DataResult evalSimExp(ASTSimExp simExp);
}
