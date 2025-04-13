# 🛠️ MeCheck: A Rule-Based Metadata Bug Detector for Java Projects

**MeCheck** (Metadata Checker) is a tool designed to detect metadata-related bugs in Java projects using a custom domain-specific language we created, called **RSL (Rule Specification Language)**. It is particularly useful for analyzing the use of annotations, configuration files, and other project metadata that might go out of sync or cause bugs.

This tool was developed as part of our [FSE 2025](https://arxiv.org/abs/2502.14463) paper and is available under an open-source license.

---

## 📌 Features

- Analyze Java projects for metadata-related bugs
- Define your own rules using **RSL**
- Run across commits of a project
- View structured bug reports with detailed explanations

---

## 🚀 How to Run

- Go to `language-engine/language-engine`
- Compile with maven with Java 11 (or higher) using `mvn clean package`
- Run the tool using the packaged JAR file with the following **7 arguments**:

```
<dataset-folder> <project-name> <commit-id> <output-path> <rules-folder> <log-path> <library-classes-file>
```

### ✅ Example

```bash
mvn clean package

java -cp target/language-engine-0.0.1-SNAPSHOT.jar engine.EngineMain \
  artifact-submission/dataset \
  ShcUtils \
  afc5bc \
  artifact-submission/output \
  artifact-submission/rules \
  artifact-submission/logs.txt \
  artifact-submission/library-regex.txt
```

### 🧾 Folder Setup & Output Details

We created a folder named [`artifact-submission`](language-engine/language-engine/artifact-submission/) in the same directory as the `pom.xml` of [`language-engine/language-engine`](language-engine/language-engine/). Inside it:

- `dataset/ShcUtils/` contains a sample Java project to analyze  
- `rules/` contains 15 rule files written in RSL  
- `library-regex.txt` lists fully-qualified names (FQNs) of library classes to suppress false positives  

When you run the example command above, it will:

- Generate a `ShcUtils.txt` file in the [`output/`](language-engine/language-engine/artifact-submission/output/) folder, with a bug report similar to ---

```
[artifact-submission\dataset\ShcUtils](afc5bc)	beanClassExists.txt	Bean class: test.GreetingServiceImpl mentioned in bean: artifact-submission\dataset\ShcUtils\src\main\resources\applicationContext.xml-bean-greetingService, does not exist
```
(This output corresponds to one of the metadata issues discussed in our FSE 2025 paper --- missing bean class.)
- Write logs to `artifact-submission/logs.txt`

### 📂 Argument Details

| Argument | Description |
|----------|-------------|
| `dataset-folder` | Path to the folder containing Java projects |
| `project-name` | Name of the project you want to analyze |
| `commit-id` | Git commit id to analyze |
| `output-path` | Folder where the bug report will be saved |
| `rules-folder` | Path to folder containing the RSL rule files |
| `log-path` | File path for runtime logs |
| `library-classes-file` | File containing fully-qualified names of known library classes to suppress false positives |

---

## 📈 Experimental Results (FSE 2025 Paper)

This repository includes an Excel file `experiment-results.xlsx` containing detailed results from our evaluation. Here's a breakdown of each sheet:

| Sheet Name | Description |
|------------|-------------|
| **bug-injections** | Synthetic bug injection details. Columns: Bug Location, Repo, Bug Type, Bug Description, Detected?, Note |
| **repo-commit-file-count** | File count and keyword stats per repo. Columns: Repo, URL, Commit, Java File Count, XML File Count, Total File Count, Keyword |
| **randomly-selected-70-projects** | List of 70 randomly selected projects used in the study |
| **detected-bugs** | Real-world bugs detected. Columns: Repo, Initial Repo Commit, Bug Commit, Bug in Init Commit (Yes or No), Bug Timestamp, Vdiff(bug, fix) (Version difference between buggy and fixed version), Fix Commit, Fix timestamp, Bug Type, Report, Note, Fixed in Later Version (Yes or No) |
| **processing-time-for-fixables** | Runtime performance of the tool. Columns: Project, Commit, Processing Time |

---

## 📁 Repository Layout

```
detecting-metadata-bugs/
├── dataset-categorizer/	# Scripts used for processing the dataset
├── language-engine/	# MeCheck engine code
│   └── language-engine/src/engine/EngineMain.java	# Entry point to run the tool
│   └── language-engine/library-classes.txt	# Suppress known external classes
├── paper-experiments/	# Scripts used after FSE Feedback
├── parser-generator/	# JavaCC generated parser classes
├── rules/	# Sample RSL rules
└── experiment-results.xlsx	# Experimental results
```

---

## 📝 License

This tool is released under the [MIT License](/LICSENSE).

---

## 🤝 Citation

If you use MeCheck in your work, please cite our upcoming FSE 2025 paper (citation to be updated after publication).

---

## 📬 Contact

For questions or collaboration ideas, feel free to reach out to the authors.

---

## 📄 Additional Documentation

- [Installation Guide](/INSTALL.md)
- [Requirements](/REQUIREMENTS.md)
- [Artifact Status & Badge Justification](/STATUS.md)
- [Rules Descriptions](rules/README.md)

Note: The files INSTALL, REQUIREMENTS, and STATUS are in markdown for formatting.