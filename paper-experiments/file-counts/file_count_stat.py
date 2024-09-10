import pandas as pd

if __name__ == "__main__":
    """Find max, min, mean, median numbers of files in the selected 70 projects"""
    file_count_df = pd.read_csv("file-count.txt", sep="\t")
    selected_projects_df = pd.read_csv("selected-70-projects.txt", sep="\t")
    
    merged_df = pd.merge(selected_projects_df, file_count_df, on="Repo")
    
    max_num_of_files = merged_df["Total File Count"].max()
    min_num_of_files = merged_df["Total File Count"].min()
    mean_num_of_files = merged_df["Total File Count"].mean()
    median_num_of_files = merged_df["Total File Count"].median()
    
    repos_with_max_num_of_files = merged_df[merged_df["Total File Count"] == max_num_of_files]["Repo"].tolist()
    repos_with_min_num_of_files = merged_df[merged_df["Total File Count"] == min_num_of_files]["Repo"].tolist()
    
    print(f"Max number of files: {max_num_of_files}. Repos: {repos_with_max_num_of_files}")
    print(f"Min number of files: {min_num_of_files}. Repos: {repos_with_min_num_of_files}")
    print(f"Mean number of files: {mean_num_of_files}")
    print(f"Median number of files: {median_num_of_files}")
    