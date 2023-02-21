package engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import utils.Helper;
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
            } catch (SAXException | IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }
            cache.addFunctionCall(functionCall, result);
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
            List<MethodItem> methodItems = this.classHelper.getMethods(c.getFqn());
            if (methodItems == null)
                methodItems = new ArrayList<MethodItem>();
            result = new DataResult<List<MethodItem>>(Constants.TYPE_METHOD_LIST,
                    methodItems);
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

    private DataResult<BooleanItem> hasAnnotation(ClassItem c, String annotation) {
        List<AnnotationItem> annotationItems = this.classHelper.getAnnotations(c.getFqn());
        annotation = annotation.replace("@", "");
        boolean hasAnnotation = false;
        if (annotationItems != null) {
            for (AnnotationItem annItem : annotationItems) {
                if (annItem.getAnnotationName().equals(annotation)) {
                    hasAnnotation = true;
                    break;
                }
            }
        }
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(hasAnnotation));
    }

    private DataResult<BooleanItem> hasAnnotation(ClassItem c, MethodItem m, String annotation) {
        annotation = annotation.replace("@", "");
        String basicFunction = "hasAnnotation()" + "||" + c.getFqn();
        String functionCall = basicFunction + "||" + m.getName() + "||" + annotation;
        DataResult<BooleanItem> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            result = new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(false));
            List<MethodItem> methods = this.getMethods(c).getResult();
            if (methods != null) {
                for (MethodItem method : methods) {
                    List<AnnotationItem> aItems = method.getAnnotations();
                    if (aItems != null) {
                        functionCall = basicFunction + "||" + method.getName();
                        for (AnnotationItem aItem : aItems) {
                            String functionCallAttr = functionCall + "||" + aItem.getAnnotationName();
                            DataResult<BooleanItem> currRes = new DataResult<BooleanItem>(
                                    Constants.TYPE_BOOLEAN, new BooleanItem(true));
                            cache.addFunctionCall(functionCallAttr, currRes);

                            if (method.getName().equals(m.getName())
                                    && aItem.getAnnotationName().equals(annotation)) {
                                result = currRes;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Check if annotation of classItem has the attribute - attr
     * 
     * @param classItem
     * @param annotation
     * @param attr
     * @return
     */
    private DataResult hasAnnoAttr(ClassItem classItem, String annotation, String attr) {
        if (annotation.startsWith("@"))
            annotation = annotation.substring(1);
        List<StringItem> annoAttrs = this.getAnnoAttr(classItem, annotation, attr).getResult();
        boolean annoExists = annoAttrs.size() > 0;
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(annoExists));
    }

    private DataResult<List<StringItem>> getArg(ClassItem c, String methodName, int argIdx) {
        String functionCall = "getArg()" + "||" + c.getFqn() + "||" + methodName + "||" + argIdx;
        DataResult<List<StringItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            result = new DataResult<List<StringItem>>(Constants.TYPE_STRING_LIST, new ArrayList<StringItem>());
            List<InvocationItem> invocationItems = c.getInvocations();
            if (invocationItems == null) {
                DataResult<List<InvocationItem>> invocationResult = new DataResult<List<InvocationItem>>(
                        Constants.TYPE_INVOCATION_LIST,
                        this.classHelper.getInvocations(c.getFqn()));
                invocationItems = invocationResult.getResult();
            }

            if (invocationItems != null) {
                for (InvocationItem invocationItem : invocationItems) {
                    if (invocationItem.getInvocationLine().strip().startsWith(methodName + "(")) {
                        String arg = invocationItem.getArguments().get(argIdx);
                        if (arg.startsWith("\""))
                            arg = arg.substring(1, arg.length() - 1);
                        result.getResult().add(new StringItem(arg));
                    }
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

            List<InvocationItem> invocationItems = invocationResult.getResult();
            if (invocationItems != null) {
                for (InvocationItem invocationItem : invocationItems) {
                    String invStmnt = invocationItem.getInvocationLine();
                    String invMethod = invStmnt.split("(\\()")[0];
                    if (invMethod.strip().equals(invocation))
                        result = trueRes;
                    cache.addFunctionCall(basicFunction + "||" + invMethod, trueRes);
                }
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
            List<AnnotationItem> annItems = annRes.getResult();
            if (annItems != null) {
                for (AnnotationItem annItem : annItems) {
                    List<AnnotationAttrItem> annAttrs = annItem.getAnnotationAttrs();
                    if (annAttrs != null) {
                        for (AnnotationAttrItem annAttrItem : annAttrs) {
                            String functionCurr = "getAnnoAttr()" + "||" + c.getFqn() + "||"
                                    + annItem.getAnnotationName() + "||" + annAttrItem.getAnnotationAttrName();
                            DataResult<List<StringItem>> currRes = this.cache.fetchFunctionCall(functionCurr);
                            if (currRes == null)
                                currRes = new DataResult<List<StringItem>>(
                                        Constants.TYPE_STRING_LIST, new ArrayList<StringItem>());

                            String annVal = annAttrItem.getAnnotationAttrValue();
                            if (annVal == null)
                                annVal = ""; // e.g. - @Annotation(CLASSNAME.class)
                            if (annVal.startsWith("\"") && annVal.endsWith("\""))
                                annVal = annVal.substring(1, annVal.length() - 1);
                            currRes.getResult().add(new StringItem(annVal));
                            cache.addFunctionCall(functionCurr, currRes);
                            if (annItem.getAnnotationName().equals(annotation)
                                    && annAttrItem.getAnnotationAttrName().equals(attr))
                                result = currRes;
                        }
                    }
                }
            }

            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    private DataResult<List<StringItem>> getAnnoAttrNames(ClassItem c, String anno) {
        anno = anno.replace("@", "");
        String basicFunction = "getAnnoAttrNames()" + "||" + c.getFqn();
        String functionCall = basicFunction + "||" + anno;
        DataResult<List<StringItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            List<StringItem> attrNames = new ArrayList<>();
            final String annoName = anno;

            List<AnnotationItem> annotationItems = c.getAnnotations();
            if (annotationItems != null) {
                annotationItems.forEach(annItem -> {
                    if (annItem.getAnnotationName().equals(annoName)) {
                        List<AnnotationAttrItem> annAttrItems = annItem.getAnnotationAttrs();
                        if (annAttrItems != null) {
                            annAttrItems.forEach(annAttrItem -> {
                                attrNames.add(new StringItem(annAttrItem.getAnnotationAttrName()));
                            });
                        }
                    }
                });
            }
            result = new DataResult<List<StringItem>>(Constants.TYPE_STRING_LIST, attrNames);
            this.cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    private DataResult<List<XMLItem>> getElms(XMLItem xml, String selector) {
        DataResult<List<XMLItem>> result = new DataResult<List<XMLItem>>(Constants.TYPE_XML_LIST,
                this.xmlHelper.getElms(xml, selector));
        return result;
    }

    private DataResult<StringItem> getAttr(XMLItem node, String attrName) {
        return new DataResult<StringItem>(Constants.TYPE_STRING,
                new StringItem(node.getAttr(attrName)));
    }

    /**
     * Get all -method attributes' values
     * 
     * @param node
     * @param suffix
     * @return
     */
    private DataResult getAttrs(XMLItem node, String suffix) {
        if (suffix.startsWith("*"))
            suffix = suffix.substring(1);
        final String suffString = suffix;

        List<StringItem> attrs = new ArrayList<>();
        node.getAttrMap().forEach((attr, val) -> {
            if (attr.endsWith(suffString))
                attrs.add(new StringItem(val));
        });

        return new DataResult<List<StringItem>>(Constants.TYPE_STRING_LIST, attrs);
    }

    private DataResult<BooleanItem> elementExists(XMLItem xml, String selector) {
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN,
                new BooleanItem(xml.getMapChildTags().containsKey(selector)));
    }

    private DataResult<StringItem> getName(JItem jItem) {
        return new DataResult<StringItem>(Constants.TYPE_STRING, new StringItem(jItem.getName()));
    }

    private DataResult<StringItem> getName(XMLItem xmlItem) {
        return new DataResult<StringItem>(Constants.TYPE_STRING, new StringItem(xmlItem.getId()));
    }

    private DataResult<BooleanItem> pathExists(String path) {
        if (path.startsWith("\"") && path.endsWith("\""))
            path = path.substring(1, path.length() - 1);

        boolean pathExists = Files.exists(Paths.get(path));
        if (!pathExists) {
            Path relativePath = Paths.get(EngineFactory.getProjectPath(), path);
            pathExists = Files.exists(relativePath);
        }

        BooleanItem booleanItem = new BooleanItem(pathExists);
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, booleanItem);
    }

    private DataResult<StringItem> subString(String str, int st) {
        return new DataResult<StringItem>(Constants.TYPE_STRING,
                new StringItem(str.substring(st)));
    }

    private DataResult<StringItem> subString(String str, int st, int en) {
        return new DataResult<StringItem>(Constants.TYPE_STRING,
                new StringItem(str.substring(st, en)));
    }

    private DataResult<StringItem> upperCase(String str) {
        return new DataResult<StringItem>(Constants.TYPE_STRING,
                new StringItem(str.toUpperCase()));
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
                List<ClassItem> classItems = cResult.getResult();
                if (classItems != null) {
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
            }

            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    private DataResult<List> join(List<List> lists) {
        List ret = new ArrayList();
        for (List list : lists) {
            ret = (List) concatList(ret, list);
        }
        return new DataResult<List>(Constants.TYPE_LIST, ret);
    }

    private Object concatList(List ret, List list) {
        return Stream.of(ret, list)
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    private DataResult<BooleanItem> isEmpty(List list) {
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN,
                new BooleanItem(list.size() != 0));
    }

    private DataResult<BooleanItem> isEmpty(String item) {
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN,
                new BooleanItem(item.isEmpty()));
    }

    private DataResult<BooleanItem> endsWith(String str, String suffix) {
        boolean endsWith = str.endsWith(suffix);
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(endsWith));
    }

    private DataResult<IntegerItem> indexOf(String str, String search) {
        return new DataResult<IntegerItem>(Constants.TYPE_INTEGER, new IntegerItem(
                str.indexOf(search)));
    }

    private DataResult<StringItem> getReturnType(MethodItem methodItem) {
        return new DataResult<StringItem>(Constants.TYPE_STRING, new StringItem(methodItem.getType()));
    }

    private DataResult isIterable(StringItem returnType) {
        String ret = returnType.getValue();
        if (ret.contains("<"))
            ret = ret.substring(0, ret.indexOf("<")).strip();
        boolean isIterable = Helper.isIterable(ret);
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(isIterable));
    }

    /**
     * Check if only one class has the classSN
     * 
     * @param classSN
     * @return
     */
    private DataResult<BooleanItem> isUnique(String classSN) {
        classSN = classSN.replace(".class", "");
        Map<String, List<ClassItem>> dictSNClass = this.classHelper.getClassSNDict();
        boolean isUnique = dictSNClass.containsKey(classSN)
                && dictSNClass.get(classSN).size() == 1;

        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(isUnique));
    }

    /**
     * Locate class based on Short Name
     * (If multiple class exists, return the first one)
     * 
     * @param classSN
     * @return
     */
    private DataResult<ClassItem> locateClass(String classSN) {
        classSN = classSN.replace(".class", "");
        ClassItem ret = new ClassItem("");
        if (this.classHelper.getClassSNDict().containsKey(classSN))
            ret = this.classHelper.getClassSNDict().get(classSN).get(0);
        return new DataResult<ClassItem>(Constants.TYPE_CLASS, ret);
    }

    /**
     * Check if a class exists using the fully qualified name
     * 
     * @param classFQN
     * @return
     */
    private DataResult<BooleanItem> classExists(String classFQN) {
        String basicFunction = "classExists()";
        String functionCall = basicFunction + "||" + classFQN;
        DataResult<BooleanItem> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            DataResult<List<ClassItem>> classesDT = this.getClasses();
            List<ClassItem> classes = classesDT.getResult();
            Boolean found = false;
            for (ClassItem classItem : classes) {
                functionCall = basicFunction + "||" + classItem.getFqn();
                DataResult<BooleanItem> currResult = new DataResult<BooleanItem>(
                        Constants.TYPE_BOOLEAN, new BooleanItem(true));
                this.cache.addFunctionCall(functionCall, currResult);
                if (classItem.getFqn().equals(classFQN))
                    found = true;
            }
            result = new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(found));
        }

        return result;
    }

    /**
     * Check if there are multiple beans for the same class
     * 
     * @param xmlItem
     * @return
     */
    private DataResult<BooleanItem> hasDuplicateBeans(XMLItem xmlItem) {
        List<XMLItem> beans = xmlItem.getChildNodes(Constants.NODE_TYPE_BEAN);

        Map<String, List<XMLItem>> mapClassBeans = new HashMap<>();
        for (XMLItem bean : beans) {
            String className = bean.getAttr(Constants.TYPE_CLASS);
            if (!className.isEmpty()) {
                if (!mapClassBeans.containsKey(className))
                    mapClassBeans.put(className, new ArrayList<XMLItem>());
                mapClassBeans.get(className).add(bean);
            }
        }

        boolean dupFound = false;
        for (Map.Entry<String, List<XMLItem>> entry : mapClassBeans.entrySet()) {
            List<XMLItem> beansForClass = entry.getValue();
            if (beansForClass.size() > 1) {
                boolean primaryFound = false;
                for (XMLItem beanItem : beansForClass) {
                    if (beanItem.getAttr(Constants.ATTR_PRIMARY).equals(Constants.BOOLEAN_TRUE)) {
                        primaryFound = true;
                        break;
                    }
                }
                if (!primaryFound) {
                    dupFound = true;
                    break;
                }
            }
        }

        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(dupFound));
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

        try {
            switch (name.getIdentifier()) {
                case Constants.FUNCTION_GET_CLASSES:
                    result = this.getClasses();
                    break;
                case Constants.FUNCTION_GET_XMLS:
                    result = this.getXMLs();
                    break;
                case Constants.FUNCTION_PATH_EXISTS: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    StringItem pathItem = (StringItem) params.get(0).getResult();
                    result = this.pathExists((String) pathItem.getValue());
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
                    Object item = params.get(0).getResult();
                    if (item instanceof JItem)
                        result = this.getName((JItem) item);
                    else if (item instanceof XMLItem)
                        result = this.getName((XMLItem) item);
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
                    result = this.getElms(parentElm, selector.getValue());
                }
                    break;

                case Constants.FUNCTION_GET_ATTR: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    XMLItem parentElm = (XMLItem) params.get(0).getResult();
                    StringItem attr = (StringItem) params.get(1).getResult();
                    result = this.getAttr(parentElm, attr.getValue());
                }
                    break;

                case Constants.FUNCTION_GET_ATTRS: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    XMLItem parentElm = (XMLItem) params.get(0).getResult();
                    StringItem attrSuffix = (StringItem) params.get(1).getResult();
                    result = this.getAttrs(parentElm, attrSuffix.getValue());
                }
                    break;

                case Constants.FUNCTION_ELEMENT_EXISTS: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    XMLItem parentElm = (XMLItem) params.get(0).getResult();
                    StringItem selector = (StringItem) params.get(1).getResult();

                    selector.setValue(selector.getValue().substring(1,
                            selector.getValue().length() - 1));
                    result = this.elementExists(parentElm, selector.getValue());
                }
                    break;

                case Constants.FUNCTION_SUBSTRING: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    StringItem str = (StringItem) params.get(0).getResult();
                    IntegerItem st = (IntegerItem) params.get(1).getResult();
                    if (params.size() == 3) {
                        IntegerItem en = (IntegerItem) params.get(2).getResult();
                        result = this.subString(str.getValue(), st.getValue(), en.getValue());
                    } else {
                        result = this.subString(str.getValue(), st.getValue());
                    }
                }
                    break;

                case Constants.FUNCTION_UPPERCASE: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    StringItem str = (StringItem) params.get(0).getResult();
                    result = this.upperCase(str.getValue());
                }
                    break;

                case Constants.FUNCTION_ENDS_WITH: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    StringItem str = (StringItem) params.get(0).getResult();
                    StringItem suffix = (StringItem) params.get(1).getResult();

                    result = this.endsWith(str.getValue(), suffix.getValue());
                }
                    break;

                case Constants.FUNCTION_IS_EMPTY: {
                    // TODO: Need to introduce list variable
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    Object item = params.get(0).getResult();
                    if (item instanceof List)
                        result = this.isEmpty((List) item);
                    else if (item instanceof StringItem)
                        result = this.isEmpty(((StringItem) item).getValue());
                }
                    break;

                case Constants.FUNCTION_JOIN: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    List list = new ArrayList<>();
                    for (DataResult param : params)
                        list.add((List) param.getResult());

                    result = this.join(list);
                }
                    break;

                case Constants.FUNCTION_INDEX_OF: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    StringItem str = (StringItem) params.get(0).getResult();
                    StringItem search = (StringItem) params.get(1).getResult();
                    result = this.indexOf(str.getValue(), search.getValue());
                }
                    break;

                case Constants.FUNCTION_HAS_ANNOTATION: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    ClassItem classItem = (ClassItem) params.get(0).getResult();
                    if (params.size() == 3) {
                        MethodItem methodItem = (MethodItem) params.get(1).getResult();
                        StringItem annotation = (StringItem) params.get(2).getResult();
                        result = this.hasAnnotation(classItem, methodItem, annotation.getValue());
                    } else {
                        StringItem annotation = (StringItem) params.get(1).getResult();
                        result = this.hasAnnotation(classItem, annotation.getValue());
                    }
                }
                    break;

                case Constants.FUNCTION_GET_RETURN_TYPE: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    MethodItem methodItem = (MethodItem) params.get(0).getResult();
                    result = this.getReturnType(methodItem);
                }
                    break;

                case Constants.FUNCTION_IS_ITERABLE: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    StringItem dataType = (StringItem) params.get(0).getResult();
                    result = this.isIterable(dataType);
                }
                    break;

                case Constants.FUNCTION_HAS_ANNO_ATTR: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    ClassItem classItem = (ClassItem) params.get(0).getResult();
                    StringItem anno = (StringItem) params.get(1).getResult();
                    StringItem prop = (StringItem) params.get(2).getResult();
                    result = this.hasAnnoAttr(classItem, anno.getValue(), prop.getValue());
                }
                    break;

                case Constants.FUNCTION_GET_ANNO_ATTR_NAMES: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    ClassItem classItem = (ClassItem) params.get(0).getResult();
                    StringItem anno = (StringItem) params.get(1).getResult();
                    result = this.getAnnoAttrNames(classItem, anno.getValue());
                }
                    break;

                case Constants.FUNCTION_IS_UNIQUE: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    StringItem className = (StringItem) params.get(0).getResult();
                    result = this.isUnique(className.getValue());
                }
                    break;

                case Constants.FUNCTION_LOCATE_CLASS: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    StringItem className = (StringItem) params.get(0).getResult();
                    result = this.locateClass(className.getValue());
                }
                    break;

                case Constants.FUNCTION_CLASS_EXISTS: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    StringItem classFQN = (StringItem) params.get(0).getResult();
                    result = this.classExists(classFQN.getValue());
                }
                    break;

                case Constants.FUNCTION_HAS_DUPLICATE_BEANS: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    XMLItem xmlItem = (XMLItem) params.get(0).getResult();
                    result = this.hasDuplicateBeans(xmlItem);

                }
                    break;
            }
        } catch (Exception ex) {
            utils.Logger.log(ex.getMessage());
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
