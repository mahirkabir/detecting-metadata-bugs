import json
import os
import random
import git
from tqdm import tqdm


def collect_keyword_files(project_path, keyword, extension="java"):
    """Collect files in `project_path` with `keyword` in it"""
    filepaths = []
    for root, _, files in os.walk(project_path):
        for file in files:
            if file.endswith(f".{extension}"):
                fpath = os.path.join(root, file)
                try:
                    with open(fpath, 'r', encoding='utf-8', errors="ignore") as f:
                        content = f.read()
                        if keyword in content:
                            filepaths.append(fpath)
                except FileNotFoundError:
                    print(f"File not found: {fpath}")
                except Exception as ex:
                    print(f"Error reading file {fpath}: {str(ex)}")
    return filepaths

def get_preselected_random_repos(filepath):
    """Find list of 70 randomly selected repository names from `filepath`"""
    repos = []
    with open(filepath, "r", encoding="utf-8", errors="ignore") as reader:
        lines = reader.readlines()
        for line in lines:
            repos.append(line.strip())
    return repos

def collect_file_commits(repo_path, filepath):
    """Find all the commits on `filepath` in `repo_path` repo"""
    commits = []
    
    try:
        repo = git.Repo(repo_path)
        # Iterate through all the commits that affected the specific file
        for commit in repo.iter_commits(paths=filepath):
            commit_info = {
                'hash': commit.hexsha,
                'author': commit.author.name,
                'date': commit.committed_datetime.strftime("%Y-%m-%d %H:%M:%S"),  # Convert datetime to string
                'message': commit.message.strip()
            }
            commits.append(commit_info)
    except git.exc.InvalidGitRepositoryError:
        print(f"Invalid Git repository: {repo_path}")
    except git.exc.NoSuchPathError:
        print(f"No such path: {repo_path}")
    except Exception as e:
        print(f"An error occurred: {e}")
    
    return commits
    

if __name__ == "__main__":
    """Find commits of randomly selected files based on keywords for realbug-related experiments"""
    # Define the paths
    dataset_folder = "D:\\Mahir\\detecting-metadata-bugs\\dataset\\all_projects"
    randomly_selected_repos = get_preselected_random_repos("random_real_bug_repos.txt")
    project_keyword_output_file_path = "project_keyword_file_commits.json"
    project_keyword_output_file_path_sheet = "project_keyword_output_file_path.txt"
    
    keywords = {
        "@ImportResource": "java", "ApplicationContext": "java",
        ".getBean(": "java", "<bean": "xml", "<property": "xml",
        "constructor-arg": "xml", "-method=": "xml",
        "ClassPathXmlApplicationContext": "java",
        "org.junit": "java", "@RunWith(": "java", "@RunWith(Suite.class)": "java",
        "@Parameters": "java", "@Parameterized.Parameters": "java"
    }
    
    repo_keyword_file_commit = {}
    # Check projects until we find `limit` of each keyword or run out of projects
    for repo in tqdm(randomly_selected_repos):
        repo_path = os.path.join(dataset_folder, repo)
        repo_keyword_file_commit[repo] = {}
        for keyword in keywords:
            try:
                filepaths = collect_keyword_files(repo_path, keyword, extension=keywords[keyword])
                if len(filepaths) > 0:
                    for filepath in filepaths:
                        if filepath not in repo_keyword_file_commit[repo]:
                            repo_keyword_file_commit[repo][filepath] = {"keywords": [], "commits": []}
                        repo_keyword_file_commit[repo][filepath]["keywords"].append(keyword)
            except Exception as ex:
                print(str(ex))
    
    for repo in tqdm(randomly_selected_repos):
        repo_path = os.path.join(dataset_folder, repo)
        if repo in repo_keyword_file_commit:
            for filepath in tqdm(repo_keyword_file_commit[repo]):
                repo_keyword_file_commit[repo][filepath]["commits"] = collect_file_commits(repo_path, filepath)
    
    if False:
        # Save the dictionary to a JSON file
        try:
            with open(project_keyword_output_file_path, "w", encoding="utf-8") as json_file:
                json.dump(repo_keyword_file_commit, json_file, indent=4)
            print(f"Data successfully saved to {project_keyword_output_file_path}")
        except Exception as ex:
            print(f"Error saving data to JSON: {str(ex)}")
    
    # Save the dictionary data in text file
    try:
        with open(project_keyword_output_file_path_sheet, "w", encoding="utf-8", errors="ignore") as sheet_file:
            for repo in tqdm(randomly_selected_repos):
                if repo in repo_keyword_file_commit:
                    for filepath in repo_keyword_file_commit[repo]:
                        keywords = ",".join(repo_keyword_file_commit[repo][filepath]["keywords"])
                        for commit in repo_keyword_file_commit[repo][filepath]["commits"]:
                            commit_id = commit["hash"]
                            commit_date = commit["date"]
                            commit_message = commit["message"].replace("\n", "</br>")
                            # output = f"{repo}\t{filepath}\t{keywords}\t{commit_id}\t{commit_date}\t{commit_message}\n"
                            output = f"{repo}\t{filepath}\t{commit_id}\t{commit_date}\t{commit_message}\n"
                            sheet_file.write(output)
                            
    except Exception as ex:
        print(f"Error saving data to file: {str(ex)}")
                