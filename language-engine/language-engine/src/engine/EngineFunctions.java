package engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import models.AnnotationAttrItem;
import models.AnnotationItem;
import models.BooleanItem;
import models.ClassItem;
import models.DataResult;
import models.FieldItem;
import models.IntegerItem;
import models.InvocationItem;
import models.JItem;
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
            // this.cache.saveClasses(classItems);
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

    private DataResult<List<AnnotationItem>> getAnnotations(ClassItem c) {
        DataResult<List<AnnotationItem>> result = new DataResult<List<AnnotationItem>>(
                Constants.TYPE_ANNOTATION_LIST,
                this.classHelper.getAnnotations(c.getFqn()));
        return result;
    }

    private DataResult<StringItem> getArg(ClassItem c, String methodName, int argIdx) {
        String functionCall = "getArg()" + "||" + c.getFqn() + "||" + methodName + "||" + argIdx;
        DataResult<StringItem> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            result = new DataResult<StringItem>(Constants.TYPE_STRING, new StringItem(""));
            List<InvocationItem> invocationItems = c.getInvocations();
            if (invocationItems == null) {
                DataResult<List<InvocationItem>> invocationResult = new DataResult<List<InvocationItem>>(
                        Constants.TYPE_INVOCATION_LIST,
                        this.classHelper.getInvocations(c.getFqn()));
                invocationItems = invocationResult.getResult();
            }

            for (InvocationItem invocationItem : invocationItems) {
                if (invocationItem.getInvocationLine().strip().startsWith(methodName + "(")) {
                    String arg = invocationItem.getArguments().get(argIdx);
                    if (arg.startsWith("\""))
                        arg = arg.substring(1, arg.length() - 1);
                    result.setResult(new StringItem(arg));
                    break;
                }
            }

            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    private DataResult<BooleanItem> callExists(ClassItem c, String invocation) {
        String basicFunction = "callExists()" + "||" + c.getFqn();
        String functionCall = basicFunction + "||" + invocation;
        DataResult<BooleanItem> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            result = new DataResult<BooleanItem>(
                    Constants.TYPE_BOOLEAN, new BooleanItem(false));
            DataResult<BooleanItem> trueRes = new DataResult<BooleanItem>(
                    Constants.TYPE_BOOLEAN, new BooleanItem(true));

            DataResult<List<InvocationItem>> invocationResult = new DataResult<List<InvocationItem>>(
                    Constants.TYPE_INVOCATION_LIST,
                    this.classHelper.getInvocations(c.getFqn()));

            for (InvocationItem invocationItem : invocationResult.getResult()) {
                String invStmnt = invocationItem.getInvocationLine();
                String invMethod = invStmnt.split("(\\()")[0];
                if (invMethod.strip().equals(invocation))
                    result = trueRes;
                cache.addFunctionCall(basicFunction + "||" + invMethod, trueRes);
            }

            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    private DataResult<StringItem> getFQN(ClassItem c) {
        return new DataResult<StringItem>(Constants.TYPE_STRING, new StringItem(c.getFqn()));
    }

    private DataResult<List<StringItem>> getAnnoAttr(ClassItem c, String annotation, String attr) {
        String functionCall = "getAnnoAttr()" + "||" + c.getFqn() + "||" + annotation + "||" + attr;
        DataResult<List<StringItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            result = new DataResult<List<StringItem>>(
                    Constants.TYPE_STRING_LIST, new ArrayList<StringItem>());
            DataResult<List<AnnotationItem>> annRes = this.getAnnotations(c);

            for (AnnotationItem annItem : annRes.getResult()) {
                List<AnnotationAttrItem> annAttrs = annItem.getAnnotationAttrs();
                for (AnnotationAttrItem annAttrItem : annAttrs) {
                    String functionCurr = "getAnnoAttr()" + "||" + c.getFqn() + "||"
                            + annItem.getAnnotationName() + "||" + annAttrItem.getAnnotationAttrName();
                    DataResult<List<StringItem>> currRes = this.cache.fetchFunctionCall(functionCurr);
                    if (currRes == null)
                        currRes = new DataResult<List<StringItem>>(
                                Constants.TYPE_STRING_LIST, new ArrayList<StringItem>());

                    String annVal = annAttrItem.getAnnotationAttrValue();
                    if (annVal.startsWith("\"") && annVal.endsWith("\""))
                        annVal = annVal.substring(1, annVal.length() - 1);
                    currRes.getResult().add(new StringItem(annVal));
                    cache.addFunctionCall(functionCurr, currRes);
                    if (annItem.getAnnotationName().equals(annotation)
                            && annAttrItem.getAnnotationAttrName().equals(attr))
                        result = currRes;
                }
            }

            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    private DataResult<List<XMLItem>> getElms(XMLItem xml, StringItem selector) {
        String functionCall = "getElms()" + "||" + xml.getId() + "||" + selector.getValue();
        DataResult<List<XMLItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            String selectorType = selector.getValue();
            selectorType = selectorType.substring(1, selectorType.length() - 1);
            result = new DataResult<List<XMLItem>>(Constants.TYPE_XML_LIST,
                    this.xmlHelper.getElms(xml, selectorType));
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

    private DataResult<StringItem> getName(JItem jItem) {
        return new DataResult<StringItem>(Constants.TYPE_STRING, new StringItem(jItem.getName()));
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

    private DataResult<List<StringItem>> getAnnotated(String annotation, String entityType) {
        String functionCall = "getAnnotated()" + "||" + annotation + "||" + entityType;
        DataResult<List<StringItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            result = new DataResult<List<StringItem>>(
                    Constants.TYPE_STRING_LIST, new ArrayList<StringItem>());
            // TODO: Need to search annotation in methods as well
            if (entityType.equals("*") || entityType.equals(Constants.TYPE_CLASS)) {
                DataResult<List<ClassItem>> cResult = this.getClasses();
                for (ClassItem classItem : cResult.getResult()) {
                    List<AnnotationItem> annotationItems = classItem.getAnnotations();
                    if (annotationItems == null)
                        this.classHelper.getAnnotations(classItem.getFqn());
                    annotationItems = classItem.getAnnotations();
                    if (annotationItems != null) {
                        for (AnnotationItem annItem : annotationItems) {
                            if (annItem.getAnnotationName().equals(annotation)) {
                                result.getResult().add(new StringItem(classItem.getFqn()));
                                break;
                            }
                        }
                    }
                }
            }

            cache.addFunctionCall(functionCall, result);
        }

        return result;
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

    /*
     * (non-Javadoc)
     * 
     * @see engine.IEngineFunctions#callFunction(parser.ASTFunctionOrId)
     */
    @Override
    public DataResult callFunction(ASTFunctionOrId funcNode) {
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
                List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                ClassItem classItem = (ClassItem) params.get(0).getResult();
                result = this.getFields(classItem);
            }
                break;
            case Constants.FUNCTION_GET_METHODS: {
                List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                ClassItem classItem = (ClassItem) params.get(0).getResult();
                result = this.getMethods(classItem);
            }
                break;

            case Constants.FUNCTION_GET_FQN: {
                List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                ClassItem classItem = (ClassItem) params.get(0).getResult();
                result = this.getFQN(classItem); // Consistent
            }
                break;

            case Constants.FUNCTION_GET_NAME: {
                List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                JItem jItem = (JItem) params.get(0).getResult();
                result = this.getName(jItem);
            }
                break;

            case Constants.FUNCTION_CALL_EXISTS: {
                List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                ClassItem classItem = (ClassItem) params.get(0).getResult();
                StringItem invocation = (StringItem) params.get(1).getResult();
                result = this.callExists(classItem, invocation.getValue());
            }
                break;

            case Constants.FUNCTION_GET_ANNO_ATTR: {
                List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                ClassItem classItem = (ClassItem) params.get(0).getResult();
                StringItem anno = (StringItem) params.get(1).getResult();
                StringItem prop = (StringItem) params.get(2).getResult();

                anno.setValue(anno.getValue().substring(1)); // To remove the @
                result = this.getAnnoAttr(classItem, anno.getValue(), prop.getValue());
            }
                break;

            case Constants.FUNCTION_GET_ARG: {
                List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                ClassItem classItem = (ClassItem) params.get(0).getResult();
                StringItem invocation = (StringItem) params.get(1).getResult();
                IntegerItem argIdx = (IntegerItem) params.get(2).getResult();
                result = this.getArg(classItem, invocation.getValue(), argIdx.getValue());
            }
                break;

            case Constants.FUNCTION_GET_ANNOTATED: {
                List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                StringItem annoStr = (StringItem) params.get(0).getResult();
                StringItem entityType = (StringItem) params.get(1).getResult();

                annoStr.setValue(annoStr.getValue().substring(1));
                result = this.getAnnotated(annoStr.getValue(), entityType.getValue());
            }
                break;

            case Constants.FUNCTION_GET_ELMS: {
                List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                XMLItem parentElm = (XMLItem) params.get(0).getResult();
                StringItem selector = (StringItem) params.get(1).getResult();
                result = this.getElms(parentElm, selector);
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
