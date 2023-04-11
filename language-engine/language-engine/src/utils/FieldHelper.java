package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import engine.EngineFactory;
import engine.IEngineCache;
import models.FieldItem;

public class FieldHelper {
    private String javaFilePath;
    private IEngineCache engineCache;

    /**
     * Used for extracting field items from a java file
     * 
     * @param javaFilePath
     */
    public FieldHelper(String javaFilePath) {
        super();
        this.javaFilePath = javaFilePath;
        this.engineCache = EngineFactory.getEngineCache();
    }

    public List<FieldItem> GetFields() {
        Path path = Paths.get(this.javaFilePath);
        String filename = path.getFileName().toString();
        String folder = path.getParent().toString();

        CompilationUnit cu = this.engineCache.getLoadedAST(javaFilePath);
        if (cu == null) {
            Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
            SourceRoot sourceRoot = new SourceRoot(
                    CodeGenerationUtils.mavenModuleRoot(FieldHelper.class)
                            .resolve(folder));

            cu = sourceRoot.parse("", filename);
            this.engineCache.setLoadedAST(javaFilePath, cu);
        }

        List<FieldItem> fields = new ArrayList<FieldItem>();
        List<FieldDeclaration> fieldDecls = cu.findAll(FieldDeclaration.class);
        if (fieldDecls != null) {
            for (FieldDeclaration decl : fieldDecls) {
                try {
                    Node parentNode = decl.getParentNode().get();
                    String fieldClass = "";
                    while (parentNode != null && !(parentNode instanceof ClassOrInterfaceDeclaration
                            || parentNode instanceof EnumDeclaration))
                        parentNode = parentNode.getParentNode().get();
                    if (parentNode != null && parentNode instanceof NodeWithSimpleName) {
                        fieldClass = ((NodeWithSimpleName<VariableDeclarator>) parentNode).getNameAsString();
                    }

                    String className = fieldClass;
                    List<String> modifiers = new ArrayList<String>();
                    decl.getModifiers().forEach(item -> {
                        modifiers.add(item.toString());
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
                } catch (Exception ex) {
                    Logger.log("Error parsing fields from: " + javaFilePath + " => " + ex.toString());
                }
            }
        }

        return fields;
    }
}
