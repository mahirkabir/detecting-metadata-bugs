package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import engine.EngineFactory;
import engine.IEngineCache;
import models.VariableItem;

/**
 * This helper will help collect locally declared variables in a java file.
 * They are not the same as the instance variables, which are taken care of
 * by FieldHelper class
 */
public class VariableHelper {
    private String javaFilePath;
    private IEngineCache engineCache;

    /**
     * Used for extracting local variables from a java file
     * 
     * @param javaFilePath
     */
    public VariableHelper(String javaFilePath) {
        super();
        this.javaFilePath = javaFilePath;
        this.engineCache = EngineFactory.getEngineCache();
    }

    public List<VariableItem> GetVariables() {
        List<VariableItem> variables = new ArrayList<VariableItem>();

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

        List<VariableDeclarationExpr> declVariables = cu
                .findAll(VariableDeclarationExpr.class)
                .stream()
                .collect(Collectors.toList());

        if (declVariables == null)
            return variables;

        for (VariableDeclarationExpr decl : declVariables) {
            try {
                Node parentNode = decl.getParentNode().get();
                String variableClass = "";
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

                if (parentNode != null && parentNode instanceof NodeWithSimpleName) {
                    variableClass = ((NodeWithSimpleName<VariableDeclarator>) parentNode).getNameAsString();
                }

                String className = variableClass;
                String parentMethod = variableMethod;
                List<String> modifiers = new ArrayList<String>();
                decl.getModifiers().forEach(item -> {
                    modifiers.add(item.toString());
                });

                if (decl.getVariables() != null) {
                    decl.getVariables().forEach(item -> {
                        VariableItem variableItem = new VariableItem();
                        variableItem.setName(item.getNameAsString());
                        variableItem.setType(item.getTypeAsString());
                        variableItem.setClassName(className);
                        variableItem.setParentMethod(parentMethod);
                        if (modifiers.size() >= 1)
                            variableItem.setAccessModifier(modifiers.get(0));
                        if (modifiers.size() == 2)
                            variableItem.setDeclType(modifiers.get(1));

                        variables.add(variableItem);
                    });
                }
            } catch (Exception ex) {
                Logger.log("Error parsing variables from: " + javaFilePath + " => " + ex.toString());
            }
        }

        return variables;
    }
}
