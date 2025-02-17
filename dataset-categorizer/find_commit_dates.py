from datetime import datetime
import os
from tqdm import tqdm
import helper
from functools import partialmethod
import re


def get_month(month):
    """Get month number from month name"""
    dict = {
        "Jan": 1, "Feb": 2, "Mar": 3, "Apr": 4,
        "May": 5, "Jun": 6, "Jul": 7, "Aug": 8,
        "Sep": 9, "Oct": 10, "Nov": 11, "Dec": 12
    }
    return dict[month]


if __name__ == "__main__OFF":
    """Find commit dates from commit id"""
    dataset_loc = "D:\Mahir\detecting-metadata-bugs\dataset\projects"
    root = os.getcwd()
    dict_result = {}

    # Disable tqdm to avoid printing unnecessary progress bars
    tqdm.__init__ = partialmethod(tqdm.__init__, disable=True)
    # Initialize the output file
    writer = open(os.path.join(root, "commit_dates.txt"),
                  "w", encoding="utf-8", errors="ignore")
    writer.close()

    with open(os.path.join(root, "unique_commits.txt"), "r", encoding="utf-8", errors="ignore") as reader:
        lines = reader.readlines()
        proj = ""
        proj_loc = ""
        for line in tqdm(lines, disable=False):
            line = line.strip()
            if "Project: " in line:
                if proj != "":
                    """Print the last project's commits"""
                    with open(os.path.join(root, "commit_dates.txt"), "a", encoding="utf-8", errors="ignore") as writer:
                        writer.write("%s: (%s)\n" %
                                     (proj, len(dict_result[proj])))
                        for commit in dict_result[proj]:
                            writer.write("%s\n" % commit)

                proj = line.replace("Project: ", "")
                proj_loc = os.path.join(dataset_loc, proj)
                dict_result[proj] = []
            else:
                commit_id = line
                try:
                    _, output = helper.execute_cmd(
                        proj_loc, "git show %s" % commit_id)
                    output_lines = output.split("\n")
                    for oline in output_lines:
                        if "Date: " in oline:
                            parts = oline.split(" ")
                            year, month, day = parts[7], get_month(
                                parts[4]), parts[5]

                            dict_result[proj].append("%s\t(%s-%s-%s)" %
                                                     (commit_id, year, month, day))
                            break
                except Exception as ex:
                    print("Error. Project: %s. Commit: %s => %s" %
                          (proj, commit_id, str(ex)))

    if proj != "":
        """Print the last project's commits"""
        with open(os.path.join(root, "commit_dates.txt"), "a", encoding="utf-8", errors="ignore") as writer:
            writer.write("%s: (%s)\n" %
                         (proj, len(dict_result[proj])))
            for commit in dict_result[proj]:
                writer.write("%s\n" % commit)

if __name__ == "__main__":
    """Sort project commit lines based on commit count -> commit date"""
    root = os.getcwd()
    dict_result = {}
    with open(os.path.join(root, "commit_dates.txt"), "r", encoding="utf-8", errors="ignore") as reader:
        lines = reader.readlines()
        for line in tqdm(lines):
            line = line.strip()
            match = re.search(".*: \(\d+\)", line)
            if match:
                proj = line.split(": ")[0]
                commit_count = line.split(": ")[1][1:-1]
                dict_result[proj] = []
            else:
                [commit_id, commit_date] = line.split("\t")
                dict_result[proj].append(
                    {"commit_id": commit_id, "commit_date": commit_date})

    # Sort based on commit count
    dict_result = {k: v for k, v in sorted(
        dict_result.items(), key=lambda item: int(len(item[1])), reverse=True)}

    tot_versions = 0
    with open(os.path.join(root, "sorted_unique_commits.txt"), "w", encoding="utf-8", errors="ignore") as writer:
        for proj in dict_result:
            tot_versions += len(dict_result[proj])
            writer.write("%s: (%s)\n" % (proj, len(dict_result[proj])))

            # Sort based on date
            dict_result[proj] = sorted(dict_result[proj], key=lambda item: datetime.strptime(
                item["commit_date"][1:-1], "%Y-%m-%d"), reverse=True)

            for commit in dict_result[proj]:
                writer.write("%s\t%s\n" %
                             (commit["commit_id"], commit["commit_date"]))

        writer.write("Total versions to process: %s\n" % tot_versions)
