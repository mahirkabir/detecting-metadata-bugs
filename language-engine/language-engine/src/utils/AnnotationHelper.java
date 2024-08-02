package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import engine.EngineFactory;
import engine.IEngineCache;
import models.AnnotationAttrItem;
import models.AnnotationItem;

public class AnnotationHelper {
    private String javaFilePath;
    private IEngineCache engineCache;

    /**
     * Used for extracting field items from a java file
     * 
     * @param javaFilePath
     */
    public AnnotationHelper(String javaFilePath) {
        super();
        this.javaFilePath = javaFilePath;
        this.engineCache = EngineFactory.getEngineCache();
    }

    public List<AnnotationItem> GetAnnotations() {
        List<AnnotationItem> annotations = new ArrayList<AnnotationItem>();

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
        
        /**
         * We might need to remove comments for all the compilation unit related code
         * We did it here to prevent //s getting reported as annotation attribute
         * Note: Can we generated the compilation unit centrally?
         */
        cu.getAllComments().forEach(Comment::remove);
        List<ClassOrInterfaceDeclaration> classDecls = cu
                .findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .collect(Collectors.toList());

        if (classDecls == null)
            return annotations;

        for (ClassOrInterfaceDeclaration decl : classDecls) {
            System.out.println("========");
            try {
                String className = decl.getNameAsString();
                if (decl.getAnnotations() == null)
                    continue;
                decl.getAnnotations().forEach(item -> {
                    AnnotationItem annotationItem = new AnnotationItem();
                    annotationItem.setParentEntity(className);
                    annotationItem.setAnnotationName(item.getNameAsString());
                    annotationItem.setAnnotationType(Constants.ANNOTATION_CLASS);
                    List<AnnotationAttrItem> attrs = new ArrayList<AnnotationAttrItem>();

                    if (item instanceof NormalAnnotationExpr) {
                        item.getChildNodes().forEach(paramItem -> {
                            if (paramItem.getChildNodes().size() >= 1) {
                                List<Node> childNodes = paramItem.getChildNodes();
                                if (childNodes.size() > 1) {
                                    if (childNodes.get(1) instanceof ArrayInitializerExpr) {
                                        ArrayInitializerExpr attrValArray = (ArrayInitializerExpr) childNodes.get(1);
                                        if (attrValArray.getChildNodes().size() > 0) {
                                            attrValArray.getChildNodes().forEach(attrValItem -> {
                                                if (attrValItem instanceof StringLiteralExpr) {
                                                    AnnotationAttrItem attr = new AnnotationAttrItem();
                                                    attr.setAnnotationAttrName(
                                                            paramItem.toString().split("=")[0].strip());
                                                    String paramValue = attrValItem.toString();
                                                    attr.setAnnotationAttrValue(paramValue);
                                                    attrs.add(attr);
                                                }
                                            });
                                        }
                                    } else {
                                        AnnotationAttrItem attr = new AnnotationAttrItem();
                                        attr.setAnnotationAttrName(paramItem.toString().split("=")[0].strip());
                                        String paramValue = childNodes.get(1).toString();
                                        attr.setAnnotationAttrValue(paramValue);
                                        attrs.add(attr);
                                    }
                                }

                            }
                        });

                    } else if (item instanceof SingleMemberAnnotationExpr) {
                        item.getChildNodes().forEach(paramItem -> {
                            if (paramItem instanceof ClassExpr) {
                                AnnotationAttrItem attr = new AnnotationAttrItem();
                                attr.setAnnotationAttrName(paramItem.toString());
                                attrs.add(attr);
                            } else if (paramItem instanceof ArrayInitializerExpr) {
                                paramItem.getChildNodes().forEach(exprItem -> {
                                    if (exprItem instanceof ClassExpr) {
                                        AnnotationAttrItem attr = new AnnotationAttrItem();
                                        attr.setAnnotationAttrName(exprItem.toString());
                                        attrs.add(attr);
                                    }
                                });
                            }
                        });
                    }

                    annotationItem.setAnnotationAttrs(attrs);
                    annotations.add(annotationItem);
                });
            } catch (Exception ex) {
                Logger.log("Error parsing annotations from: " + javaFilePath + " => " + ex.toString());
            }
        }

        return annotations;
    }

    /**
     * Get all annotations for the method/constructor
     * 
     * @param decl
     * @return
     */
    public List<AnnotationItem> getCallableAnnotations(CallableDeclaration decl) {
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
                SingleMemberAnnotationExpr annoExpr = (SingleMemberAnnotationExpr) annotation;
            }

            annotationItems.add(annItem);
        }

        return annotationItems;
    }
}
