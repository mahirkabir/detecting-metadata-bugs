package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import models.AnnotationAttrItem;
import models.AnnotationItem;

public class AnnotationHelper {
    private String javaFilePath;

    /**
     * Used for extracting field items from a java file
     * 
     * @param javaFilePath
     */
    public AnnotationHelper(String javaFilePath) {
        super();
        this.javaFilePath = javaFilePath;
    }

    public List<AnnotationItem> GetAnnotations() {
        List<AnnotationItem> annotations = new ArrayList<AnnotationItem>();

        Path path = Paths.get(this.javaFilePath);
        String filename = path.getFileName().toString();
        String folder = path.getParent().toString();

        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
        SourceRoot sourceRoot = new SourceRoot(
                CodeGenerationUtils.mavenModuleRoot(InvocationHelper.class)
                        .resolve(folder));
        CompilationUnit cu = sourceRoot.parse("", filename);

        Map<String, ClassOrInterfaceDeclaration> fields = cu
                .findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .collect(Collectors.toMap(NodeWithSimpleName::getNameAsString, Function.identity()));

        if (fields == null)
            return annotations;

        for (Map.Entry<String, ClassOrInterfaceDeclaration> mapElm : fields.entrySet()) {
            ClassOrInterfaceDeclaration decl = mapElm.getValue();

            System.out.println("========");

            String className = decl.getNameAsString();
            if (decl.getAnnotations() == null)
                continue;
            decl.getAnnotations().forEach(item -> {
                AnnotationItem annotationItem = new AnnotationItem();
                annotationItem.setParentEntity(className);
                annotationItem.setAnnotationName(item.getNameAsString());
                annotationItem.setAnnotationType(Constants.ANNOTATION_CLASS);
                List<AnnotationAttrItem> attrs = new ArrayList<AnnotationAttrItem>();

                item.getChildNodes().forEach(paramItem -> {
                    if (paramItem.getChildNodes().size() >= 1) {
                        AnnotationAttrItem attr = new AnnotationAttrItem();
                        attr.setAnnotationAttrName(paramItem.toString().split("=")[0].strip());

                        String paramValue = paramItem.getChildNodes().get(1).toString();
                        attr.setAnnotationAttrValue(paramValue);

                        attrs.add(attr);
                    }
                });

                annotationItem.setAnnotationAttrs(attrs);
                annotations.add(annotationItem);
            });
        }

        return annotations;
    }
}
