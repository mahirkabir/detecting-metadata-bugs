import os
import random
from tqdm import tqdm

# Function to find if a project contains specific annotation in files ending with Test.java
def project_has_annotation(project_path, annotation):
    for root, _, files in os.walk(project_path):
        for file in files:
            if file.endswith(".java"):
                fpath = os.path.join(root, file)
                try:
                    with open(fpath, 'r', encoding='utf-8', errors="ignore") as f:
                        content = f.read()
                        if annotation in content:
                            return fpath
                except FileNotFoundError:
                    print(f"File not found: {fpath}")
                except Exception as ex:
                    print(f"Error reading file {fpath}: {str(ex)}")
    return None

if __name__ == "__main__":
    """Find dataset projects relevant to junits bugs"""
    # Define the paths
    dataset_folder = "D:\\Mahir\\detecting-metadata-bugs\\dataset\\all_projects"
    log_file_path = "log_file.log"
    
    # Define the annotations
    param_annotation = "@RunWith(Parameterized.class)"
    suite_annotation = "@RunWith(Suite.class)"

    # Get a list of all projects in the dataset folder
    all_projects = [os.path.join(dataset_folder, project) for project in os.listdir(dataset_folder) if os.path.isdir(os.path.join(dataset_folder, project))]

    # Shuffle the projects to randomize the checking order
    random.seed(71)
    random.shuffle(all_projects)

    limit = 12
    selected_param_projects = []
    selected_suite_projects = []

    # Check projects until we find `limit` of each type or run out of projects
    for project in tqdm(all_projects):
        if len(selected_param_projects) < limit:
            file_path = project_has_annotation(project, param_annotation)
            if file_path:
                selected_param_projects.append((project, file_path))
        
        if len(selected_suite_projects) < limit:
            file_path = project_has_annotation(project, suite_annotation)
            if file_path:
                selected_suite_projects.append((project, file_path))
        
        if len(selected_param_projects) >= limit and len(selected_suite_projects) >= limit:
            break

    # Write the results to the log file
    with open(log_file_path, 'w') as log_file:
        for project, file_path in selected_param_projects:
            log_file.write(f"<type-1, {project}, {file_path}>\n")
        
        for project, file_path in selected_suite_projects:
            log_file.write(f"<type-2, {project}, {file_path}>\n")

    print(f"Log file created at {log_file_path} with selected projects.")
