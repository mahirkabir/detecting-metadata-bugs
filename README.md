# detecting-metadata-bugs
In this repository, we have created a domain specific language called `RSL` (Rule Specification Language), and a language engine called `MeCheck` (Metadata Checker) to help software developers detect metadata-related bugs in java projects.

## How to run
Run `language-engine\language-engine\src\EngineMain.java` with the following arguments:

1. The dataset folder that contains the java projects we want to check
1. The project name that we want to analyze
1. The commit id that we want to analyze
1. The output path where we want to see the bug reports
1. The location where all the rules we want to run are located
1. The log path
1. The filepath containing the list of library classes we have in our project that we want to avoid reporting as missing classes


# Experiment Results for FSE 2025 Paper
This repository also contains the `experiment-results.xlsx` file that contains the experiment results for our tool. 

The result file `experiment-results.xlsx` has the following sheets - 

- `bug-injections`: This sheet has the synthetic dataset-related information. It contains the following headers - Bug Location, Repo, Bug Type, Bug Description, Detected?, Note 
- `repo-commit-file-count`: This sheet contains the file count and keyword matches for the dataset repos. It contains the following headers - Repo, URL, Commit, Java File Count, XML File Count, Total File Count, Keyword
- `randomly-selected-70-projects`: It contains the list of 70 projects that we randomly selected
- `detected-bugs`: This is the table that contains the detected real bugs. It has the following headers - Repo, Initial Repo Commit, Bug Commit, Bug in Init Commit (Yes or No), Bug Timestamp, Vdiff(bug, fix) (Version difference between buggy and fixed version), Fix Commit, Fix timestamp, Bug Type, Report, Note, Fixed in Later Version (Yes or No)
- `processing-time-for-fixables`: This contains the processing time for the tool to detect bugs in the commits that contained fixable bugs. It has the following headers: Project, Commit, Processing Time
