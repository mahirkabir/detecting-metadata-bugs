import os
import subprocess
from pathlib import Path
import sys
from tqdm import tqdm

def main():
    if len(sys.argv) != 2:
        print("Usage: python count_files.py <path_to_directory>")
        sys.exit(1)

    base_dir = Path(sys.argv[1])
    if not base_dir.exists() or not base_dir.is_dir():
        print(f"Error: The specified path {base_dir} does not exist or is not a directory.")
        sys.exit(1)

    output_file = base_dir / "output.txt"

    # Prepare the output file
    with open(output_file, 'w') as file:
        file.write("")

    # List all directories
    directories = [d for d in base_dir.iterdir() if d.is_dir()]

    for dir in tqdm(directories, desc="Processing directories"):
        try:
            # Check for Git repository and get the current commit ID
            commit_id = get_git_commit_id(dir)
            
            # Count .java and .xml files
            java_count = sum(1 for _ in dir.rglob('*.java'))
            xml_count = sum(1 for _ in dir.rglob('*.xml'))

            # Write to output file
            with open(output_file, 'a') as file:
                file.write(f"{dir.name}\t{commit_id}\t{java_count}\t{xml_count}\n")
        except Exception as e:
            print(f"Failed to process {dir}: {e}")

    print("\nDone. Output written to", output_file)

def get_git_commit_id(directory):
    """Return the current Git commit ID of the directory, if it's a Git repository."""
    try:
        # Use subprocess to execute git rev-parse
        return subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=directory).decode('utf-8').strip()
    except subprocess.CalledProcessError:
        # If it fails, not a Git repo or Git is not available
        return "N/A"
    except Exception as e:
        print(f"Error getting Git commit ID for {directory}: {e}")
        return "Error"

if __name__ == "__main__":
    main()
