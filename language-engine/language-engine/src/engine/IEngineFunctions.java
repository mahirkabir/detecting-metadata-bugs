package engine;

import models.DataResult;
import parser.ASTFunctionOrId;

public interface IEngineFunctions {
    /**
     * Determine and call function to return result
     * 
     * @param funcNode
     * @return
     */
    DataResult callFunction(ASTFunctionOrId funcNode);

}