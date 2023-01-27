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


def get_commits(root, file, dict_commits, dict_file_commits):
    """Get commit numbers of files inside result `file`"""
    dict_files = {}
    writer = open(os.path.join(root, "new_" + file),
                  "w", encoding="utf-8", errors="ignore")
    writer.close()
    with open(os.path.join(root, file), "r", encoding="utf-8", errors="ignore") as reader:
        lines = reader.readlines()
        proj = ""
        rel_file = ""
        for line in tqdm(lines):
            line = line.strip()
            try:
                is_proj = re.search("(.)+: \((\d)+\)", line)
                if is_proj:
                    if proj != "":
                        with open(os.path.join(root, "new_" + file),
                                  "a", encoding="utf-8", errors="ignore") as writer:
                            writer.write("Project: %s\n" % proj)
                            for commit in dict_files[proj]:
                                writer.write("%s\n" % commit)

                    proj = line.split(":")[0]
                    dict_files[proj] = []
                else:
                    rel_file = line.strip()
                    if rel_file in dict_file_commits:
                        commits = dict_file_commits[rel_file]
                    else:
                        commits = get_file_commits(rel_file)
                    dict_file_commits[rel_file] = commits
                    new_line = rel_file

                    for commit in commits:
                        if proj not in dict_commits:
                            dict_commits[proj] = {}
                        dict_commits[proj][commit] = True

                        new_line += "||" + commit

                    dict_files[proj].append(new_line)
            except Exception as ex:
                print("Error processing %s: %s" % (line, str(ex)))

    with open(os.path.join(root, "new_" + file),
              "a", encoding="utf-8", errors="ignore") as writer:
        writer.write("Project: %s\n" % proj)
        for commit in dict_files[proj]:
            writer.write("%s\n" % commit)

    return dict_commits, dict_file_commits


if __name__ == "__main__":
    """Get commits on beans, annotations, & junits related files"""
    dict_commits = {}
    dict_file_commits = {}
    root = os.getcwd()
    dict_commits, dict_file_commits = get_commits(
        root, "annotations.txt", dict_commits, dict_file_commits)
    dict_commits, dict_file_commits = get_commits(
        root, "beans.txt", dict_commits, dict_file_commits)
    dict_commits, _ = get_commits(
        root, "junits.txt", dict_commits, dict_file_commits)

    tot_versions = 0
    with open(os.path.join(root, "unique_commits.txt"), "w", encoding="utf-8", errors="ignore") as writer:
        for proj in dict_commits:
            tot_versions += len(dict_commits[proj])
            writer.write("Project: %s\n" % proj)
            for commit in dict_commits[proj]:
                writer.write("%s\n" % commit)

        writer.write("Total versions to process: %s\n" % tot_versions)
