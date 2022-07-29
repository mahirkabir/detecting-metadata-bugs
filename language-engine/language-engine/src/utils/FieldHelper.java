package utils;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;

import models.FieldItem;

public class FieldHelper {
    private String javaFilePath;

    /**
     * Used for extracting field items from a java file
     * 
     * @param javaFilePath
     */
    public FieldHelper(String javaFilePath) {
        super();
        this.javaFilePath = javaFilePath;
    }

    public List<FieldItem> GetFields() {
        List<FieldItem> fields = new ArrayList<FieldItem>();

        Path path = Paths.get(this.javaFilePath);
        String filename = path.getFileName().toString();
        String folder = path.getParent().toString();

        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
        SourceRoot sourceRoot = new SourceRoot(
                CodeGenerationUtils.mavenModuleRoot(FieldHelper.class)
                        .resolve(folder));
        CompilationUnit cu = sourceRoot.parse("", filename);

        Map<String, FieldDeclaration> declFields = cu
                .findAll(FieldDeclaration.class)
                .stream()
                .collect(Collectors.toMap(declaration -> declaration.getVariable(0).getNameAsString(),
                        Function.identity()));

        for (Map.Entry<String, FieldDeclaration> mapElm : declFields.entrySet()) {
            FieldDeclaration decl = mapElm.getValue();

            System.out.println("========");

            Node parentNode = decl.getParentNode().get();
            @SuppressWarnings("unchecked")
            String className = ((NodeWithSimpleName<VariableDeclarator>) parentNode).getNameAsString();
            System.out.println(className);

            List<String> modifiers = new ArrayList<String>();
            decl.getModifiers().forEach(item -> {
                modifiers.add(item.toString());
            });

            decl.getVariables().forEach(item -> {
                FieldItem fieldItem = new FieldItem(
                        item.getNameAsString(), item.getTypeAsString(), className);
                if (modifiers.size() >= 1)
                    fieldItem.setAccessModifier(modifiers.get(0));
                if (modifiers.size() == 2)
                    fieldItem.setDeclType(modifiers.get(1));

                fields.add(fieldItem);
            });

            System.out.println("========");
        }

        return fields;
    }
}
