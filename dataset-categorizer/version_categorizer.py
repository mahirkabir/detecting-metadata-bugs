def get_presence(filename):
    """Get presence of commits in `filename`"""
    dict_presence = {}
    proj = ""
    with open(filename, "r", encoding="utf-8", errors="ignore") as reader:
        lines = reader.readlines()
        for line in lines:
            line = line.strip()
            if "Project: " in line:
                proj = line.replace("Project: ", "")
                dict_presence[proj] = {}
            else:
                parts = line.split("||")
                commits = parts[1:]
                for commit in commits:
                    dict_presence[proj][commit] = True

    return dict_presence


if __name__ == "__main__":
    """Categorize versions based on their presence in new_beans.txt, news_annotations.txt, & new_junits.txt"""
    dict_beans = get_presence("new_beans.txt")
    dict_annotations = get_presence("new_annotations.txt")
    dict_junits = get_presence("new_junits.txt")

    result = ""
    dict_versions = {}
    with open("sorted_unique_commits.txt", "r", encoding="utf-8", errors="ignore") as reader:
        lines = reader.readlines()
        for line in lines:
            line = line.strip()
            if ": (" in line:
                proj = line.split(":")[0]
                dict_versions[proj] = {}
                result += line + "\n"
            else:
                parts = line.split("\t")
                commit = parts[0]
                if commit in dict_beans[proj]:
                    line += "\tBeans=1"
                else:
                    line += "\tBeans=0"

                if commit in dict_annotations[proj]:
                    line += "\tAnnotations=1"
                else:
                    line += "\tAnnotations=0"

                if commit in dict_junits[proj]:
                    line += "\tJUnits=1"
                else:
                    line += "\tJUnits=0"

                result += line + "\n"

    with open("version_categories.txt", "w", encoding="utf-8", errors="ignore") as writer:
        writer.write(result)
