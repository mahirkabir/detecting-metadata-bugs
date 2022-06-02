package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import models.ClassItem;

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
			ClassItem classItem = new ClassItem();
			classItem.setFields(new FieldHelper(javaFile).GetFields());
			classItem.setMethods(new MethodHelper(javaFile).GetMethods());
			classes.add(classItem);
		}
		return classes;
	}
}
