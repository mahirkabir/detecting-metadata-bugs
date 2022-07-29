package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import models.FieldItem;
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
        List<MethodItem> methods = new ArrayList<MethodItem>();

        Path path = Paths.get(this.javaFilePath);
        String filename = path.getFileName().toString();
        String folder = path.getParent().toString();

        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
        SourceRoot sourceRoot = new SourceRoot(
                CodeGenerationUtils.mavenModuleRoot(MethodHelper.class)
                        .resolve(folder));
        CompilationUnit cu = sourceRoot.parse("", filename);

        Map<String, MethodDeclaration> declMethods = cu
                .findAll(MethodDeclaration.class)
                .stream()
                .collect(Collectors.toMap(NodeWithSimpleName::getNameAsString, Function.identity()));

        for (Map.Entry<String, MethodDeclaration> mapElm : declMethods.entrySet()) {
            MethodDeclaration decl = mapElm.getValue();

            System.out.println("========");

            Node parentNode = decl.getParentNode().get();
            @SuppressWarnings("unchecked")
            String className = ((NodeWithSimpleName<VariableDeclarator>) parentNode).getNameAsString();
            System.out.println(className);

            MethodItem methodItem = new MethodItem(
                    decl.getNameAsString(), decl.getTypeAsString(), className);

            List<String> modifiers = new ArrayList<String>();
            decl.getModifiers().forEach(item -> {
                modifiers.add(item.toString());
            });

            if (modifiers.size() >= 1)
                methodItem.setAccessModifier(modifiers.get(0));
            if (modifiers.size() == 2)
                methodItem.setDeclType(modifiers.get(1));

            methods.add(methodItem);

            System.out.println("========");
        }

        return methods;
    }
}
