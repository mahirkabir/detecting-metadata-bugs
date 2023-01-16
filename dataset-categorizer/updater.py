import sys
import os
from tqdm import tqdm
import helper


def count_dirs(root_folder):
    """Count total number of git repositories inside `root_folder`"""
    total_dirs = 0

    for root, dirs, _ in os.walk(root_folder):
        if ".git" in dirs:
            total_dirs += 1
            dirs[:] = []  # Stop propagation inside git project

    return total_dirs


def update_repos(root_folder, log_file):
    """Recursively update all the .git projects under `root_folder`"""
    total_dirs = count_dirs(root_folder)
    helper.init_log(log_file)
    helper.log(log_file, "Total git repositories: " + str(total_dirs))
    with tqdm(total=total_dirs) as pbar:
        for root, dirs, _ in os.walk(root_folder):
            if ".git" in dirs:
                """This is a git repository"""
                try:
                    helper.log(log_file, "Git updating: " + str(root))
                    res, output = helper.execute_cmd(root, "git pull")
                    if res == False:
                        helper.error(log_file, output)
                    else:
                        helper.log(log_file, "Updated: " + str(root))

                except Exception as ex:
                    helper.log(log_file, "Error updating: " + str(ex))
                pbar.update(1)  # increment the progressbar


if __name__ == "__main__":
    """Update the dataset git projects"""
    root_folder = sys.argv[1]
    log_file = sys.argv[2]

    print("Start walking from: " + sys.argv[1])
    update_repos(root_folder, log_file)
    print("End")
