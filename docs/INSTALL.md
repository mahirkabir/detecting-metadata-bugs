# INSTALL

## Prerequisites

- Java 11 or higher
- Git
- Excel viewer (for reviewing experiment-results.xlsx)

## Installation Instructions

1. Clone the repository:
```
git clone https://github.com/your-username/detecting-metadata-bugs.git
cd detecting-metadata-bugs
```

2. Compile the Java tool:
```
cd language-engine
javac -d bin src/*.java
```

3. Run the tool:
```
java -cp bin EngineMain <dataset-folder> <project-name> <commit-id> <output-path> <rules-folder> <log-path> <library-classes-file>
```

See the main README for full argument descriptions and a working example.
