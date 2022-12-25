package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import models.AnnotationAttrItem;
import models.AnnotationItem;
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
                    methodItem.setAnnotations(this.getMethodAnnotations(decl));

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

    /**
     * Get all annotations for the method
     * 
     * @param decl
     * @return
     */
    private List<AnnotationItem> getMethodAnnotations(MethodDeclaration decl) {
        List<AnnotationItem> annotationItems = new ArrayList<>();

        List<AnnotationExpr> annotations = decl.getAnnotations();

        if (annotations == null)
            return annotationItems;

        for (AnnotationExpr annotation : annotations) {
            AnnotationItem annItem = new AnnotationItem();
            annItem.setParentEntity(decl.getNameAsString());
            annItem.setAnnotationName(annotation.getNameAsString());
            annItem.setAnnotationType(Constants.ANNOTATION_METHOD);

            NormalAnnotationExpr annExpr = (NormalAnnotationExpr) annotation;
            List<MemberValuePair> annKeyValuePairs = annExpr.getPairs();

            if (annKeyValuePairs != null) {
                for (MemberValuePair pair : annKeyValuePairs) {
                    String annKey = pair.getNameAsString();
                    String annVal = pair.getValue().toString();
                    if (annVal.startsWith("\"") && annVal.endsWith("\""))
                        annVal = annVal.substring(1, annVal.length() - 1);
                    annItem.addAnnotationAttr(new AnnotationAttrItem(annKey, annVal));
                }
            }

            annotationItems.add(annItem);
        }

        return annotationItems;
    }
}
