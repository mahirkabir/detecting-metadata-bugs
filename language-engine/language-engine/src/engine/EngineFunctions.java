package engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import models.AnnotatedItem;
import models.AnnotationAttrItem;
import models.AnnotationItem;
import models.ArgumentItem;
import models.BooleanItem;
import models.ClassItem;
import models.DataResult;
import models.FieldItem;
import models.IntegerItem;
import models.InvocationItem;
import models.JItem;
import models.MethodItem;
import models.ObjectCreationItem;
import models.ParamItem;
import models.StringItem;
import models.VariableItem;
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

    private DataResult importsClass(ClassItem classItem, String classToCheck) {
        String functionCall = "importsClass()" + "||" + classItem.getFqn() + "||" + classToCheck;
        DataResult<BooleanItem> result = this.cache.fetchFunctionCall(functionCall);

        boolean importResult = false;
        List<String> imports = classItem.getImports();
        if (imports != null) {
            for (String imported : imports) {
                if (imported.equals(classToCheck)) {
                    importResult = true;
                    break;
                } else if (imported.endsWith(".*") || classToCheck.endsWith(".*")) {
                    if (imported.startsWith(classToCheck.replace(".*", ""))
                            || classToCheck.startsWith(imported.replace(".*", ""))) {
                        importResult = true;
                        break;
                    }
                }
            }
        }

        BooleanItem importCheck = new BooleanItem(importResult);
        result = new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, importCheck);
        this.cache.addFunctionCall(functionCall, result);
        return result;
    }

    private DataResult extendsClass(ClassItem classItem, String classToCheck) {
        String functionCall = "extendsClass()" + "||" + classItem.getFqn() + "||" + classToCheck;
        DataResult<BooleanItem> result = this.cache.fetchFunctionCall(functionCall);

        boolean extendResult = false;
        List<String> parentClasses = classItem.getExtendedClasses();
        if (parentClasses != null) {
            for (String extendItem : parentClasses) {
                if (extendItem.equals(classToCheck)) {
                    extendResult = true;
                    break;
                }
            }
        }

        BooleanItem extendCheck = new BooleanItem(extendResult);
        result = new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, extendCheck);
        this.cache.addFunctionCall(functionCall, result);
        return result;
    }

    private DataResult<List<AnnotatedItem>> getAnnotated(String annotation, String entityType) {
        String functionCall = "getAnnotated()" + "||" + annotation + "||" + entityType;
        DataResult<List<AnnotatedItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            List<AnnotatedItem> annotatedItems = this.classHelper.getAnnotated();
            List<AnnotatedItem> filteredAnnotatedItems = new ArrayList<AnnotatedItem>();

            for (AnnotatedItem annItem : annotatedItems) {
                if ((entityType.equals("*") ||
                        annItem.getEntity().getType().equals(entityType))
                        && annItem.getAnnotationName()
                                .equals(annotation.replace("@", "")))
                    filteredAnnotatedItems.add(annItem);
            }

            result = new DataResult<List<AnnotatedItem>>(
                    Constants.TYPE_ANNOTATED_ENTITY_LIST, filteredAnnotatedItems);
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

    private DataResult<List<MethodItem>> getConstructors(ClassItem c) {
        String functionCall = "getConstructors()" + "||" + c.getFqn();
        DataResult<List<MethodItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            List<MethodItem> constructorItems = this.classHelper.getConstructors(c.getFqn());
            if (constructorItems == null)
                constructorItems = new ArrayList<MethodItem>();
            result = new DataResult<List<MethodItem>>(Constants.TYPE_CONSTRUCTOR_LIST,
                    constructorItems);
            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    /**
     * Check if a method/constructor has parameter of a certain name
     * 
     * @param m
     * @param name
     * @return
     */
    private DataResult<BooleanItem> hasParam(MethodItem m, String name) {
        List<ParamItem> parameters = m.getParameters();
        boolean hasName = false;
        if (parameters != null) {
            hasName = m.getParameters().stream()
                    .anyMatch(paramItem -> paramItem.getName().equals(name));
        }
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(hasName));
    }

    /**
     * Check if a method/constructor has parameter of a certain type
     * 
     * @param m
     * @param type
     * @return
     */
    private DataResult<BooleanItem> hasParamType(MethodItem m, String type) {
        List<ParamItem> parameters = m.getParameters();
        boolean hasType = false;
        if (parameters != null) {
            hasType = m.getParameters().stream()
                    .anyMatch(paramItem -> paramItem.getType().equals(type));
        }
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(hasType));
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

    private DataResult<BooleanItem> hasAnnotation(MethodItem m, String annotation) {
        annotation = annotation.replace("@", "");
        String basicFunction = "hasAnnotation(m)" + "||" + m.getClassFQN() + "||" + m.getName();
        String functionCall = basicFunction + "||" + annotation;
        DataResult<BooleanItem> result = this.cache.fetchFunctionCall(functionCall);
        boolean found = false;
        if (result == null) {
            List<AnnotationItem> annotations = m.getAnnotations();
            if (annotations != null) {
                for (AnnotationItem annoItem : annotations) {
                    if (annoItem.getAnnotationName().equals(annotation)) {
                        found = true;
                        break;
                    }
                }
            }
            result = new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(found));
            this.cache.addFunctionCall(functionCall, result);
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

    private DataResult<List<StringItem>> getArg(ClassItem c, String callerClass, String methodName, int argIdx) {
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
                List<FieldItem> fields = this.getFields(c).getResult();
                List<VariableItem> variables = this.classHelper.getVariables(c.getFqn());

                for (InvocationItem invocationItem : invocationItems) {
                    if (invocationItem.getCallee().equals(methodName)) {
                        String callerObjectName = invocationItem.getCaller();
                        boolean callerClassMatches = false;
                        if (fields != null)
                            callerClassMatches = fields.stream().anyMatch(item -> item.getName()
                                    .equals(callerObjectName) && item.getType().equals(callerClass));
                        if (!callerClassMatches && variables != null)
                            callerClassMatches = variables.stream().anyMatch(item -> item.getName()
                                    .equals(callerObjectName) && item.getType().equals(callerClass));
                        if (callerClassMatches) {
                            String arg = invocationItem.getArguments().get(argIdx);
                            // NOTE: For now, we are only considering string literals
                            // or CLASSNAME.class arguments
                            // Variables are skipped for now
                            // Other literals are also skipped for now
                            if (arg.startsWith("\"")) {
                                arg = arg.substring(1, arg.length() - 1);
                                result.getResult().add(new StringItem(arg));
                            } else if (arg.endsWith("." + Constants.TYPE_CLASS)) {
                                result.getResult().add(new StringItem(arg));
                            }
                        }
                    }
                }
            }

            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    /**
     * Get all the argIdx number arguments for the constructorInvocation inside c
     * 
     * @param c
     * @param constructorInvocation
     * @param argIdx
     * @return
     */
    private DataResult<List<StringItem>> getArg(ClassItem c, String constructorInvocation, Integer argIdx) {
        String functionCall = "getArg()" + "||" + c.getFqn() + "||" + constructorInvocation + "||" + argIdx;
        DataResult<List<StringItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            List<StringItem> args = new ArrayList<StringItem>();
            List<ObjectCreationItem> objectCreations = this.classHelper
                    .getObjectCreations(c.getFqn());
            if (objectCreations != null) {
                objectCreations.forEach(objectCreationItem -> {
                    if (objectCreationItem.getDeclType().equals(constructorInvocation)) {
                        List<ArgumentItem> arguments = objectCreationItem.getArguments();
                        if (arguments != null && arguments.size() - 1 >= argIdx) {
                            args.add(new StringItem(arguments.get(argIdx).getValue()));
                        }
                    }
                });
            }

            result = new DataResult<List<StringItem>>(Constants.TYPE_STRING_LIST, args);
            this.cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    /**
     * Check if the constructor was used to create any object
     * 
     * @param c
     * @param constructor
     * @return
     */
    private DataResult<BooleanItem> objectCreated(ClassItem c, String constructor) {
        String functionCall = "objectCreated()" + "||" + c.getFqn() + "||" + constructor;
        DataResult<BooleanItem> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            boolean resObjectCreated = false;

            List<ObjectCreationItem> objectCreations = this.classHelper.getObjectCreations(c.getFqn());
            if (objectCreations != null)
                resObjectCreated = objectCreations.stream().anyMatch(item -> item.getDeclType().equals(constructor));

            result = new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(resObjectCreated));
            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    private DataResult<BooleanItem> callExists(ClassItem c, String callerClass, String invocation) {
        String basicFunction = "callExists()" + "||" + c.getFqn();
        String functionCall = basicFunction + "||" + invocation;
        DataResult<BooleanItem> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            boolean resCallExists = false;
            List<FieldItem> fields = this.getFields(c).getResult();
            List<VariableItem> variables = this.classHelper.getVariables(c.getFqn());

            List<InvocationItem> invocations = this.classHelper.getInvocations(c.getFqn());
            if (invocations != null) {
                for (InvocationItem invocationItem : invocations) {
                    if (invocationItem.getCallee().equals(invocation)) {
                        // TODO: Need to handle static method invocations
                        String callerObjectName = invocationItem.getCaller();
                        if (callerObjectName.isEmpty())
                            resCallExists = true; // Same-class-method
                        if (!resCallExists && fields != null)
                            resCallExists = fields.stream().anyMatch(item -> item.getName()
                                    .equals(callerObjectName) && item.getType().equals(callerClass));
                        if (!resCallExists && variables != null)
                            resCallExists = variables.stream().anyMatch(item -> item.getName()
                                    .equals(callerObjectName) && item.getType().equals(callerClass));
                    }
                    if (resCallExists)
                        break;
                }
            }

            result = new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(resCallExists));
            cache.addFunctionCall(functionCall, result);
        }

        return result;
    }

    private DataResult<StringItem> getFQN(ClassItem c) {
        return new DataResult<StringItem>(Constants.TYPE_STRING, new StringItem(c.getFqn()));
    }

    private DataResult<List<StringItem>> getAnnoAttr(ClassItem c, String annotation, String attr) {
        List<StringItem> annoAttrs = new ArrayList<StringItem>();
        List<AnnotationItem> classAnnotationItems = this.getAnnotations(c).getResult();
        if (classAnnotationItems == null)
            classAnnotationItems = new ArrayList<AnnotationItem>();

        classAnnotationItems.forEach(classAnnotationItem -> {
            if (classAnnotationItem.getAnnotationName().equals(annotation)) {
                if (classAnnotationItem.getAnnotationAttrs() != null) {
                    classAnnotationItem.getAnnotationAttrs().forEach(annAttrItem -> {
                        if (annAttrItem.getAnnotationAttrName().equals(attr)) {
                            annoAttrs.add(new StringItem(annAttrItem.getAnnotationAttrValue()));
                        }
                    });
                }
            }
        });

        return new DataResult<List<StringItem>>(Constants.TYPE_STRING_LIST, annoAttrs);
    }

    private DataResult<List<StringItem>> getAnnoAttr(ClassItem c, MethodItem m, String annotation, String attr) {
        String functionCall = "getAnnoAttr()" + "||" + m.getFqn() + "||" + annotation + "||" + attr;
        DataResult<List<StringItem>> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            result = new DataResult<List<StringItem>>(
                    Constants.TYPE_STRING_LIST, new ArrayList<StringItem>());

            List<StringItem> annAttrVals = new ArrayList<StringItem>();
            for (AnnotationItem annotationItem : m.getAnnotations()) {
                if (annotationItem.getAnnotationName().equals(annotation)) {
                    for (AnnotationAttrItem annotationAttrItem : annotationItem.getAnnotationAttrs()) {
                        if (annotationAttrItem.getAnnotationAttrName().equals(attr)) {
                            annAttrVals.add(new StringItem(annotationAttrItem.getAnnotationAttrValue()));
                        }
                    }
                }
            }
            result.setResult(annAttrVals);
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

    private DataResult<BooleanItem> hasAttr(XMLItem node, String attrName) {
        boolean resHasAttr = node.hasAttr(attrName);
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN,
                new BooleanItem(resHasAttr));
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
        while (path.startsWith("/"))
            path = path.substring(1);
        boolean pathExists = Helper.pathExists(path);
        if (pathExists == false)
            System.out.println("");
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

    /**
     * For checking if ClassItem is an empty ClassItem
     * (A valid ClassItem will always have a file path)
     * 
     * @param classItem
     * @return
     */
    private DataResult<BooleanItem> isEmpty(ClassItem classItem) {
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN,
                new BooleanItem(classItem.getFilePath().isBlank()));
    }

    private DataResult<BooleanItem> endsWith(String str, String suffix) {
        boolean endsWith = str.endsWith(suffix);
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(endsWith));
    }

    private DataResult<BooleanItem> startsWith(String str, String prefix) {
        boolean startsWith = str.startsWith(prefix);
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(startsWith));
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
    private DataResult<ClassItem> locateClassSN(String classSN) {
        classSN = classSN.replace(".class", "");
        ClassItem ret = new ClassItem("");
        if (this.classHelper.getClassSNDict().containsKey(classSN))
            ret = this.classHelper.getClassSNDict().get(classSN).get(0);
        return new DataResult<ClassItem>(Constants.TYPE_CLASS, ret);
    }

    /**
     * Locate class based on Fully Qualified Name
     * 
     * @param classFQN
     * @return
     */
    private DataResult<ClassItem> locateClassFQN(String classFQN) {
        // classFQN = classFQN.replace(".class", "");
        if (classFQN.endsWith(".class"))
            classFQN = classFQN.substring(0, classFQN.length() - 6);

        ClassItem ret = new ClassItem("");

        if (this.classHelper.getClassDict().size() == 0) {
            // Classes might not have been loaded yet
            this.getClasses();
        }

        if (this.classHelper.getClassDict().containsKey(classFQN))
            ret = this.classHelper.getClassDict().get(classFQN);
        return new DataResult<ClassItem>(Constants.TYPE_CLASS, ret);
    }

    /**
     * Check if a class exists using the fully qualified name
     * 
     * @param classFQN
     * @return
     */
    private DataResult<BooleanItem> classExists(String classFQN) {
        if (this.classHelper.getClassDict().size() == 0)
            this.getClasses();
        boolean found = this.classHelper.getClassDict().containsKey(classFQN);
        return new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(found));
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

    /**
     * Check if the constructor index mentioned in the bean is in bound wrt the
     * constructor params
     * 
     * @param constructorItem
     * @param index
     * @return
     */
    private DataResult<BooleanItem> indexInBound(MethodItem constructorItem, int index) {
        boolean inBound = constructorItem.getParameters().size() > index;
        DataResult<BooleanItem> resInBound = new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(
                inBound));
        return resInBound;
    }

    /**
     * Check if property - propertyName is direct field of class c
     * 
     * @param c
     * @param propertyName
     * @return
     */
    private DataResult hasField(ClassItem c, String propertyName) {
        String basicFunction = "hasField()" + "||" + c.getFqn();
        String functionCall = basicFunction + "||" + propertyName;
        DataResult<BooleanItem> result = this.cache.fetchFunctionCall(functionCall);

        if (result == null) {
            List<FieldItem> fields = this.getFields(c).getResult();
            result = new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN,
                    new BooleanItem(false));
            if (fields != null) {
                for (FieldItem field : fields) {
                    functionCall = basicFunction + "||" + field.getName();
                    DataResult<BooleanItem> currRes = new DataResult<BooleanItem>(functionCall,
                            new BooleanItem(true));
                    this.cache.addFunctionCall(functionCall, currRes);
                    if (field.getName().equals(propertyName))
                        result.setResult(new BooleanItem(true));
                }
            }
        }

        return result;
    }

    /**
     * Get all the ancestor classes including the current class
     */
    private DataResult getFamily(ClassItem currentClass) {
        List<ClassItem> familyClasses = new ArrayList<ClassItem>();
        while (currentClass != null) {
            familyClasses.add(currentClass);
            List<String> parents = currentClass.getExtendedClasses();
            if (parents != null && parents.size() > 0) {
                currentClass = this.locateClassSN(parents.get(0)).getResult();
            } else
                break;
        }
        return new DataResult<List<ClassItem>>(Constants.TYPE_CLASS_LIST, familyClasses);
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

                case Constants.FUNCTION_GET_CONSTRUCTORS: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    ClassItem classItem = (ClassItem) params.get(0).getResult();
                    result = this.getConstructors(classItem);
                }
                    break;

                case Constants.FUNCTION_HAS_PARAM: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    MethodItem m = (MethodItem) params.get(0).getResult();
                    StringItem paramName = (StringItem) params.get(1).getResult();
                    result = this.hasParam(m, paramName.getValue());
                }
                    break;

                case Constants.FUNCTION_HAS_PARAM_TYPE: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    MethodItem m = (MethodItem) params.get(0).getResult();
                    StringItem type = (StringItem) params.get(1).getResult();
                    result = this.hasParamType(m, type.getValue());
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
                    StringItem callerClass = (StringItem) params.get(1).getResult();
                    StringItem invocation = (StringItem) params.get(2).getResult();
                    result = this.callExists(classItem, callerClass.getValue(), invocation.getValue());
                }
                    break;

                case Constants.FUNCTION_GET_ANNO_ATTR: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    ClassItem classItem = (ClassItem) params.get(0).getResult();

                    if (params.size() == 4) {
                        MethodItem methodItem = (MethodItem) params.get(1).getResult();
                        StringItem anno = (StringItem) params.get(2).getResult();
                        StringItem prop = (StringItem) params.get(3).getResult();
                        anno.setValue(anno.getValue().substring(1)); // To remove the @
                        result = this.getAnnoAttr(classItem, methodItem, anno.getValue(), prop.getValue());
                    } else {
                        StringItem anno = (StringItem) params.get(1).getResult();
                        StringItem prop = (StringItem) params.get(2).getResult();
                        anno.setValue(anno.getValue().substring(1)); // To remove the @
                        result = this.getAnnoAttr(classItem, anno.getValue(), prop.getValue());
                    }
                }
                    break;

                case Constants.FUNCTION_GET_ARG: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    ClassItem classItem = (ClassItem) params.get(0).getResult();

                    if (params.size() == 4) {
                        // For method invocations
                        StringItem callerClass = (StringItem) params.get(1).getResult();
                        StringItem invocation = (StringItem) params.get(2).getResult();
                        IntegerItem argIdx = (IntegerItem) params.get(3).getResult();
                        result = this.getArg(classItem, callerClass.getValue(), invocation.getValue(),
                                argIdx.getValue());
                    } else {
                        // For object creations
                        StringItem constructorInvocation = (StringItem) params.get(1).getResult();
                        IntegerItem argIdx = (IntegerItem) params.get(2).getResult();
                        result = this.getArg(classItem, constructorInvocation.getValue(), argIdx.getValue());
                    }
                }
                    break;

                case Constants.FUNCTION_GET_ANNOTATED: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    StringItem annoStr = (StringItem) params.get(0).getResult();
                    StringItem entityType = (StringItem) params.get(1).getResult();

                    annoStr.setValue(annoStr.getValue().substring(1));
                    List<AnnotatedItem> annRes = this.getAnnotated(
                            annoStr.getValue(), entityType.getValue()).getResult();
                    if (entityType.getValue().equals(Constants.TYPE_CLASS)) {
                        List<ClassItem> annotatedClasses = new ArrayList<ClassItem>();
                        annRes.forEach(annItem -> {
                            ClassItem classItem = new ClassItem(annItem.getJavaFilePath());
                            classItem.setName(annItem.getEntity().getName());
                            annotatedClasses.add(classItem);
                        });
                        result = new DataResult<List<ClassItem>>(
                                Constants.TYPE_CLASS_LIST, annotatedClasses);

                    } else {
                        result = new DataResult<List<AnnotatedItem>>(
                                Constants.TYPE_ANNOTATED_ENTITY_LIST, annRes);
                    }
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

                case Constants.FUNCTION_HAS_ATTR: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    XMLItem parentElm = (XMLItem) params.get(0).getResult();
                    StringItem attr = (StringItem) params.get(1).getResult();
                    result = this.hasAttr(parentElm, attr.getValue());
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

                case Constants.FUNCTION_STARTS_WITH: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    StringItem str = (StringItem) params.get(0).getResult();
                    StringItem suffix = (StringItem) params.get(1).getResult();

                    result = this.startsWith(str.getValue(), suffix.getValue());
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
                    else if (item instanceof ClassItem)
                        result = this.isEmpty((ClassItem) item);
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
                    if (params.get(0).getResult() instanceof ClassItem) {
                        ClassItem classItem = (ClassItem) params.get(0).getResult();
                        StringItem annotation = (StringItem) params.get(1).getResult();
                        result = this.hasAnnotation(classItem, annotation.getValue());
                    } else {
                        MethodItem methodItem = (MethodItem) params.get(0).getResult();
                        StringItem annotation = (StringItem) params.get(1).getResult();
                        result = this.hasAnnotation(methodItem, annotation.getValue());
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

                case Constants.FUNCTION_LOCATE_CLASS_SN: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    StringItem className = (StringItem) params.get(0).getResult();
                    result = this.locateClassSN(className.getValue());
                }
                    break;

                case Constants.FUNCTION_LOCATE_CLASS_FQN: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    StringItem className = (StringItem) params.get(0).getResult();
                    result = this.locateClassFQN(className.getValue());
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

                case Constants.FUNCTION_INDEX_IN_BOUND: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    MethodItem constructorItem = (MethodItem) params.get(0).getResult();
                    StringItem constructorIndex = (StringItem) params.get(1).getResult();
                    try {
                        int index = Integer.parseInt(constructorIndex.getValue());
                        result = this.indexInBound(constructorItem, index);
                    } catch (NumberFormatException numFormExp) {
                        utils.Logger
                                .log(name.getIdentifier() + ": " + constructorIndex.getValue() + " is not an integer");
                        result = new DataResult<BooleanItem>(Constants.TYPE_BOOLEAN, new BooleanItem(false));
                    }

                }
                    break;

                case Constants.FUNCTION_OBJECT_CREATED: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    ClassItem c = (ClassItem) params.get(0).getResult();
                    StringItem objectCreationMethod = (StringItem) params.get(1).getResult();
                    result = this.objectCreated(c, objectCreationMethod.getValue());
                }
                    break;

                case Constants.FUNCTION_HAS_FIELD: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    ClassItem c = (ClassItem) params.get(0).getResult();
                    StringItem propertyName = (StringItem) params.get(1).getResult();
                    result = this.hasField(c, propertyName.getValue());
                }
                    break;

                case Constants.IMPORTS_CLASS: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    ClassItem classItem = (ClassItem) params.get(0).getResult();
                    StringItem importedClass = (StringItem) params.get(1).getResult();
                    result = this.importsClass(classItem, importedClass.getValue());
                }
                    break;

                case Constants.EXTENDS_CLASS: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    ClassItem childClass = (ClassItem) params.get(0).getResult();
                    StringItem parentClassFQN = (StringItem) params.get(1).getResult();
                    result = this.extendsClass(childClass, parentClassFQN.getValue());
                }
                    break;

                case Constants.GET_FAMILY: {
                    List<DataResult> params = this.getParams((ASTFunctionTail) funcNode.jjtGetChild(1));
                    ClassItem currentClass = (ClassItem) params.get(0).getResult();
                    result = this.getFamily(currentClass);
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
