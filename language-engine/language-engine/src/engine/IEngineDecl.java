package engine;

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
     * 
     * @param declNode
     */
    void declareVariable(ASTDeclStmnt declNode);

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
