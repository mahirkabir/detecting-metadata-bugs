package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import models.ArgumentItem;
import models.ObjectCreationItem;

public class ObjectCreationHelper {
    private String javaFilePath;

    /**
     * Used for extracting object creation items from a java file
     * 
     * @param javaFilePath
     */
    public ObjectCreationHelper(String javaFilePath) {
        super();
        this.javaFilePath = javaFilePath;
    }

    public List<ObjectCreationItem> GetObjectCreations() {
        Path path = Paths.get(this.javaFilePath);
        String filename = path.getFileName().toString();
        String folder = path.getParent().toString();

        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
        SourceRoot sourceRoot = new SourceRoot(
                CodeGenerationUtils.mavenModuleRoot(MethodHelper.class)
                        .resolve(folder));
        CompilationUnit cu = sourceRoot.parse("", filename);

        List<ObjectCreationItem> objectCreations = cu
                .findAll(ObjectCreationExpr.class)
                .stream()
                .map(decl -> {
                    Node parentNode = decl.getParentNode().get();

                    StringBuilder sbClassName = new StringBuilder();
                    while (parentNode != null && !(parentNode instanceof ClassOrInterfaceDeclaration)) {
                        parentNode = parentNode.getParentNode().get();
                    }
                    if (parentNode instanceof ClassOrInterfaceDeclaration)
                        sbClassName.append(((ClassOrInterfaceDeclaration) parentNode).getNameAsString());

                    String className = sbClassName.toString();

                    ObjectCreationItem objectCreationItem = new ObjectCreationItem();
                    objectCreationItem.setType(Constants.TYPE_OBJECT_CREATION);
                    objectCreationItem.setClassName(className);
                    objectCreationItem.setDeclType(decl.getTypeAsString());

                    if (decl.getArguments() != null) {
                        decl.getArguments().forEach(argItem -> {
                            ArgumentItem argument = new ArgumentItem();
                            if (argItem instanceof StringLiteralExpr) {
                                argument.setType(Constants.TYPE_STRING);

                                String argumentVal = argItem.toString();
                                if (argumentVal.startsWith("\"")
                                        && argumentVal.endsWith("\""))
                                    argumentVal = argumentVal.substring(1, argumentVal.length() - 1);
                                argument.setValue(argumentVal);
                            }
                            // TODO: Do for other types of arguments

                            objectCreationItem.addArgument(argument);
                        });
                    }

                    return objectCreationItem;
                })
                .collect(Collectors.toList());

        return objectCreations;
    }
}
