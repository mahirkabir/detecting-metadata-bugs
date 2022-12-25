package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.DataResult;
import models.StackFrame;
import parser.ASTDeclStmnt;
import parser.ASTFunctionOrId;
import parser.ASTIdentifier;
import parser.ASTLiteral;
import parser.Node;
import utils.Constants;
import utils.Helper;
import utils.Logger;

public class EngineDecl implements IEngineDecl {

    private List<StackFrame> stackFrames;

    public EngineDecl() {
        super();
        this.stackFrames = new ArrayList<StackFrame>();
    }

    @Override
    public void declareVariable(ASTDeclStmnt declNode) {
        // TODO: Need to add boolean literals (Not necessary now)
        // TODO: Need to be able to compare with literals (Not necessary now)
        String varName = ((ASTIdentifier) declNode.jjtGetChild(1)).getIdentifier();
        DataResult result = null;
        Node assignedNode = declNode.jjtGetChild(2);
        if (assignedNode.toString().equals(Constants.FUNCTION_OR_ID))
            result = Helper.getFunctionOrIdValue((ASTFunctionOrId) assignedNode);
        else
            result = Helper.getLiteralValue((ASTLiteral) assignedNode);

        this.updateVariableInMap(varName, result);
    }

    @Override
    public void declareVariable(String varName, DataResult value) {
        this.updateVariableInMap(varName, value);
    }

    private void updateVariableInMap(String varName, DataResult value) {
        StackFrame topFrame = this.stackFrames.get(this.stackFrames.size() - 1);
        Map<String, DataResult> mapVars = topFrame.getMapVariables();
        if (mapVars.containsKey(varName))
            System.out.println(varName + " already existed. Replaced.");
        mapVars.put(varName, value);
        topFrame.setMapVariables(mapVars);
        this.stackFrames.set(this.stackFrames.size() - 1, topFrame);
    }

    /*
     * (non-Javadoc)
     * 
     * @see engine.IEngineDecl#extractVariable(java.lang.String)
     * 
     */
    @Override
    public DataResult extractVariable(String var) {
        DataResult result = null;
        int totalStackFrames = this.stackFrames.size();
        for (int i = totalStackFrames - 1; i >= 0; --i) {
            StackFrame frame = this.stackFrames.get(i);
            Map<String, DataResult> mapVars = frame.getMapVariables();
            if (mapVars.containsKey(var)) {
                result = mapVars.get(var);
                break;
            }
        }

        if (result == null) {
            Logger.log("Could not find variable: " + var);
        }
        return result;
    }

    @Override
    public void createFrame() {
        this.stackFrames.add(new StackFrame());
    }

    @Override
    public void removeFrame() {
        int sz = this.stackFrames.size();
        this.stackFrames.remove(sz - 1);
    }

    @Override
    public void resetFrame() {
        StackFrame topFrame = this.stackFrames.get(this.stackFrames.size() - 1);
        topFrame.setMapVariables(new HashMap<String, DataResult>());
        this.stackFrames.set(this.stackFrames.size() - 1, topFrame);
    }

}
