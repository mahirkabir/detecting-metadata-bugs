package engine;

import models.DataResult;
import parser.ASTDeclStmnt;

public interface IEngineDecl {
    /**
     * Extract the value of the variable
     * 
     * @param var
     * @return
     */
    DataResult extractVariable(String var);

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
     * Clear the iteration frame after the iteration
     */
    void resetFrame();

    /**
     * Create a new frame for the variables and push in the frame-stack
     */
    void createFrame();

    /**
     * Remove the top most frame from the frame-stack
     */
    void removeFrame();
}
