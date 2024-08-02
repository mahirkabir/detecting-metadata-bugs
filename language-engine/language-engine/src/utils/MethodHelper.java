package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import engine.EngineFactory;
import engine.IEngineCache;
import models.AnnotationAttrItem;
import models.AnnotationItem;
import models.MethodItem;

public class MethodHelper {
    private String javaFilePath;
    private IEngineCache engineCache;

    /**
     * Used for extracting field items from a java file
     * 
     * @param javaFilePath
     */
    public MethodHelper(String javaFilePath) {
        super();
        this.javaFilePath = javaFilePath;
        this.engineCache = EngineFactory.getEngineCache();
    }

    public List<MethodItem> GetMethods() {
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

        List<MethodItem> methods = new ArrayList<MethodItem>();
        List<MethodDeclaration> methodDecls = cu.findAll(MethodDeclaration.class);
        if (methodDecls != null) {
            for (MethodDeclaration decl : methodDecls) {
                try {
                    Node parentNode = decl.getParentNode().get();
                    String methodClass = "";
                    while (parentNode != null && !(parentNode instanceof ClassOrInterfaceDeclaration
                            || parentNode instanceof EnumDeclaration))
                        parentNode = parentNode.getParentNode().get();
                    if (parentNode != null && parentNode instanceof NodeWithSimpleName) {
                        methodClass = ((NodeWithSimpleName<VariableDeclarator>) parentNode).getNameAsString();
                    }

                    String className = methodClass;

                    MethodItem methodItem = new MethodItem();
                    methodItem.setName(decl.getNameAsString());
                    methodItem.setType(decl.getTypeAsString());
                    methodItem.setClassName(className);
                    methodItem.setAnnotations(this.getMethodAnnotations(decl));

                    List<String> modifiers = new ArrayList<String>();
                    if (decl.getModifiers() != null)
                        decl.getModifiers().forEach(item -> {
                            modifiers.add(item.toString());
                        });

                    if (modifiers.size() >= 1)
                        methodItem.setAccessModifier(modifiers.get(0));
                    if (modifiers.size() == 2)
                        methodItem.setDeclType(modifiers.get(1));

                    methods.add(methodItem);
                } catch (Exception ex) {
                    Logger.log("Error parsing methods from: " + javaFilePath + " => " + ex.toString());
                }
            }
        }

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

            if (annotation instanceof NormalAnnotationExpr) {
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
            } else if (annotation instanceof MarkerAnnotationExpr) {
                // No attribute for MarkerAnnotatiionExpr
                // No need to do anything about it
                MarkerAnnotationExpr annoExpr = (MarkerAnnotationExpr) annotation;
            } else if (annotation instanceof SingleMemberAnnotationExpr) {
                // Single parameter w/o values for SingleMemberAnnotationExpr
                // Need to collect the single parameter
                SingleMemberAnnotationExpr annExpr = (SingleMemberAnnotationExpr) annotation;
                String annAttrValue = annExpr.getMemberValue().asStringLiteralExpr().getValue();
                annItem.addAnnotationAttr(new AnnotationAttrItem("", annAttrValue));
            }

            annotationItems.add(annItem);
        }

        return annotationItems;
    }
}
