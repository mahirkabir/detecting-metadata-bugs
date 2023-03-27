package utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import engine.EngineFactory;
import engine.IEngineCache;
import models.AnnotatedItem;
import models.AnnotationItem;
import models.ClassItem;
import models.FieldItem;
import models.InvocationItem;
import models.JItem;
import models.MethodItem;
import models.ObjectCreationItem;
import models.VariableItem;

/*
 * ClassHelper version that uses lazy analysis to explore different branches of the class tree
 * e.g. - Fields, Methods, Invocations etc. will not be explored unless they are requested
 * To use this version, first rename it back to ClassHelper,
 * Then use this to class to instantiate the classHelper variables in the project
 */
public class ClassHelper_Lazy {
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
    public ClassHelper_Lazy(String projectPath) {
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
            // List<FieldItem> fields = new FieldHelper(javaFile).GetFields();
            // List<MethodItem> methods = new MethodHelper(javaFile).GetMethods();
            // List<InvocationItem> invocations = new
            // InvocationHelper(javaFile).GetInvocations();
            // List<AnnotationItem> annotations = new
            // AnnotationHelper(javaFile).GetAnnotations();

            try {
                List<ClassItem> classInstances = this.GetClassInstances(javaFile);

                if (classInstances == null)
                    continue;

                classInstances.forEach(classInstance -> {
                    // classInstance is one of the classes located in javaFile
                    classes.add(classInstance);
                    this.dictClass.put(classInstance.getFqn(), classInstance);
                    if (!this.dictSNClass.containsKey(classInstance.getName()))
                        this.dictSNClass.put(classInstance.getName(), new ArrayList<>());
                    this.dictSNClass.get(classInstance.getName()).add(classInstance);
                });
            } catch (Exception ex) {
                Logger.log("getClasses() => Error parsing file: " + javaFile);
            }
        }

        return classes;
    }

    /**
     * Populates classname & class fqn for classes located in javaFilePath
     * 
     * @param javaFilePath
     * @return
     */
    private List<ClassItem> GetClassInstances(String javaFilePath) {
        List<ClassItem> classInstances = new ArrayList<>();

        Path path = Paths.get(javaFilePath);
        String filename = path.getFileName().toString();
        String folder = path.getParent().toString();

        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
        SourceRoot sourceRoot = new SourceRoot(
                CodeGenerationUtils.mavenModuleRoot(FieldHelper.class)
                        .resolve(folder));
        CompilationUnit cu = sourceRoot.parse("", filename);

        List<ClassOrInterfaceDeclaration> classes = cu
                .findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .collect(Collectors.toList());

        String packageName = null;
        if (classes == null)
            return classInstances;

        for (ClassOrInterfaceDeclaration decl : classes) {

            System.out.println("========");

            ClassItem classItem = new ClassItem(javaFilePath);
            Node parentNode = decl.getParentNode().get();
            if (packageName == null) {
                // Only look for the package for the first class.
                List<Node> children = parentNode.getChildNodes();

                if (children == null)
                    continue;

                for (Node item : children) {
                    if (item.toString().contains(Constants.PACKAGE)) {
                        if (item.getChildNodes().size() > 0) {
                            packageName = item.getChildNodes().get(0).toString();
                            break;
                        }
                    }
                }
            }

            String classname = decl.getNameAsString();
            classItem.setName(classname);
            if (packageName == null || packageName.isEmpty())
                classItem.setFqn(classname);
            else
                classItem.setFqn(packageName + "." + classname);

            classInstances.add(classItem);
        }

        return classInstances;
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
