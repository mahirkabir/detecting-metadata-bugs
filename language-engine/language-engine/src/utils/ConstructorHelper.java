package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import models.MethodItem;
import models.ParamItem;

public class ConstructorHelper {
    private String javaFilePath;
    private AnnotationHelper annotationHelper;

    /**
     * Used for extracting constructor items from a java file
     * 
     * @param javaFilePath
     */
    public ConstructorHelper(String javaFilePath) {
        super();
        this.javaFilePath = javaFilePath;
        this.annotationHelper = new AnnotationHelper(javaFilePath);
    }

    public List<MethodItem> GetConstructors() {
        Path path = Paths.get(this.javaFilePath);
        String filename = path.getFileName().toString();
        String folder = path.getParent().toString();

        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
        SourceRoot sourceRoot = new SourceRoot(
                CodeGenerationUtils.mavenModuleRoot(MethodHelper.class)
                        .resolve(folder));
        CompilationUnit cu = sourceRoot.parse("", filename);

        List<MethodItem> constructors = cu
                .findAll(ConstructorDeclaration.class)
                .stream()
                .map(decl -> {
                    Node parentNode = decl.getParentNode().get();
                    @SuppressWarnings("unchecked")
                    String className = ((NodeWithSimpleName<VariableDeclarator>) parentNode).getNameAsString();

                    MethodItem constructorItem = new MethodItem();
                    constructorItem.setName(decl.getNameAsString());
                    constructorItem.setClassName(className);
                    constructorItem.setAnnotations(this.annotationHelper.getCallableAnnotations(decl));
                    constructorItem.setType(Constants.TYPE_CONSTRUCTOR);

                    List<String> modifiers = new ArrayList<String>();
                    decl.getModifiers().forEach(item -> {
                        modifiers.add(item.toString());
                    });

                    if (modifiers.size() >= 1)
                        constructorItem.setAccessModifier(modifiers.get(0));
                    if (modifiers.size() == 2)
                        constructorItem.setDeclType(modifiers.get(1));

                    if (decl.getParameters() != null)
                        decl.getParameters().forEach(item -> {
                            ParamItem paramItem = new ParamItem();
                            paramItem.setName(item.getNameAsString());
                            paramItem.setType(item.getTypeAsString());
                            if (!item.getModifiers().isEmpty())
                                paramItem.setAccessModifier(item.getModifiers().get(0).toString());
                            constructorItem.addParameter(paramItem);
                        });

                    return constructorItem;
                })
                .collect(Collectors.toList());

        return constructors;
    }
}
