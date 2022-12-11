package engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import models.BooleanItem;
import models.ClassItem;
import models.DataResult;
import models.FieldItem;
import models.InvocationItem;
import models.MethodItem;
import models.StringItem;
import models.XMLItem;
import parser.ASTFunctionOrId;
import parser.ASTFunctionTail;
import parser.ASTIdentifier;
import parser.ASTParams;
import parser.ASTSimExp;
import utils.ClassHelper;
import utils.Constants;
import utils.XMLHelper;

public class EngineFunctions implements IEngineFunctions {
    private String projectPath;
    private ClassHelper classHelper;
    private XMLHelper xmlHelper;
    private IEngineCache cache;

    public EngineFunctions() {
        super();
        this.projectPath = EngineFactory.getProjectPath();
        this.classHelper = new ClassHelper(this.projectPath);
        this.xmlHelper = new XMLHelper(this.projectPath);
        this.cache = EngineFactory.getEngineCache();
    }

    private DataResult<List<ClassItem>> getClasses() {
        String functionCall = "getClasses()";
        DataResult<List<ClassItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            List<ClassItem> classItems = this.classHelper.getClasses();
            result = new DataResult<List<ClassItem>>(Constants.TYPE_CLASS_LIST, classItems);
            this.cache.saveClasses(classItems);
            this.cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    private DataResult<List<XMLItem>> getXMLs() {
        String functionCall = "getXMLs()";
        DataResult<List<XMLItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            try {
                result = new DataResult<List<XMLItem>>(Constants.TYPE_XML_LIST, this.xmlHelper.getXMLs());
                cache.addFunctionCall(functionCall, result);
            } catch (SAXException | IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private DataResult<StringItem> getArg(ClassItem c, String methodName, int argIdx) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataResult<BooleanItem> callExists(ClassItem c, String methodName) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataResult<String> getFQN(ClassItem c) {
        return new DataResult<String>(Constants.TYPE_STRING, c.getFqn());
    }

    private DataResult<List<String>> getAnnoAttr(ClassItem c, String annotation, String attr) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataResult<List<XMLItem>> getElms(XMLItem xml, String selector) {
        String functionCall = "getElms()" + "||" + xml.getId() + "||" + selector;
        DataResult<List<XMLItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            result = new DataResult<List<XMLItem>>(Constants.TYPE_XML_LIST, this.xmlHelper.getElms(xml, selector));
            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    private DataResult<StringItem> getAttr(XMLItem node, String attrName) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataResult<BooleanItem> elementExists(XMLItem xml, String selector) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataResult<StringItem> getName(MethodItem m) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataResult<BooleanItem> pathExists(String path) {
        path = path.substring(1, path.length() - 1);
        BooleanItem booleanItem = new BooleanItem(Files.exists(Paths.get(path)));
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, booleanItem);
    }

    private DataResult<StringItem> subString(String str, int st) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataResult<StringItem> subString(String str, int st, int en) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataResult<StringItem> upperCase(String str) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataResult<List<String>> getAnnotated(String annotation, String entityType) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataResult<List<String>> join(List<String> lists) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataResult<BooleanItem> isEmpty(List<String> list) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataResult<BooleanItem> endsWith(String str, String suffix) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataResult<StringItem> getSN(String className) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataResult<List<FieldItem>> getFields(ClassItem c) {
        String functionCall = "getFields()" + "||" + c.getFqn();
        DataResult<List<FieldItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            result = new DataResult<List<FieldItem>>(Constants.TYPE_FIELD_LIST, this.classHelper.getFields(c.getFqn()));
            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    private DataResult<List<MethodItem>> getMethods(ClassItem c) {
        String functionCall = "getMethods()" + "||" + c.getFqn();
        DataResult<List<MethodItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            result = new DataResult<List<MethodItem>>(Constants.TYPE_METHOD_LIST,
                    this.classHelper.getMethods(c.getFqn()));
            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    private DataResult<List<InvocationItem>> getInvocations(ClassItem c) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see engine.IEngineFunctions#callFunction(parser.ASTFunctionOrId)
     */
    @Override
    public DataResult callFunction(ASTFunctionOrId funcNode) {
        IEngineDecl engineDecl = EngineFactory.getEngineDecl();
        DataResult result = null;
        ASTIdentifier name = (ASTIdentifier) funcNode.jjtGetChild(0);
        switch (name.getIdentifier()) {
            case Constants.FUNCTION_GET_CLASSES:
                result = this.getClasses();
                break;
            case Constants.FUNCTION_GET_XMLS:
                result = this.getXMLs();
                break;
            case Constants.FUNCTION_PATH_EXISTS: {
                List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                result = this.pathExists((String) params.get(0).getResult());
            }
                break;
            case Constants.FUNCTION_GET_FIELDS: {
                ASTFunctionTail tail = (ASTFunctionTail) funcNode.jjtGetChild(1);
                ASTParams params = (ASTParams) tail.jjtGetChild(0);
                ASTSimExp simExp = (ASTSimExp) params.jjtGetChild(0);
                ASTFunctionOrId id = (ASTFunctionOrId) simExp.jjtGetChild(0);
                String classVar = ((ASTIdentifier) id.jjtGetChild(0)).getIdentifier();
                DataResult<ClassItem> classItem = engineDecl.extractVariable(classVar);
                result = this.getFields(classItem.getResult());
            }
                break;
            case Constants.FUNCTION_GET_METHODS: {
                ASTFunctionTail tail = (ASTFunctionTail) funcNode.jjtGetChild(1);
                ASTParams params = (ASTParams) tail.jjtGetChild(0);
                ASTSimExp simExp = (ASTSimExp) params.jjtGetChild(0);
                ASTFunctionOrId id = (ASTFunctionOrId) simExp.jjtGetChild(0);
                String classVar = ((ASTIdentifier) id.jjtGetChild(0)).getIdentifier();
                DataResult<ClassItem> classItem = engineDecl.extractVariable(classVar);
                result = this.getMethods(classItem.getResult());
            }
                break;

            case Constants.FUNCTION_GET_FQN: {
                List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                result = this.getFQN((ClassItem) params.get(0).getResult()); // Consistent
            }

                break;
        }
        return result;
    }

    /**
     * Prepares the params list from the functionTail node
     * 
     * @param jjtGetChild
     * @return
     */
    private List<DataResult> getParams(ASTFunctionTail functionTail) {
        List<DataResult> params = new ArrayList<>();
        ASTParams paramsAST = (ASTParams) functionTail.jjtGetChild(0);
        int totParams = paramsAST.jjtGetNumChildren();
        for (int i = 0; i < totParams; ++i) {
            ASTSimExp paramSimExp = (ASTSimExp) paramsAST.jjtGetChild(i);
            IEngineEvaluate evaluate = EngineFactory.getEvaluator();
            params.add(evaluate.evalSimExp(paramSimExp));
        }
        return params;
    }

}
