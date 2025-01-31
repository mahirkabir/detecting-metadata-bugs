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
