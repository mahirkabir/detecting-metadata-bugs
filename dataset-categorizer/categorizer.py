import os
import sys
from tqdm import tqdm
import re


def has_content(content, regex):
    """Check if `regex` is found in `content`"""
    sresult = re.search(regex, content)
    if sresult:
        return True
    return False


if __name__ == "__main__":
    """Check the testing framework added in the project"""
    dataset_loc = sys.argv[1]
    dirs = os.listdir(dataset_loc)

    with open("categorizer.csv", "w", encoding="utf-8", errors="ignore") as writer:
        writer.write("Project\tBeans\tAnnotations\tJUnit\n")

    for dir in tqdm(dirs):
        """Check if Beans/Annotations/JUnit are used"""
        try:

            project_loc = os.path.join(dataset_loc, dir)
            categories = {"beans": False,
                          "annotations": False, "junit": False}
            for croot, cdirs, cfiles in os.walk(project_loc):
                for cfile in cfiles:
                    cfile = os.path.join(croot, cfile)
                    if cfile.endswith(".xml"):
                        try:
                            with open(cfile, "r", encoding="utf-8", errors="ignore") as xml:
                                content = xml.read()
                                if categories["beans"] == False and "</beans>" in content:
                                    categories["beans"] = True
                        except Exception as ex:
                            print("Error processing %s: %s" % (cfile, ex))
                    elif cfile.endswith(".java"):
                        try:
                            with open(cfile, "r", encoding="utf-8", errors="ignore") as javaFile:
                                content = javaFile.read()
                                if categories["junit"] == False and "org.junit" in content:
                                    categories["junit"] = True
                                if categories["annotations"] == False and has_content(content, "@(.)+"):
                                    categories["annotations"] = True
                        except Exception as ex:
                            print("Error processing %s: %s" % (cfile, ex))

                    if categories["beans"] and categories["annotations"] and categories["junit"]:
                        break
                if categories["beans"] and categories["annotations"] and categories["junit"]:
                    break

            with open("categorizer.csv", "a", encoding="utf-8", errors="ignore") as writer:
                writer.write("%s\t%s\t%s\t%s\n" % (
                    dir, categories["beans"], categories["annotations"], categories["junit"]))

        except Exception as ex:
            print("Error processing " + str(dir) + ": " + str(ex))
