package _engine;

import parser.ASTForStmnt;
import utils.Logger;

public class EngineFor implements IEngineFor {
    private IEngineDecl engineDecl;

    public EngineFor(IEngineDecl engineDecl) {
        super();
        this.engineDecl = engineDecl;
    }

    @Override
    public void processFor(ASTForStmnt forStmnt) {
        Logger.log("ProcessFor");
    }

}
