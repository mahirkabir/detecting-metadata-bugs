import os
from tqdm import tqdm
import helper


def get_month(month):
    """Get month number from month name"""
    dict = {
        "Jan": 1, "Feb": 2, "Mar": 3, "Apr": 4,
        "May": 5, "Jun": 6, "Jul": 7, "Aug": 8,
        "Sep": 9, "Oct": 10, "Nov": 11, "Dec": 12
    }
    return dict[month]


if __name__ == "__main__":
    """Find commit dates from commit id"""
    dataset_loc = "D:\Mahir\detecting-metadata-bugs\dataset\projects"
    root = os.getcwd()
    result = {}
    with open(os.path.join(root, "unique_commits.txt"), "r", encoding="utf-8", errors="ignore") as reader:
        lines = reader.readlines()
        proj = ""
        proj_loc = ""
        for line in tqdm(lines):
            line = line.strip()
            if "Project: " in line:
                proj = line.replace("Project: ", "")
                proj_loc = os.path.join(dataset_loc, proj)
                result[proj] = []
            else:
                commit_id = line
                try:
                    result, output = helper.execute_cmd(
                        proj_loc, "git show %s" % commit_id)
                    output_lines = output.split("\n")
                    for oline in output_lines:
                        if "Date: " in oline:
                            parts = oline.split(" ")
                            year, month, day = parts[7], get_month(
                                parts[4]), parts[5]

                            result[proj].append("%s\t(%s-%s-%s)" %
                                                (commit_id, year, month, day))
                except Exception as ex:
                    print("Error. Project: %s. Commit: %s => %s" %
                          (proj, commit_id, str(ex)))

    with open(os.path.join(root, "commit_dates.txt"), "w", encoding="utf-8", errors="ignore") as writer:
        for proj in tqdm(result):
            writer.write("%s: (%s)\n" % (proj, len(result[proj])))
            for commit in result[proj]:
                writer.write("%s\n" % commit)
