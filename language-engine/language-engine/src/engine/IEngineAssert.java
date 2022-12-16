package engine;

import parser.ASTAssertStmnt;

public interface IEngineAssert {

    /**
     * Process the assert statement node
     * 
     * @param forStmnt
     */
    void process(ASTAssertStmnt assertStmnt);

}
