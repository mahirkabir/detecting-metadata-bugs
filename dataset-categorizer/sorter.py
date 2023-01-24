import re


def desc_sort(file):
    """Sort `file` based on relevant-file count"""
    dict = {}

    project = ""
    with open(file, "r") as reader:
        lines = reader.readlines()
        for line in lines:
            line = line.strip()
            sres = re.search("(.)+: \(\d+\)", line)
            if sres:
                project = line.split(":")[0]
                dict[project] = []
            else:
                dict[project].append(line)

    dict = {k: v for k, v in sorted(
        dict.items(), key=lambda item: -len(item[1]))}

    with open(file, "w", encoding="utf-8", errors="ignore") as writer:
        for proj in dict:
            writer.write("%s: (%s)\n" % (proj, len(dict[proj])))
            for file in dict[proj]:
                writer.write("%s\n" % file)

    return dict


if __name__ == "__main__":
    """Descending-sort repositories based on beans, annotations, & junit relevant file count"""
    dict_beans = desc_sort("beans.txt")
    dict_annotations = desc_sort("annotations.txt")
    dict_junits = desc_sort("junits.txt")

    reader = open("categorizer.csv", "r")
    lines = reader.readlines()
    reader.close()

    writer = open("categorizer.csv", "w", encoding="utf-8")
    writer.write(lines[0])
    for line in lines[1:]:
        proj = str(line.split("\t")[0])
        writer.write("%s\t%s\t%s\t%s\n" % (
            proj, str((len(dict_beans[proj]) > 0)),
            str((len(dict_annotations[proj]) > 0)),
            str((len(dict_junits[proj]) > 0))
        ))

    writer.close()
