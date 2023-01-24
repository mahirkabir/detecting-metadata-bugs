import os
import sys
from tqdm import tqdm


def has_annotation(content):
    """Check if any relevant annotation is found in `content`"""
    annotations = ["@Component", "@Service", "@Repository", "@Controller", "@RestController",
                   "@Bean", "@Autowired", "@Qualifier", "@PostConstruct", "@PreDestroy", "@Resource",
                   "@ImportResource", "@RunWith", "@Parameters", "@SuiteClasses", "@Test"]

    for annotation in annotations:
        if annotation in content:
            return True

    return False


if __name__ == "__main__":
    """Check the testing framework added in the project"""
    dataset_loc = sys.argv[1]
    dirs = os.listdir(dataset_loc)

    writer = open("beans.txt", "w", encoding="utf-8", errors="ignore")
    writer.close()
    writer = open("annotations.txt", "w", encoding="utf-8", errors="ignore")
    writer.close()
    writer = open("junits.txt", "w", encoding="utf-8", errors="ignore")
    writer.close()

    for dir in tqdm(dirs):
        """Check if Beans/Annotations/JUnit are used"""
        try:

            project_loc = os.path.join(dataset_loc, dir)
            categories = {"beans": [],
                          "annotations": [], "junit": []}
            for croot, cdirs, cfiles in os.walk(project_loc):
                for cfile in cfiles:
                    cfile = os.path.join(croot, cfile)
                    if cfile.endswith(".xml"):
                        try:
                            with open(cfile, "r", errors="ignore") as xml:
                                content = xml.read()
                                if "</beans>" in content:
                                    categories["beans"].append(cfile)
                        except Exception as ex:
                            print("Error reading: %s" % cfile)
                    elif cfile.endswith(".java"):
                        try:
                            with open(cfile, "r", errors="ignore") as javaFile:
                                content = javaFile.read()
                                if "org.junit" in content:
                                    categories["junit"].append(cfile)
                                if has_annotation(content):
                                    categories["annotations"].append(cfile)
                        except Exception as ex:
                            print("Error reading: %s" % cfile)

            with open("beans.txt", "a", encoding="utf-8", errors="ignore") as writer:
                writer.write("%s: (%s)\n" % (dir, len(categories["beans"])))
                for cfile in categories["beans"]:
                    writer.write("%s\n" % cfile)

            with open("annotations.txt", "a", encoding="utf-8", errors="ignore") as writer:
                writer.write("%s: (%s)\n" %
                             (dir, len(categories["annotations"])))
                for cfile in categories["annotations"]:
                    writer.write("%s\n" % cfile)

            with open("junits.txt", "a", encoding="utf-8", errors="ignore") as writer:
                writer.write("%s: (%s)\n" % (dir, len(categories["junit"])))
                for cfile in categories["junit"]:
                    writer.write("%s\n" % cfile)

        except Exception as ex:
            print("Error processing " + str(dir) + ": " + str(ex))
