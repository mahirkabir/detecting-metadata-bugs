import pandas as pd

if __name__ == "__main__":
    """Merge the file count and keyword matches"""
    # Load Data1
    data1 = pd.read_csv('keyword_file_match.txt', sep='\t', header=None, names=['Keyword', 'RepoPath', 'FilePath'])

    # Extract repository name from the file paths
    data1['RepoName'] = data1['RepoPath'].apply(lambda x: x.split('all_projects\\')[1].split('\\')[0])

    # Load Data2
    data2 = pd.read_csv('file_count.txt', sep='\t', header=None, names=['RepoName', 'CommitID', 'JavaCount', 'XmlCount'])

    # Merge Data1 and Data2 on the 'RepoName'
    merged_data = pd.merge(data1, data2, on='RepoName')

    # Save the merged data to a new CSV file
    merged_data.to_csv('merged_data.csv', sep='\t', index=False)

    print("Merge complete and file saved as 'merged_data.csv'.")
