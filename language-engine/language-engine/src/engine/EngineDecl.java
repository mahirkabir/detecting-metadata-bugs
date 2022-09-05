package engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.DataResult;
import models.StackFrame;
import parser.ASTDeclStmnt;
import parser.ASTFunctionOrId;
import parser.ASTIdentifier;
import parser.ASTLiteral;
import parser.ASTType;
import utils.Constants;
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
        String type = ((ASTType) declNode.jjtGetChild(0)).getType();
        String varName = ((ASTIdentifier) declNode.jjtGetChild(1)).getIdentifier();

        switch (type) {
            case Constants.TYPE_STRING:
                String value = ((ASTLiteral) declNode.jjtGetChild(2)).getLitValue();
                this.updateVariableInMap(varName, new DataResult<String>(type, value));
                break;
            // TODO: Other types
        }
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

    @Override
    public void declareFunction(ASTFunctionOrId funcNode) {

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
    public DataResult extractFunction(String funcName, String[] params) {
        return null;
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

}
