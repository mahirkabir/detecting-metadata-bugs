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
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import models.InvocationItem;

public class InvocationHelper {
    private String javaFilePath;

    /**
     * Used for extracting field items from a java file
     * 
     * @param javaFilePath
     */
    public InvocationHelper(String javaFilePath) {
        super();
        this.javaFilePath = javaFilePath;
    }

    public List<InvocationItem> GetInvocations() {
        List<InvocationItem> invocations = new ArrayList<InvocationItem>();

        Path path = Paths.get(this.javaFilePath);
        String filename = path.getFileName().toString();
        String folder = path.getParent().toString();

        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
        SourceRoot sourceRoot = new SourceRoot(
                CodeGenerationUtils.mavenModuleRoot(InvocationHelper.class)
                        .resolve(folder));
        CompilationUnit cu = sourceRoot.parse("", filename);

        Map<String, MethodCallExpr> declInvocations = cu
                .findAll(MethodCallExpr.class)
                .stream()
                .collect(Collectors.toMap(NodeWithSimpleName::getNameAsString, Function.identity()));

        for (Map.Entry<String, MethodCallExpr> mapElm : declInvocations.entrySet()) {
            MethodCallExpr decl = mapElm.getValue();

            System.out.println("========");

            Node classNode = decl
                    .getParentNode().get()
                    .getParentNode().get()
                    .getParentNode().get()
                    .getParentNode().get();
            @SuppressWarnings("unchecked")
            String className = ((NodeWithSimpleName<VariableDeclarator>) classNode)
                    .getNameAsString();
            Node parentNode = decl.getParentNode().get();
            String invocationStmnt = parentNode.toString();
            String callee = decl.getNameAsString();
            List<String> arguments = new ArrayList<String>();
            decl.getArguments().forEach(item -> {
                arguments.add(item.toString());
            });

            InvocationItem invocationItem = new InvocationItem();
            invocationItem.setCallee(callee);
            invocationItem.setCaller(className);
            invocationItem.setInvocationStmnt(invocationStmnt);
            invocationItem.setArguments(arguments);
            invocations.add(invocationItem);
        }

        return invocations;
    }
}
