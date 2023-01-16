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


def update_repos(root_folder):
    """Recursively update all the .git projects under `root_folder`"""
    total_dirs = count_dirs(root_folder)
    helper.log("Total git repositories: " + str(total_dirs))
    with tqdm(total=total_dirs) as pbar:
        for root, dirs, _ in os.walk(root_folder):
            if ".git" in dirs:
                """This is a git repository"""
                try:
                    helper.log("Git Updating: " + str(root))
                    res, output = helper.execute_cmd(root, "git pull")
                    if res == False:
                        helper.log(output)

                except Exception as ex:
                    helper.log("Error updating: " + str(ex))
                pbar.update(1)  # increment the progressbar


if __name__ == "__main__":
    """Update the dataset git projects"""
    root_folder = sys.argv[1]
    print("Start walking from: " + sys.argv[1])
    update_repos(root_folder)
    print("End")
