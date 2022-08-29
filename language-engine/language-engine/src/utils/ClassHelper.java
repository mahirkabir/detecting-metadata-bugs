package utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import models.AnnotationItem;
import models.ClassItem;
import models.FieldItem;
import models.InvocationItem;
import models.MethodItem;

public class ClassHelper {
	private List<String> javaFiles;
	private String projectPath;

	/**
	 * Instantiate project path for the project
	 * 
	 * @param projectPath
	 */
	public ClassHelper(String projectPath) {
		super();
		this.javaFiles = new ArrayList<String>();
		this.projectPath = projectPath;
	}

	/**
	 * Get all java file paths under folder
	 * 
	 * @folder - Folder location
	 * @return
	 */
	public void loadJavaFiles(String folder) {
		File[] files = new File(folder).listFiles();
		if (files != null && files.length > 0) {
			for (File currFile : files) {
				String fullPath = folder + "\\" + currFile.getName().toString();
				if (currFile.isDirectory()) {
					this.loadJavaFiles(fullPath.toString());
				} else {
					if (currFile.getName().endsWith(".java"))
						this.javaFiles.add(fullPath);
				}
			}
		}
	}

	/**
	 * Get all class items in the specified project
	 * 
	 * @return
	 */
	public List<ClassItem> getClasses() {
		List<ClassItem> classes = new ArrayList<ClassItem>();

		this.loadJavaFiles(this.projectPath);
		for (String javaFile : this.javaFiles) {
			// List<FieldItem> fields = new FieldHelper(javaFile).GetFields();
			// List<MethodItem> methods = new MethodHelper(javaFile).GetMethods();
			// List<InvocationItem> invocations = new
			// InvocationHelper(javaFile).GetInvocations();
			// List<AnnotationItem> annotations = new
			// AnnotationHelper(javaFile).GetAnnotations();

			List<ClassItem> classInstances = this.GetClassInstances(javaFile);
			classInstances.forEach(classInstance -> {
				// classInstance is one of the classes located in javaFile
				classes.add(classInstance);
			});
		}

		return classes;
	}

	/**
	 * Populates classname & class fqn for classes located in javaFilePath
	 * 
	 * @param javaFilePath
	 * @return
	 */
	private List<ClassItem> GetClassInstances(String javaFilePath) {
		List<ClassItem> classInstances = new ArrayList<>();

		Path path = Paths.get(javaFilePath);
		String filename = path.getFileName().toString();
		String folder = path.getParent().toString();

		Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
		SourceRoot sourceRoot = new SourceRoot(
				CodeGenerationUtils.mavenModuleRoot(FieldHelper.class)
						.resolve(folder));
		CompilationUnit cu = sourceRoot.parse("", filename);

		Map<String, ClassOrInterfaceDeclaration> classes = cu
				.findAll(ClassOrInterfaceDeclaration.class)
				.stream()
				.collect(Collectors.toMap(NodeWithSimpleName::getNameAsString, Function.identity()));

		String packageName = null;
		for (Map.Entry<String, ClassOrInterfaceDeclaration> mapElm : classes.entrySet()) {
			ClassOrInterfaceDeclaration decl = mapElm.getValue();

			System.out.println("========");

			ClassItem classItem = new ClassItem(filename);
			Node parentNode = decl.getParentNode().get();
			if (packageName == null) {
				// Only look for the package for the first class.
				List<Node> children = parentNode.getChildNodes();
				for (Node item : children) {
					if (item.toString().contains(Constants.PACKAGE)) {
						if (item.getChildNodes().size() > 0) {
							packageName = item.getChildNodes().get(0).toString();
							break;
						}
					}
				}
			}

			String classname = decl.getNameAsString();
			classItem.setName(classname);
			if (packageName.isEmpty())
				classItem.setFqn(classname);
			else
				classItem.setFqn(packageName + "." + classname);

			classInstances.add(classItem);
		}

		return classInstances;
	}
}
