package engine;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.utils.Pair;

import models.BooleanItem;
import models.DataResult;
import models.StringItem;
import parser.ASTAssertStmnt;
import parser.ASTExpression;
import parser.ASTFunctionOrId;
import parser.ASTIdentifier;
import parser.ASTLiteral;
import parser.ASTMsgStmnt;
import parser.ASTMsgSuffix;
import parser.ASTSimExp;
import parser.ASTType;
import parser.Node;
import utils.Constants;
import utils.Helper;

public class EngineAssert implements IEngineAssert {

    private IEngineDecl engineDecl;
    private IEngineEvaluate evaluator;
    private IEngineFunctions engineFunctions;

    public EngineAssert() {
        super();
        this.engineDecl = EngineFactory.getEngineDecl();
        this.evaluator = EngineFactory.getEvaluator();
        this.engineFunctions = EngineFactory.getEngineFunctions();
    }

    @Override
    public void process(ASTAssertStmnt assertStmnt) {
        ASTExpression assertExp = (ASTExpression) assertStmnt.jjtGetChild(0);
        boolean assertPass = this.evaluator.evalBooleanExpr(assertExp);
        if (!assertPass) {
            ASTMsgStmnt msgStmnt = (ASTMsgStmnt) assertStmnt.jjtGetChild(1);
            this.printMsg(msgStmnt);
        }
    }

    @Override
    public DataResult getExistsValue(ASTSimExp assertSimExp) {
        this.engineDecl.createFrame();
        ASTExpression iterationExp = (ASTExpression) assertSimExp.jjtGetChild(1);
        Pair<ASTType, ASTIdentifier> pIterator = Helper.getIterator(iterationExp);
        ASTExpression containerExp = (ASTExpression) assertSimExp.jjtGetChild(2);
        DataResult containerValue = Helper.getContainer(containerExp);

        String iteratorType = pIterator.a.getType();
        String iteratorVar = pIterator.b.getIdentifier();
        ArrayList containerList = (ArrayList) containerValue.getResult();

        boolean assertPass = false;
        for (Object element : containerList) {
            DataResult iteratorCurrValue = Helper.typeCastValue(iteratorType, element);
            this.engineDecl.declareVariable(iteratorVar, iteratorCurrValue);
            ASTExpression booleanExp = (ASTExpression) assertSimExp.jjtGetChild(3);
            assertPass |= this.evaluator.evalBooleanExpr(booleanExp);
            this.engineDecl.resetFrame();
            if (assertPass)
                break;
        }
        this.engineDecl.removeFrame();

        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(assertPass));
    }

    /**
     * Print the assert message
     * 
     * @param msgStmnt
     */
    private void printMsg(ASTMsgStmnt msgStmnt) {
        Node msgNode = msgStmnt.jjtGetChild(0);
        String message = ((ASTLiteral) msgNode).getLitValue(); // This string might have %s. in it
        if (msgStmnt.jjtGetNumChildren() > 1) {
            List<String> formatValues = new ArrayList<String>();
            ASTMsgSuffix msgSuffix = (ASTMsgSuffix) msgStmnt.jjtGetChild(1);
            int numOfMsgSuffix = msgSuffix.jjtGetNumChildren();
            for (int i = 0; i < numOfMsgSuffix; ++i) {
                ASTSimExp suffixExp = (ASTSimExp) msgSuffix.jjtGetChild(i);
                Node child = suffixExp.jjtGetChild(0);
                if (child.toString().equals(Constants.FUNCTION_OR_ID)) {
                    ASTFunctionOrId functionOrId = (ASTFunctionOrId) child;
                    int totalChildren = functionOrId.jjtGetNumChildren();
                    String varName;
                    if (totalChildren == 1) { // Id
                        ASTIdentifier identifier = (ASTIdentifier) functionOrId.jjtGetChild(0);
                        String idenString = identifier.getIdentifier();
                        StringItem varItem = (StringItem) engineDecl.extractVariable(idenString).getResult();
                        varName = varItem.getValue();

                    } else { // Function
                        StringItem varItem = (StringItem) this.engineFunctions.callFunction(functionOrId).getResult();
                        varName = varItem.getValue();
                    }

                    if (varName.startsWith("\"") && varName.endsWith("\""))
                        varName = varName.substring(1, varName.length() - 1);
                    formatValues.add(varName);
                } else {
                    formatValues.add(((ASTLiteral) child).getLitValue());
                }
            }

            message = Helper.formatStr(message, formatValues);
        }

        utils.Logger.output(message);
    }
}
