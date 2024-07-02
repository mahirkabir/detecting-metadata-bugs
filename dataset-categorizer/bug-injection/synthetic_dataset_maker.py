import os
import random
from tqdm import tqdm

# Function to find if a project contains specific keyword in files ending with Test.java
def project_has_keyword(project_path, keyword, extension="java"):
    for root, _, files in os.walk(project_path):
        for file in files:
            if file.endswith(f".{extension}"):
                fpath = os.path.join(root, file)
                try:
                    with open(fpath, 'r', encoding='utf-8', errors="ignore") as f:
                        content = f.read()
                        if keyword in content:
                            return fpath
                except FileNotFoundError:
                    print(f"File not found: {fpath}")
                except Exception as ex:
                    print(f"Error reading file {fpath}: {str(ex)}")
    return None

if __name__ == "__main__":
    """Find projects for making synthetic dataset"""
    # Define the paths
    dataset_folder = "D:\\Mahir\\detecting-metadata-bugs\\dataset\\all_projects"
    log_file_path = "log_file.log"
    

    # Get a list of all projects in the dataset folder
    all_projects = [os.path.join(dataset_folder, project) for project in os.listdir(dataset_folder) if os.path.isdir(os.path.join(dataset_folder, project))]

    # Shuffle the projects to randomize the checking order
    random.seed(107)
    random.shuffle(all_projects)

    keywords = {
        "@ImportResource": "java", "ApplicationContext": "java",
        ".getBean(": "java", "</bean>": "xml", "<property": "xml",
        "constructor-arg": "xml", "-method=": "xml",
        "ClassPathXmlApplicationContext": "java"
    }
    limit = 12
    selected_projects = {}
    needed_projects_cnt = int(limit * len(keywords))

    # Check projects until we find `limit` of each keyword or run out of projects
    for project in tqdm(all_projects):
        for keyword in keywords:
            if keyword in selected_projects and len(selected_projects[keyword]) == limit:
                continue
            try:
                file_path = project_has_keyword(project, keyword, extension=keywords[keyword])
                if file_path:
                    if keyword not in selected_projects: selected_projects[keyword] = []
                    selected_projects[keyword].append((project, file_path))
                    needed_projects_cnt -= 1
                    
                if needed_projects_cnt == 0:
                    break
            except Exception as ex:
                print(str(ex))
        if needed_projects_cnt == 0:
                break
                
    # Write the results to the log file
    with open(log_file_path, 'w') as log_file:
        for keyword in keywords:
            if keyword in selected_projects:
                for project, file_path in selected_projects[keyword]:
                    log_file.write(f"{keyword}\t{project}\t{file_path}\n")

    print(f"Log file created at {log_file_path} with selected projects.")
