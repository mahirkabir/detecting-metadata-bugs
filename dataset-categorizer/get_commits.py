import re
from tqdm import tqdm
import helper
import os


def get_file_commits(filepath):
    """Get all commit numbers of `filepath`"""
    commits = []
    folder, file = os.path.split(filepath)
    _, output = helper.execute_cmd(folder,
                                   "git log --pretty=oneline --follow %s" % file)
    lines = output.split("\n")[:-1]
    for line in lines:
        line = line.strip()
        commit_id = line.split(" ")[0]
        commits.append(commit_id)
    return commits


def get_commits(file, dict_commits):
    """Get commit numbers of files inside result `file`"""
    dict_files = {}
    writer = open("new_" + file, "w", encoding="utf-8", errors="ignore")
    writer.close()
    with open(file, "r", encoding="utf-8", errors="ignore") as reader:
        lines = reader.readlines()
        proj = ""
        rel_file = ""
        for line in tqdm(lines):
            line = line.strip()
            try:
                is_proj = re.search("(.)+: \((\d)+\)", line)
                if is_proj:
                    if proj != "":
                        with open("new_" + file, "a", encoding="utf-8", errors="ignore") as writer:
                            writer.write("Project: %s\n" % proj)
                            for commit in dict_files[proj]:
                                writer.write("%s\n" % commit)

                    proj = line.split(":")[0]
                    dict_files[proj] = []
                else:
                    rel_file = line.strip()
                    commits = get_file_commits(rel_file)
                    new_line = rel_file

                    for commit in commits:
                        if proj not in dict_commits:
                            dict_commits[proj] = {}
                        dict_commits[proj][commit] = True

                        new_line += "||" + commit

                    dict_files[proj].append(new_line)
            except Exception as ex:
                print("Error processing %s: %s" % (line, str(ex)))

    with open("new_" + file, "a", encoding="utf-8", errors="ignore") as writer:
        writer.write("Project: %s\n" % proj)
        for commit in dict_files[proj]:
            writer.write("%s\n" % commit)
    return dict_commits


if __name__ == "__main__":
    """Get commits on beans, annotations, & junits related files"""
    dict_commits = {}
    dict_commits = get_commits("beans.txt", dict_commits)
    dict_commits = get_commits("annotations.txt", dict_commits)
    dict_commits = get_commits("junits.txt", dict_commits)

    with open("unique_commits.txt", "w", encoding="utf-8", errors="ignore") as writer:
        for proj in dict_commits:
            writer.write("Project: %s\n" % proj)
            for commit in dict_commits[proj]:
                writer.write("%s\n" % commit)
