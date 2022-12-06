package engine;

import java.util.ArrayList;

import com.github.javaparser.utils.Pair;

import models.DataResult;
import parser.ASTExpression;
import parser.ASTForStmnt;
import parser.ASTIdentifier;
import parser.ASTType;
import parser.Node;
import utils.Helper;

public class EngineFor implements IEngineFor {
    private IEngineDecl engineDecl;

    public EngineFor() {
        super();
        this.engineDecl = EngineFactory.getEngineDecl();
    }


    @Override
    public void process(ASTForStmnt forStmnt) {
        this.engineDecl.createFrame();
        ASTExpression forExp = (ASTExpression) forStmnt.jjtGetChild(0);
        Pair<ASTType, ASTIdentifier> pIterator = Helper.getIterator(forExp);
        ASTExpression forContExp = (ASTExpression) forStmnt.jjtGetChild(1);
        DataResult containerValue = Helper.getContainer(forContExp);

        String iteratorType = pIterator.a.getType();
        String iteratorVar = pIterator.b.getIdentifier();
        ArrayList containerList = (ArrayList) containerValue.getResult();
        int totalChildren = forStmnt.jjtGetNumChildren();

        for (Object element : containerList) {
            DataResult iteratorCurrValue = Helper.typeCastValue(iteratorType, element);
            engineDecl.declareVariable(iteratorVar, iteratorCurrValue);

            for (int i = 2; i < totalChildren; ++i) {
                Node forChild = forStmnt.jjtGetChild(i);
                Helper.process(forChild);
            }
        }
        engineDecl.removeFrame();
    }

}
