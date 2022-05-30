package engine;

import java.util.List;

import models.ClassItem;
import utils.ClassHelper;

public class EngineMain {
	public static void main(String args[]) {
		ClassHelper cHelper = new ClassHelper(
				"C:\\Mahir\\VT\\Research\\detecting-metadata-bugs\\src\\detecting-metadata-bugs\\test\\dir-test");
		List<ClassItem> classes = cHelper.getClasses();
		System.out.println(classes.size());
		// System.out.println(classes.get(0).getShortName());
	}
}
