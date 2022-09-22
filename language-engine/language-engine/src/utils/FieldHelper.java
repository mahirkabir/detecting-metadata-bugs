package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

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
        Path path = Paths.get(this.javaFilePath);
        String filename = path.getFileName().toString();
        String folder = path.getParent().toString();

        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
        SourceRoot sourceRoot = new SourceRoot(
                CodeGenerationUtils.mavenModuleRoot(FieldHelper.class)
                        .resolve(folder));
        CompilationUnit cu = sourceRoot.parse("", filename);

        List<FieldItem> fields = new ArrayList<FieldItem>();
        List<FieldDeclaration> fieldDecls = cu.findAll(FieldDeclaration.class);
        fieldDecls.forEach(decl -> {
            Node parentNode = decl.getParentNode().get();
            @SuppressWarnings("unchecked")
            String className = ((NodeWithSimpleName<VariableDeclarator>) parentNode).getNameAsString();

            List<String> modifiers = new ArrayList<String>();
            decl.getModifiers().forEach(item -> {
                modifiers.add(item.toString());
            });

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
        });

        return fields;
    }
}
