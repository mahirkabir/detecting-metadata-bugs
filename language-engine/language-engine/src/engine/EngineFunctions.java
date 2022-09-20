package engine;

import java.io.IOException;
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
import utils.caching.ICache;

public class EngineFunctions implements IEngineFunctions {
    private String projectPath;
    private ClassHelper classHelper;
    private XMLHelper xmlHelper;
    private ICache cache;
    private IEngineDecl engineDecl;

    public EngineFunctions(String projectPath, ICache cache, IEngineDecl engineDecl) {
        super();
        this.projectPath = projectPath;
        this.classHelper = new ClassHelper(this.projectPath);
        this.xmlHelper = new XMLHelper(this.projectPath);
        this.cache = cache;
        this.engineDecl = engineDecl;
    }

    @Override
    public DataResult<List<ClassItem>> getClasses() {
        String functionCall = "getClasses()";
        DataResult<List<ClassItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            result = new DataResult<List<ClassItem>>(Constants.TYPE_CLASS_LIST, this.classHelper.getClasses());
            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    @Override
    public DataResult<List<XMLItem>> getXMLs() {
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

    @Override
    public DataResult<StringItem> getArg(ClassItem c, String methodName, int argIdx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<BooleanItem> callExists(ClassItem c, String methodName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<String> getFQN(ClassItem c) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<List<String>> getAnnoAttr(ClassItem c, String annotation, String attr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<List<XMLItem>> getElms(XMLItem xml, String selector) {
        String functionCall = "getElms()" + "||" + xml.getId() + "||" + selector;
        DataResult<List<XMLItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            result = new DataResult<List<XMLItem>>(Constants.TYPE_XML_LIST, this.xmlHelper.getElms(xml, selector));
            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    @Override
    public DataResult<StringItem> getAttr(XMLItem node, String attrName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<BooleanItem> elementExists(XMLItem xml, String selector) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<StringItem> getName(MethodItem m) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<BooleanItem> pathExists(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<StringItem> subString(String str, int st) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<StringItem> subString(String str, int st, int en) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<StringItem> upperCase(String str) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<List<String>> getAnnotated(String annotation, String entityType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<List<String>> join(List<String> lists) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<BooleanItem> isEmpty(List<String> list) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<BooleanItem> endsWith(String str, String suffix) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<StringItem> getSN(String className) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult<List<FieldItem>> getFields(ClassItem c) {
        String functionCall = "getFields()" + "||" + c.getFqn();
        DataResult<List<FieldItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            result = new DataResult<List<FieldItem>>(Constants.TYPE_FIELD_LIST, this.classHelper.getFields(c.getFqn()));
            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    @Override
    public DataResult<List<MethodItem>> getMethods(ClassItem c) {
        String functionCall = "getMethods()" + "||" + c.getFqn();
        DataResult<List<MethodItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            result = new DataResult<List<MethodItem>>(Constants.TYPE_METHOD_LIST, this.classHelper.getMethods(c.getFqn()));
            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    @Override
    public DataResult<List<InvocationItem>> getInvocations(ClassItem c) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataResult callFunction(ASTFunctionOrId funcNode) {
        // TODO: Make other methods private
        DataResult result = null;
        ASTIdentifier name = (ASTIdentifier) funcNode.jjtGetChild(0);
        switch (name.getIdentifier()) {
            case Constants.FUNCTION_GET_CLASSES:
                result = this.getClasses();
                break;
            case Constants.FUNCTION_PATH_EXISTS:
                result = new DataResult<Boolean>(Constants.TYPE_BOOLEAN, false);
                // TODO: Implement
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

        }
        return result;
    }

}
