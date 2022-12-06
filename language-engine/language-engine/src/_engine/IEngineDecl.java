package _engine;

import models.DataResult;
import parser.ASTDeclStmnt;
import parser.ASTFunctionOrId;

public interface IEngineDecl {
    /**
     * Extract the value of the variable
     * 
     * @param var
     * @return
     */
    DataResult extractVariable(String var);

    /**
     * Extract the value of the function with the params as arguments
     * 
     * @param funcName
     * @param params
     * @return
     */
    DataResult extractFunction(String funcName, String[] params);

    /**
     * Declare variable in the top most frame in the frame-stack
     * Using AST class
     * 
     * @param declNode
     */
    void declareVariable(ASTDeclStmnt declNode);

    /**
     * Declare variable in the top most frame in the frame-stack
     * Using variable name and value
     * 
     * @param varName
     * @param value
     */
    void declareVariable(String varName, DataResult value);

    /**
     * Declare function in the top most frame in the frame-stack
     * 
     * @param funcNode
     */
    void declareFunction(ASTFunctionOrId funcNode);

    /**
     * Create a new frame for the variables and push in the frame-stack
     */
    void createFrame();

    /**
     * Remove the top most frame from the frame-stack
     */
    void removeFrame();
}
