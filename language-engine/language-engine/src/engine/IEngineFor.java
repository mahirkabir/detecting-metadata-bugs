package engine;

import parser.ASTForStmnt;

public interface IEngineFor {
    /**
     * Process the for loop
     * @param forStmnt
     */
    void processFor(ASTForStmnt forStmnt);

}
