package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import models.MethodItem;

public class MethodHelper {
    private String javaFilePath;

    /**
     * Used for extracting field items from a java file
     * 
     * @param javaFilePath
     */
    public MethodHelper(String javaFilePath) {
        super();
        this.javaFilePath = javaFilePath;
    }

    public List<MethodItem> GetMethods() {
        Path path = Paths.get(this.javaFilePath);
        String filename = path.getFileName().toString();
        String folder = path.getParent().toString();

        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
        SourceRoot sourceRoot = new SourceRoot(
                CodeGenerationUtils.mavenModuleRoot(MethodHelper.class)
                        .resolve(folder));
        CompilationUnit cu = sourceRoot.parse("", filename);

        List<MethodItem> methods = cu
                .findAll(MethodDeclaration.class)
                .stream()
                .map(decl -> {
                    Node parentNode = decl.getParentNode().get();
                    @SuppressWarnings("unchecked")
                    String className = ((NodeWithSimpleName<VariableDeclarator>) parentNode).getNameAsString();

                    MethodItem methodItem = new MethodItem();
                    methodItem.setName(decl.getNameAsString());
                    methodItem.setType(decl.getTypeAsString());
                    methodItem.setClassName(className);

                    List<String> modifiers = new ArrayList<String>();
                    decl.getModifiers().forEach(item -> {
                        modifiers.add(item.toString());
                    });

                    if (modifiers.size() >= 1)
                        methodItem.setAccessModifier(modifiers.get(0));
                    if (modifiers.size() == 2)
                        methodItem.setDeclType(modifiers.get(1));

                    return methodItem;
                })
                .collect(Collectors.toList());

        return methods;
    }
}
