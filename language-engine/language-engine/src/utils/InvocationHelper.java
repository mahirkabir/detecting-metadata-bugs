package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import engine.EngineFactory;
import engine.IEngineCache;
import models.InvocationItem;

public class InvocationHelper {
    private String javaFilePath;
    private IEngineCache engineCache;

    /**
     * Used for extracting field items from a java file
     * 
     * @param javaFilePath
     */
    public InvocationHelper(String javaFilePath) {
        super();
        this.javaFilePath = javaFilePath;
        this.engineCache = EngineFactory.getEngineCache();
    }

    public List<InvocationItem> GetInvocations() {
        List<InvocationItem> invocations = new ArrayList<InvocationItem>();

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

        List<MethodCallExpr> declInvocations = cu
                .findAll(MethodCallExpr.class)
                .stream()
                .collect(Collectors.toList());

        if (declInvocations == null)
            return invocations;

        for (MethodCallExpr decl : declInvocations) {
            System.out.println("========");

            try {
                StringBuilder className = new StringBuilder("");
                Node classNode = decl.getParentNode().get();
                while (classNode != null && !(classNode instanceof ClassOrInterfaceDeclaration)) {
                    classNode = classNode.getParentNode().get();
                }
                if (classNode instanceof ClassOrInterfaceDeclaration) {
                    className.append(((NodeWithSimpleName<VariableDeclarator>) classNode).getNameAsString());
                }

                Node parentNode = decl.getParentNode().get();
                String invocationStmnt = parentNode.toString();
                String callee = decl.getNameAsString();

                Expression callerExpr = decl.getScope().orElse(null);
                String callerVariable = "";
                if (callerExpr != null) {
                    String[] methodCallParts = callerExpr.toString().split("\\.");
                    if (methodCallParts.length > 1) {
                        callerVariable = methodCallParts[methodCallParts.length - 1];
                    }
                }

                List<String> arguments = new ArrayList<String>();
                if (arguments != null) {
                    decl.getArguments().forEach(item -> {
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
            } catch (Exception ex) {
                Logger.log("Error parsing invocations from: " + javaFilePath + " => " + ex.toString());
            }
        }

        return invocations;
    }
}
