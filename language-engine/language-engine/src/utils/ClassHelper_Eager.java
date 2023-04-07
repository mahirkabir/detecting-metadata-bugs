package utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import engine.EngineFactory;
import engine.IEngineCache;
import models.AnnotatedItem;
import models.AnnotationAttrItem;
import models.AnnotationItem;
import models.ArgumentItem;
import models.ClassItem;
import models.FieldItem;
import models.InvocationItem;
import models.JItem;
import models.MethodItem;
import models.ObjectCreationItem;
import models.ParamItem;
import models.VariableItem;

/*
 * NOTE: This is a temporary file to implement urgent eager analysis
 * NOTE: We will remove this class and cache the compilation unit in future 
 * to implement faster lazy analysis
 * ClassHelper version that uses eager analysis to explore different branches of the class tree
 * e.g. - Fields, Methods, Invocations etc. will be explored at one go
 * To use this version, first rename it back to ClassHelper (If it's named otherwise),
 * Then use this to class to instantiate the classHelper variables in the project
 */
public class ClassHelper_Eager {
    private List<String> javaFiles;
    private List<AnnotatedItem> annotatedItems;
    private String projectPath;
    private Map<String, ClassItem> dictClass;
    private Map<String, List<ClassItem>> dictSNClass;
    private IEngineCache engineCache;

    /**
     * Instantiate project path for the project
     * 
     * @param projectPath
     */
    public ClassHelper_Eager(String projectPath) {
        super();
        this.javaFiles = new ArrayList<String>();
        this.projectPath = projectPath;
        this.dictClass = new HashMap<String, ClassItem>();
        this.dictSNClass = new HashMap<String, List<ClassItem>>();
        this.engineCache = EngineFactory.getEngineCache();
    }

    /**
     * Get all java file paths under folder
     * 
     * @folder - Folder location
     * @return
     */
    public void loadJavaFiles(String folder) {
        File[] files = new File(folder).listFiles();
        if (files != null && files.length > 0) {
            for (File currFile : files) {
                String fullPath = folder + "\\" + currFile.getName().toString();
                if (currFile.isDirectory()) {
                    this.loadJavaFiles(fullPath.toString());
                } else {
                    if (currFile.getName().endsWith(Constants.EXTENSION_JAVA))
                        this.javaFiles.add(fullPath);

                    if (currFile.getName().endsWith(Constants.EXTENSION_JAVA)
                            || currFile.getName().endsWith(Constants.EXTENSION_XML)) {
                        this.engineCache.addLoadedFilename(fullPath);
                    }
                }
            }
        }
    }

    /**
     * Get all class items in the specified project
     * 
     * @return
     */
    public List<ClassItem> getClasses() {
        List<ClassItem> classes = new ArrayList<ClassItem>();

        this.loadJavaFiles(this.projectPath);
        for (String javaFile : this.javaFiles) {
            try {
                Path filePath = Paths.get(javaFile);
                CompilationUnit cu = StaticJavaParser.parse(filePath);
                AnnotationHelper annotationHelper = new AnnotationHelper(javaFile);

                for (TypeDeclaration<?> classDecl : cu.getTypes()) {
                    ClassItem classItem = new ClassItem(javaFile);

                    String className = classDecl.getNameAsString();
                    classItem.setName(className);
                    classItem.setType(Constants.TYPE_CLASS);
                    classItem.setFqn(className);

                    String packageName = "";
                    Optional<PackageDeclaration> packageDecl = cu.getPackageDeclaration();
                    if (packageDecl.isPresent()) {
                        packageName = packageDecl.get().getNameAsString();
                        if (packageName != null && packageName.isEmpty())
                            classItem.setFqn(packageName + "." + className);
                    }

                    List<AnnotationItem> annItems = this.getAnnotations(classDecl.getAnnotations());
                    annItems.forEach(item -> item.setParentEntity(className));
                    List<FieldItem> fields = this.getFields(classDecl.getFields());
                    List<MethodItem> constructors = new ArrayList<MethodItem>();
                    List<MethodItem> methods = new ArrayList<MethodItem>();
                    List<InvocationItem> invocations = new ArrayList<InvocationItem>();
                    List<ObjectCreationItem> objectCreations = new ArrayList<ObjectCreationItem>();
                    List<VariableItem> variables = new ArrayList<VariableItem>();

                    try {
                        for (ConstructorDeclaration constructorDecl : classDecl.getConstructors()) {
                            MethodItem constructorItem = new MethodItem();
                            constructorItem.setName(constructorDecl.getNameAsString());
                            constructorItem.setClassName(className);
                            constructorItem.setAnnotations(annotationHelper.getCallableAnnotations(constructorDecl));
                            constructorItem.setType(Constants.TYPE_CONSTRUCTOR);

                            List<String> modifiers = new ArrayList<String>();
                            constructorDecl.getModifiers().forEach(item -> {
                                modifiers.add(item.toString());
                            });

                            if (modifiers.size() >= 1)
                                constructorItem.setAccessModifier(modifiers.get(0));
                            if (modifiers.size() == 2)
                                constructorItem.setDeclType(modifiers.get(1));

                            if (constructorDecl.getParameters() != null)
                                constructorDecl.getParameters().forEach(item -> {
                                    ParamItem paramItem = new ParamItem();
                                    paramItem.setName(item.getNameAsString());
                                    paramItem.setType(item.getTypeAsString());
                                    if (!item.getModifiers().isEmpty())
                                        paramItem.setAccessModifier(item.getModifiers().get(0).toString());
                                    constructorItem.addParameter(paramItem);
                                });

                            constructors.add(constructorItem);

                            try {
                                for (MethodCallExpr invocationExpr : constructorDecl.findAll(MethodCallExpr.class)) {
                                    Node parentNode = invocationExpr.getParentNode().get();
                                    String invocationStmnt = parentNode.toString();
                                    String callee = invocationExpr.getNameAsString();

                                    Expression callerExpr = invocationExpr.getScope().orElse(null);
                                    String callerVariable = "";
                                    if (callerExpr != null) {
                                        String[] methodCallParts = callerExpr.toString().split("\\.");
                                        if (methodCallParts.length > 1) {
                                            callerVariable = methodCallParts[methodCallParts.length - 1];
                                        }
                                    }

                                    List<String> arguments = new ArrayList<String>();
                                    if (arguments != null) {
                                        invocationExpr.getArguments().forEach(item -> {
                                            arguments.add(item.toString());
                                        });
                                    }

                                    InvocationItem invocationItem = new InvocationItem();
                                    invocationItem.setCallee(callee);
                                    invocationItem.setCaller(callerVariable);
                                    invocationItem.setClassName(className.toString());
                                    invocationItem.setInvocationStmnt(invocationStmnt);
                                    invocationItem.setArguments(arguments);
                                    invocations.add(invocationItem);
                                }
                            } catch (Exception ex) {
                                Logger.log("Error parsing invocations inside constructor for: " + className);
                            }

                            try {
                                for (VariableDeclarationExpr variableExpr : constructorDecl
                                        .findAll(VariableDeclarationExpr.class)) {
                                    Node parentNode = variableExpr.getParentNode().get();
                                    String variableMethod = "";
                                    while (parentNode != null && !(parentNode instanceof ClassOrInterfaceDeclaration)) {
                                        if (parentNode instanceof MethodDeclaration) {
                                            MethodDeclaration methodNode = (MethodDeclaration) parentNode;
                                            String methodType = methodNode.getTypeAsString();
                                            String methodName = methodNode.getNameAsString();
                                            StringBuilder parameters = new StringBuilder("(");
                                            if (methodNode.getParameters() != null) {
                                                methodNode.getParameters().forEach(methodParam -> {
                                                    if (parameters.toString().equals("("))
                                                        parameters.append(methodParam.toString());
                                                    else
                                                        parameters.append(", " + methodParam.toString());
                                                });
                                            }
                                            parameters.append(")");
                                            variableMethod = methodType + " " + methodName + parameters.toString();
                                        }
                                        parentNode = parentNode.getParentNode().get();
                                    }

                                    String parentMethod = variableMethod;
                                    List<String> varModifiers = new ArrayList<String>();
                                    variableExpr.getModifiers().forEach(item -> {
                                        varModifiers.add(item.toString());
                                    });

                                    if (variableExpr.getVariables() != null) {
                                        variableExpr.getVariables().forEach(item -> {
                                            VariableItem variableItem = new VariableItem();
                                            variableItem.setName(item.getNameAsString());
                                            variableItem.setType(item.getTypeAsString());
                                            variableItem.setClassName(className);
                                            variableItem.setParentMethod(parentMethod);
                                            if (varModifiers.size() >= 1)
                                                variableItem.setAccessModifier(varModifiers.get(0));
                                            if (varModifiers.size() == 2)
                                                variableItem.setDeclType(varModifiers.get(1));

                                            variables.add(variableItem);
                                        });
                                    }
                                }
                            } catch (Exception ex) {
                                Logger.log("Error parsing variables inside constructor for: " + className);
                            }

                            try {
                                for (ObjectCreationExpr objectCreationExpr : constructorDecl
                                        .findAll(ObjectCreationExpr.class)) {
                                    ObjectCreationItem objectCreationItem = new ObjectCreationItem();
                                    objectCreationItem.setType(Constants.TYPE_OBJECT_CREATION);
                                    objectCreationItem.setClassName(className);
                                    objectCreationItem.setDeclType(objectCreationExpr.getTypeAsString());

                                    if (objectCreationExpr.getArguments() != null) {
                                        objectCreationExpr.getArguments().forEach(argItem -> {
                                            ArgumentItem argument = new ArgumentItem();
                                            if (argItem instanceof StringLiteralExpr) {
                                                argument.setType(Constants.TYPE_STRING);

                                                String argumentVal = argItem.toString();
                                                if (argumentVal.startsWith("\"")
                                                        && argumentVal.endsWith("\""))
                                                    argumentVal = argumentVal.substring(1, argumentVal.length() - 1);
                                                argument.setValue(argumentVal);
                                                objectCreationItem.addArgument(argument);
                                            }
                                            // TODO: Do for other types of arguments
                                        });
                                    }

                                    objectCreations.add(objectCreationItem);
                                }
                            } catch (Exception ex) {
                                Logger.log("Error parsing object creations inside constructor for: " + className);
                            }
                        }
                    } catch (Exception ex) {
                        Logger.log("Error parsing constructor declaration for: " + className);
                    }

                    for (MethodDeclaration methodDecl : classDecl.getMethods()) {
                        MethodItem methodItem = new MethodItem();
                        methodItem.setName(methodDecl.getNameAsString());
                        methodItem.setType(methodDecl.getTypeAsString());
                        methodItem.setClassName(className);
                        methodItem.setAnnotations(this.getMethodAnnotations(methodDecl));

                        List<String> modifiers = new ArrayList<String>();
                        methodDecl.getModifiers().forEach(item -> {
                            modifiers.add(item.toString());
                        });

                        if (modifiers.size() >= 1)
                            methodItem.setAccessModifier(modifiers.get(0));
                        if (modifiers.size() == 2)
                            methodItem.setDeclType(modifiers.get(1));

                        methods.add(methodItem);

                        try {
                            for (MethodCallExpr invocationExpr : methodDecl.findAll(MethodCallExpr.class)) {
                                Node parentNode = invocationExpr.getParentNode().get();
                                String invocationStmnt = parentNode.toString();
                                String callee = invocationExpr.getNameAsString();

                                Expression callerExpr = invocationExpr.getScope().orElse(null);
                                String callerVariable = "";
                                if (callerExpr != null) {
                                    String[] methodCallParts = callerExpr.toString().split("\\.");
                                    if (methodCallParts.length > 1) {
                                        callerVariable = methodCallParts[methodCallParts.length - 1];
                                    }
                                }

                                List<String> arguments = new ArrayList<String>();
                                if (arguments != null) {
                                    invocationExpr.getArguments().forEach(item -> {
                                        arguments.add(item.toString());
                                    });
                                }

                                InvocationItem invocationItem = new InvocationItem();
                                invocationItem.setCallee(callee);
                                invocationItem.setCaller(callerVariable);
                                invocationItem.setClassName(className.toString());
                                invocationItem.setInvocationStmnt(invocationStmnt);
                                invocationItem.setArguments(arguments);
                                invocations.add(invocationItem);
                            }
                        } catch (Exception ex) {
                            Logger.log("Error parsing invocations inside method for: " + className);
                        }

                        try {
                            for (VariableDeclarationExpr variableExpr : methodDecl
                                    .findAll(VariableDeclarationExpr.class)) {
                                Node parentNode = variableExpr.getParentNode().get();
                                String variableMethod = "";
                                while (parentNode != null && !(parentNode instanceof ClassOrInterfaceDeclaration)) {
                                    if (parentNode instanceof MethodDeclaration) {
                                        MethodDeclaration methodNode = (MethodDeclaration) parentNode;
                                        String methodType = methodNode.getTypeAsString();
                                        String methodName = methodNode.getNameAsString();
                                        StringBuilder parameters = new StringBuilder("(");
                                        if (methodNode.getParameters() != null) {
                                            methodNode.getParameters().forEach(methodParam -> {
                                                if (parameters.toString().equals("("))
                                                    parameters.append(methodParam.toString());
                                                else
                                                    parameters.append(", " + methodParam.toString());
                                            });
                                        }
                                        parameters.append(")");
                                        variableMethod = methodType + " " + methodName + parameters.toString();
                                    }
                                    parentNode = parentNode.getParentNode().get();
                                }

                                String parentMethod = variableMethod;
                                List<String> varModifiers = new ArrayList<String>();
                                variableExpr.getModifiers().forEach(item -> {
                                    varModifiers.add(item.toString());
                                });

                                if (variableExpr.getVariables() != null) {
                                    variableExpr.getVariables().forEach(item -> {
                                        VariableItem variableItem = new VariableItem();
                                        variableItem.setName(item.getNameAsString());
                                        variableItem.setType(item.getTypeAsString());
                                        variableItem.setClassName(className);
                                        variableItem.setParentMethod(parentMethod);
                                        if (varModifiers.size() >= 1)
                                            variableItem.setAccessModifier(varModifiers.get(0));
                                        if (varModifiers.size() == 2)
                                            variableItem.setDeclType(varModifiers.get(1));

                                        variables.add(variableItem);
                                    });
                                }
                            }
                        } catch (Exception ex) {
                            Logger.log("Error parsing variables inside method for: " + className);
                        }

                        try {
                            for (ObjectCreationExpr objectCreationExpr : methodDecl
                                    .findAll(ObjectCreationExpr.class)) {
                                ObjectCreationItem objectCreationItem = new ObjectCreationItem();
                                objectCreationItem.setType(Constants.TYPE_OBJECT_CREATION);
                                objectCreationItem.setClassName(className);
                                objectCreationItem.setDeclType(objectCreationExpr.getTypeAsString());

                                if (objectCreationExpr.getArguments() != null) {
                                    objectCreationExpr.getArguments().forEach(argItem -> {
                                        ArgumentItem argument = new ArgumentItem();
                                        if (argItem instanceof StringLiteralExpr) {
                                            argument.setType(Constants.TYPE_STRING);

                                            String argumentVal = argItem.toString();
                                            if (argumentVal.startsWith("\"")
                                                    && argumentVal.endsWith("\""))
                                                argumentVal = argumentVal.substring(1, argumentVal.length() - 1);
                                            argument.setValue(argumentVal);
                                            objectCreationItem.addArgument(argument);
                                        }
                                        // TODO: Do for other types of arguments
                                    });
                                }

                                objectCreations.add(objectCreationItem);
                            }
                        } catch (Exception ex) {
                            Logger.log("Error parsing object creations inside method for: " + className);
                        }
                    }

                    classItem.setFields(fields);
                    classItem.setAnnotations(annItems);
                    classItem.setConstructors(constructors);
                    classItem.setMethods(methods);
                    classItem.setInvocations(invocations);
                    classItem.setObjectCreations(objectCreations);
                    classItem.setVariables(variables);
                    classes.add(classItem);

                    this.dictClass.put(classItem.getFqn(), classItem);
                    if (this.dictSNClass.get(className) == null)
                        this.dictSNClass.put(className, new ArrayList<>());
                    this.dictSNClass.get(className).add(classItem);
                }
            } catch (Exception ex) {
                Logger.log("getClasses() => Error parsing file: " + javaFile);
            }
        }

        Logger.log("Total classes: " + classes.size());
        return classes;
    }

    /**
     * Get all annotations for the method
     * 
     * @param decl
     * @return
     */
    private List<AnnotationItem> getMethodAnnotations(MethodDeclaration decl) {
        List<AnnotationItem> annotationItems = new ArrayList<>();

        List<AnnotationExpr> annotations = decl.getAnnotations();

        if (annotations == null)
            return annotationItems;

        for (AnnotationExpr annotation : annotations) {
            AnnotationItem annItem = new AnnotationItem();
            annItem.setParentEntity(decl.getNameAsString());
            annItem.setAnnotationName(annotation.getNameAsString());
            annItem.setAnnotationType(Constants.ANNOTATION_METHOD);

            if (annotation instanceof NormalAnnotationExpr) {
                NormalAnnotationExpr annExpr = (NormalAnnotationExpr) annotation;

                List<MemberValuePair> annKeyValuePairs = annExpr.getPairs();

                if (annKeyValuePairs != null) {
                    for (MemberValuePair pair : annKeyValuePairs) {
                        String annKey = pair.getNameAsString();
                        String annVal = pair.getValue().toString();
                        if (annVal.startsWith("\"") && annVal.endsWith("\""))
                            annVal = annVal.substring(1, annVal.length() - 1);
                        annItem.addAnnotationAttr(new AnnotationAttrItem(annKey, annVal));
                    }
                }
            } else if (annotation instanceof MarkerAnnotationExpr) {
                // No attribute for MarkerAnnotatiionExpr
                // No need to do anything about it
                MarkerAnnotationExpr annoExpr = (MarkerAnnotationExpr) annotation;
            } else if (annotation instanceof SingleMemberAnnotationExpr) {
                // Single parameter w/o values for SingleMemberAnnotationExpr
                // Need to collect the single parameter
                SingleMemberAnnotationExpr annoExpr = (SingleMemberAnnotationExpr) annotation;
            }

            annotationItems.add(annItem);
        }

        return annotationItems;
    }

    /**
     * Get field items from javaparser field declarations
     * 
     * @param fields
     * @return
     */
    private List<FieldItem> getFields(List<FieldDeclaration> fieldDecls) {
        List<FieldItem> fields = new ArrayList<FieldItem>();

        fieldDecls.forEach(decl -> {
            Node parentNode = decl.getParentNode().get();
            String fieldClass = "";
            while (parentNode != null && !(parentNode instanceof ClassOrInterfaceDeclaration))
                parentNode = parentNode.getParentNode().get();
            if (parentNode != null && parentNode instanceof NodeWithSimpleName) {
                fieldClass = ((NodeWithSimpleName<VariableDeclarator>) parentNode).getNameAsString();
            }

            String className = fieldClass;
            List<String> modifiers = new ArrayList<String>();
            decl.getModifiers().forEach(item -> {
                modifiers.add(item.toString());
            });

            List<AnnotationItem> annItems = new ArrayList<AnnotationItem>();
            decl.getAnnotations().forEach(fieldAnnItem -> {
                AnnotationItem annItem = new AnnotationItem();
                annItem.setAnnotationName(fieldAnnItem.getNameAsString());
                annItem.setAnnotationType(Constants.ANNOTATION_FIELD);
                annItem.setParentEntity(decl.toString());
                annItems.add(annItem);
            });

            if (decl.getVariables() != null) {
                decl.getVariables().forEach(item -> {
                    FieldItem fieldItem = new FieldItem();
                    fieldItem.setName(item.getNameAsString());
                    fieldItem.setType(item.getTypeAsString());
                    fieldItem.setClassName(className);
                    if (modifiers.size() >= 1)
                        fieldItem.setAccessModifier(modifiers.get(0));
                    if (modifiers.size() == 2)
                        fieldItem.setDeclType(modifiers.get(1));

                    fields.add(fieldItem);
                });
            }
        });

        return fields;
    }

    /**
     * Get annotation items from javaparser annotations
     * 
     * @param annotations
     * @return
     */
    private List<AnnotationItem> getAnnotations(NodeList<AnnotationExpr> annotations) {
        List<AnnotationItem> annItems = new ArrayList<AnnotationItem>();

        annotations.forEach(item -> {
            AnnotationItem annotationItem = new AnnotationItem();
            annotationItem.setAnnotationName(item.getNameAsString());
            annotationItem.setAnnotationType(Constants.ANNOTATION_CLASS);
            List<AnnotationAttrItem> attrs = new ArrayList<AnnotationAttrItem>();

            if (item instanceof NormalAnnotationExpr) {
                item.getChildNodes().forEach(paramItem -> {
                    if (paramItem.getChildNodes().size() >= 1) {
                        List<Node> childNodes = paramItem.getChildNodes();
                        if (childNodes.size() > 1) {
                            if (childNodes.get(1) instanceof ArrayInitializerExpr) {
                                ArrayInitializerExpr attrValArray = (ArrayInitializerExpr) childNodes.get(1);
                                if (attrValArray.getChildNodes().size() > 0) {
                                    attrValArray.getChildNodes().forEach(attrValItem -> {
                                        if (attrValItem instanceof StringLiteralExpr) {
                                            AnnotationAttrItem attr = new AnnotationAttrItem();
                                            attr.setAnnotationAttrName(paramItem.toString().split("=")[0].strip());
                                            String paramValue = attrValItem.toString();
                                            attr.setAnnotationAttrValue(paramValue);
                                            attrs.add(attr);
                                        }
                                    });
                                }
                            } else {
                                AnnotationAttrItem attr = new AnnotationAttrItem();
                                attr.setAnnotationAttrName(paramItem.toString().split("=")[0].strip());
                                String paramValue = childNodes.get(1).toString();
                                attr.setAnnotationAttrValue(paramValue);
                                attrs.add(attr);
                            }
                        }

                    }
                });

            } else if (item instanceof SingleMemberAnnotationExpr) {
                item.getChildNodes().forEach(paramItem -> {
                    if (paramItem instanceof ClassExpr) {
                        AnnotationAttrItem attr = new AnnotationAttrItem();
                        attr.setAnnotationAttrName(paramItem.toString());
                        attrs.add(attr);
                    } else if (paramItem instanceof ArrayInitializerExpr) {
                        paramItem.getChildNodes().forEach(exprItem -> {
                            if (exprItem instanceof ClassExpr) {
                                AnnotationAttrItem attr = new AnnotationAttrItem();
                                attr.setAnnotationAttrName(exprItem.toString());
                                attrs.add(attr);
                            }
                        });
                    }
                });
            }

            annotationItem.setAnnotationAttrs(attrs);
            annItems.add(annotationItem);
        });

        return annItems;
    }

    /**
     * Get all fields for class with cFqn as fully qualified name
     * 
     * @param cFqn
     * @return
     */
    public List<FieldItem> getFields(String cFqn) {
        if (!this.dictClass.containsKey(cFqn))
            return new ArrayList<FieldItem>();

        ClassItem classItem = this.dictClass.get(cFqn);
        if (classItem.getFields() != null)
            // If already loaded before, return the memoized result
            return classItem.getFields();

        String javaFilePath = classItem.getFilePath();
        List<FieldItem> fields = new FieldHelper(javaFilePath).GetFields();

        Map<String, String> dictRelevantClasses = new HashMap<String, String>();
        for (Map.Entry<String, ClassItem> entry : this.dictClass.entrySet()) {
            // Collecting all classes in the javaFilePath
            // Because, FieldHelper will get all fields from javaFilePath
            // We need to assign each field to the corresponding class using the class SN
            // Note: This will not be an extra O(N) operation as for each java file, it will
            // be done only once. For the later times, memoized values will be returned
            ClassItem elm = entry.getValue();
            if (elm.getFilePath().equals(javaFilePath))
                dictRelevantClasses.put(elm.getName(), elm.getFqn());
        }

        if (fields != null) {
            for (FieldItem field : fields) {
                String classSN = field.getClassName();
                if (dictRelevantClasses.containsKey(classSN)) {
                    String classFQN = dictRelevantClasses.get(classSN);
                    if (this.dictClass.containsKey(classFQN)) {
                        this.dictClass.get(classFQN).addField(field);
                    }
                }
            }
        }

        return classItem.getFields();
    }

    /**
     * Get all methods for class with cFqn as fully qualified name
     * 
     * @param cFqn
     * @return
     */
    public List<MethodItem> getMethods(String cFqn) {
        if (!this.dictClass.containsKey(cFqn))
            return new ArrayList<MethodItem>();

        ClassItem classItem = this.dictClass.get(cFqn);
        if (classItem.getMethods() != null)
            // If already loaded before, return the memoized result
            return classItem.getMethods();

        String javaFilePath = classItem.getFilePath();
        List<MethodItem> methods = new MethodHelper(javaFilePath).GetMethods();

        Map<String, String> dictRelevantClasses = new HashMap<String, String>();
        for (Map.Entry<String, ClassItem> entry : this.dictClass.entrySet()) {
            // Collecting all classes in the javaFilePath
            // Because, MethodHelper will get all fields from javaFilePath
            // We need to assign each method to the corresponding class using the class SN
            // Note: This will not be an extra O(N) operation as for each java file, it will
            // be done only once. For the later times, memoized values will be returned
            ClassItem elm = entry.getValue();
            if (elm.getFilePath().equals(javaFilePath))
                dictRelevantClasses.put(elm.getName(), elm.getFqn());
        }

        if (methods != null) {
            for (MethodItem method : methods) {
                String classSN = method.getClassName();
                if (dictRelevantClasses.containsKey(classSN)) {
                    String classFQN = dictRelevantClasses.get(classSN);
                    if (this.dictClass.containsKey(classFQN)) {
                        this.dictClass.get(classFQN).addMethod(method);
                    }
                }
            }
        }

        return classItem.getMethods();
    }

    /**
     * Get all constructors for class with cFqn as fully qualified name
     * 
     * @param cFqn
     * @return
     */
    public List<MethodItem> getConstructors(String cFqn) {
        if (!this.dictClass.containsKey(cFqn))
            return new ArrayList<MethodItem>();

        ClassItem classItem = this.dictClass.get(cFqn);
        if (classItem.getConstructors() != null)
            return classItem.getConstructors();

        String javaFilePath = classItem.getFilePath();
        List<MethodItem> constructors = new ConstructorHelper(javaFilePath).GetConstructors();

        Map<String, String> dictRelevantClasses = new HashMap<String, String>();
        for (Map.Entry<String, ClassItem> entry : this.dictClass.entrySet()) {
            ClassItem elm = entry.getValue();
            if (elm.getFilePath().equals(javaFilePath))
                dictRelevantClasses.put(elm.getName(), elm.getFqn());
        }

        if (constructors != null) {
            for (MethodItem constructor : constructors) {
                String classSN = constructor.getClassName();
                if (dictRelevantClasses.containsKey(classSN)) {
                    String classFQN = dictRelevantClasses.get(classSN);
                    if (this.dictClass.containsKey(classFQN)) {
                        this.dictClass.get(classFQN).addConstructor(constructor);
                    }
                }
            }
        }

        return classItem.getConstructors();
    }

    /**
     * Get all object creations for class with cFqn as fully qualified name
     * 
     * @param cFqn
     * @return
     */
    public List<ObjectCreationItem> getObjectCreations(String cFqn) {
        if (!this.dictClass.containsKey(cFqn))
            return new ArrayList<ObjectCreationItem>();

        ClassItem classItem = this.dictClass.get(cFqn);
        if (classItem.getObjectCreations() != null)
            return classItem.getObjectCreations();

        String javaFilePath = classItem.getFilePath();
        List<ObjectCreationItem> objectCreations = new ObjectCreationHelper(javaFilePath).GetObjectCreations();

        Map<String, String> dictRelevantClasses = new HashMap<String, String>();
        for (Map.Entry<String, ClassItem> entry : this.dictClass.entrySet()) {
            ClassItem elm = entry.getValue();
            if (elm.getFilePath().equals(javaFilePath))
                dictRelevantClasses.put(elm.getName(), elm.getFqn());
        }

        if (objectCreations != null) {
            for (ObjectCreationItem objectCreation : objectCreations) {
                String classSN = objectCreation.getClassName();
                if (dictRelevantClasses.containsKey(classSN)) {
                    String classFQN = dictRelevantClasses.get(classSN);
                    if (this.dictClass.containsKey(classFQN)) {
                        this.dictClass.get(classFQN).addObjectCreation(objectCreation);
                    }
                }
            }
        }

        return classItem.getObjectCreations();
    }

    /**
     * Get all method invocations for class with cFqn as fully qualified name
     * 
     * @param cFqn
     * @return
     */
    public List<InvocationItem> getInvocations(String cFqn) {
        if (!this.dictClass.containsKey(cFqn))
            return new ArrayList<InvocationItem>();

        ClassItem classItem = this.dictClass.get(cFqn);
        if (classItem.getInvocations() != null)
            // If already loaded before, return the memoized result
            return classItem.getInvocations();

        String javaFilePath = classItem.getFilePath();
        List<InvocationItem> invocations = new InvocationHelper(javaFilePath).GetInvocations();

        Map<String, String> dictRelevantClasses = new HashMap<String, String>();
        for (Map.Entry<String, ClassItem> entry : this.dictClass.entrySet()) {
            // Collecting all classes in the javaFilePath
            // Because, InvocationHelper will get all invocations from javaFilePath
            // We need to assign each method to the corresponding class using the class SN
            // Note: This will not be an extra O(N) operation as for each java file, it will
            // be done only once. For the later times, memoized values will be returned
            ClassItem elm = entry.getValue();
            if (elm.getFilePath().equals(javaFilePath))
                dictRelevantClasses.put(elm.getName(), elm.getFqn());
        }

        if (invocations != null) {
            for (InvocationItem invocation : invocations) {
                String classSN = invocation.getClassName();
                if (dictRelevantClasses.containsKey(classSN)) {
                    String classFQN = dictRelevantClasses.get(classSN);
                    if (this.dictClass.containsKey(classFQN)) {
                        this.dictClass.get(classFQN).addInvocation(invocation);
                    }
                }
            }
        }

        return classItem.getInvocations();
    }

    /**
     * Get all variable declarations for class with cFqn as fully qualified name
     * 
     * @param cFqn
     * @return
     */
    public List<VariableItem> getVariables(String cFqn) {
        if (!this.dictClass.containsKey(cFqn))
            return new ArrayList<VariableItem>();

        ClassItem classItem = this.dictClass.get(cFqn);
        if (classItem.getVariables() != null)
            // If already loaded before, return the memoized result
            return classItem.getVariables();

        String javaFilePath = classItem.getFilePath();
        List<VariableItem> variables = new VariableHelper(javaFilePath).GetVariables();

        Map<String, String> dictRelevantClasses = new HashMap<String, String>();
        for (Map.Entry<String, ClassItem> entry : this.dictClass.entrySet()) {
            // Collecting all classes in the javaFilePath
            // Because, VariableHelper will get all invocations from javaFilePath
            // We need to assign each variable to the corresponding class using the class SN
            // Note: This will not be an extra O(N) operation as for each java file, it will
            // be done only once. For the later times, memoized values will be returned
            ClassItem elm = entry.getValue();
            if (elm.getFilePath().equals(javaFilePath))
                dictRelevantClasses.put(elm.getName(), elm.getFqn());
        }

        if (variables != null) {
            for (VariableItem variable : variables) {
                String classSN = variable.getClassName();
                if (dictRelevantClasses.containsKey(classSN)) {
                    String classFQN = dictRelevantClasses.get(classSN);
                    if (this.dictClass.containsKey(classFQN)) {
                        this.dictClass.get(classFQN).addVariable(variable);
                    }
                }
            }
        }

        return classItem.getVariables();
    }

    /**
     * Get all annotations for class with cFqn as fully qualified name
     * 
     * @param cFqn
     * @return
     */
    public List<AnnotationItem> getAnnotations(String cFqn) {
        // TODO: Handle different types of annotation declarations
        if (!this.dictClass.containsKey(cFqn))
            return new ArrayList<AnnotationItem>();

        ClassItem classItem = this.dictClass.get(cFqn);
        if (classItem.getAnnotations() != null)
            // If already loaded before, return the memoized result
            return classItem.getAnnotations();

        String javaFilePath = classItem.getFilePath();
        List<AnnotationItem> annotations = new AnnotationHelper(javaFilePath).GetAnnotations();

        Map<String, String> dictRelevantClasses = new HashMap<String, String>();
        for (Map.Entry<String, ClassItem> entry : this.dictClass.entrySet()) {
            // Collecting all classes in the javaFilePath
            // Because, AnnotationHelper will get all invocations from javaFilePath
            // We need to assign each method to the corresponding class using the class SN
            // Note: This will not be an extra O(N) operation as for each java file, it will
            // be done only once. For the later times, memoized values will be returned
            ClassItem elm = entry.getValue();
            if (elm.getFilePath().equals(javaFilePath))
                dictRelevantClasses.put(elm.getName(), elm.getFqn());
        }

        if (annotations != null) {
            for (AnnotationItem annotation : annotations) {
                String classSN = annotation.getParentEntity();
                if (dictRelevantClasses.containsKey(classSN)) {
                    String classFQN = dictRelevantClasses.get(classSN);
                    if (this.dictClass.containsKey(classFQN)) {
                        this.dictClass.get(classFQN).addAnnotation(annotation);
                    }
                }
            }
        }

        return classItem.getAnnotations();
    }

    public Map<String, List<ClassItem>> getClassSNDict() {
        return this.dictSNClass;
    }

    /**
     * Get all annotated items (Class, Method, Field, Constructor)
     * 
     * @return
     */
    public List<AnnotatedItem> getAnnotated() {
        if (this.annotatedItems != null)
            return this.annotatedItems;

        this.annotatedItems = new ArrayList<AnnotatedItem>();
        if (this.javaFiles.size() == 0)
            this.loadJavaFiles(this.projectPath);

        this.javaFiles.forEach(javaFilePath -> {
            Path path = Paths.get(javaFilePath);
            String filename = path.getFileName().toString();
            String folder = path.getParent().toString();

            Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
            SourceRoot sourceRoot = new SourceRoot(
                    CodeGenerationUtils.mavenModuleRoot(FieldHelper.class)
                            .resolve(folder));
            CompilationUnit cu = sourceRoot.parse("", filename);

            cu.findAll(AnnotationExpr.class).forEach(annItem -> {
                AnnotatedItem annotatedItem = new AnnotatedItem();
                annotatedItem.setJavaFilePath(javaFilePath);
                annotatedItem.setAnnotationName(annItem.getNameAsString());

                JItem jItem = new JItem();
                Node parentNode = annItem.getParentNode().get();
                if (parentNode instanceof ClassOrInterfaceDeclaration) {
                    jItem.setName(((ClassOrInterfaceDeclaration) parentNode).getNameAsString());
                    jItem.setType(Constants.TYPE_CLASS);
                } else if (parentNode instanceof FieldDeclaration) {
                    // TODO: Do we need to the variable name?
                    jItem.setName(((FieldDeclaration) parentNode).toString());
                    jItem.setType(Constants.TYPE_FIELD);
                } else if (parentNode instanceof MethodDeclaration) {
                    jItem.setName(((MethodDeclaration) parentNode).getNameAsString());
                    jItem.setType(Constants.TYPE_METHOD);
                } else if (parentNode instanceof ConstructorDeclaration) {
                    jItem.setName(((ConstructorDeclaration) parentNode).getNameAsString());
                    jItem.setType(Constants.TYPE_CONSTRUCTOR);
                }
                annotatedItem.setEntity(jItem);

                this.annotatedItems.add(annotatedItem);
            });
        });

        return this.annotatedItems;
    }
}
