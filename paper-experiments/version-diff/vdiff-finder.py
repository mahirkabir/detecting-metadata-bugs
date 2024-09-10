import os
import subprocess


def find_commit_count(repo_path, bug_commit, fix_commit):
    """Find the number of commits in `repo_path` between `bug_commit` and `fix_commit`"""
    git_command = f"git rev-list --count {bug_commit}..{fix_commit}"
    result = subprocess.run(git_command, cwd=repo_path, shell=True, capture_output=True, text=True)
    if result.returncode == 0:
        return result.stdout.strip()
    else:
        return -1


if __name__ == "__main__":
    """Find the number of versions between the bug commit and the fix commit"""
    dataset_folder = "D:\\Mahir\\detecting-metadata-bugs\\dataset\\all_projects"
    commit_info_file = "repo-bugcommit-fixcommit.txt"
    
    output = []
    with open(commit_info_file, "r", encoding="utf-8", errors="ignore") as reader:
        lines = reader.readlines()[1:]
        for line in lines:
            line = line.strip()
            [repo, bug, fix] = line.split("\t")
            count = find_commit_count(os.path.join(dataset_folder, repo), bug, fix)
            output.append(count)
            
    with open("output.txt", "w", encoding="utf-8", errors="ignore") as writer:
        for output_row in output:
            writer.write(f"{output_row}\n")