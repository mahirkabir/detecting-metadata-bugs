package engine;

import models.DataResult;
import parser.ASTAssertStmnt;
import parser.ASTSimExp;

public interface IEngineAssert {

    /**
     * Process the assert statement node
     * 
     * @param forStmnt
     */
    void process(ASTAssertStmnt assertStmnt);

    /**
     * Get assert exists pass fail value
     * 
     * @param simExp
     * @return
     */
    DataResult getExistsValue(ASTSimExp simExp);
}
