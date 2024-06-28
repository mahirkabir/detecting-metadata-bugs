import pandas as pd
import re
import random

# Initialize a list to hold project details
project_data = []

# Path to the file
file_path = 'version_categories.txt'

# Read the file and parse the project details
with open(file_path, 'r') as file:
    current_project = None
    for line in file:
        # Check if the line indicates the start of a new project
        project_match = re.match(r'^(.*): \((\d+)\)', line)
        if project_match:
            current_project = project_match.group(1)
            continue
        
        # Parse the version details
        version_match = re.match(r'^([a-f0-9]+)\s+\(([\d-]+)\)\s+Beans=(\d+)\s+Annotations=(\d+)\s+JUnits=(\d+)', line)
        if version_match:
            version, date, beans, annotations, junits = version_match.groups()
            project_data.append({
                'project': current_project,
                'version': version,
                'date': date,
                'Beans': int(beans),
                'Annotations': int(annotations),
                'JUnits': int(junits)
            })

# Create a DataFrame
df = pd.DataFrame(project_data)

# Define the rules and their types
rules = {
    'importXMLIntoAnnotation': 'Annotations',
    'beanClassExists': 'Beans',
    'beanExists': 'Beans',
    'constructorArgumentField': 'Beans',
    'constructorArgumentFieldType': 'Beans',
    'constructorIndexOutOfBound': 'Beans',
    'dupBeansExist': 'Beans',
    'methodExists': 'Beans',
    'setterMethod': 'Beans',
    'xmlPathCheck': 'General',
    'runwithNoParameters': 'JUnits',
    'runwithNoSuiteclasses': 'JUnits',
    'runwithNoTest': 'JUnits',
    'suiteclassesNoRunwith': 'JUnits',
    'suiteclassesNoTest': 'JUnits',
    'testParamsNotIterable': 'JUnits'
}

# Helper function to get valid projects for a rule
def get_valid_projects(df, rule_type, used_projects):
    if rule_type == 'General':
        return df[~df['project'].isin(used_projects)]
    return df[(df[rule_type] == 1) & (~df['project'].isin(used_projects))]

# Function to attempt selecting projects for each rule
def attempt_selection(df, rules):
    used_projects = set()
    final_selection = []

    for rule, rule_type in rules.items():
        valid_projects = get_valid_projects(df, rule_type, used_projects)

        # Randomly sample 4 projects from valid_projects
        if len(valid_projects) >= 4:
            selected = valid_projects.sample(n=4, replace=False).to_dict('records')
        else:
            selected = valid_projects.to_dict('records')

        # Add selected projects to final selection
        for project in selected:
            final_selection.append({
                'project': project['project'],
                'version': project['version'],
                'rule': rule
            })
            used_projects.add(project['project'])
    
    return final_selection, used_projects

# Try up to 5 times to get the highest number of unique projects for each rule
best_selection = []
max_unique_projects = 0

for attempt in range(5):
    final_selection, used_projects = attempt_selection(df, rules)
    unique_projects_count = len(set([item['project'] for item in final_selection]))
    if unique_projects_count > max_unique_projects:
        max_unique_projects = unique_projects_count
        best_selection = final_selection
    if unique_projects_count == 64:
        break

# Convert the best selection to a DataFrame
final_df = pd.DataFrame(best_selection)

# Write the output to a file
output_file_path = 'selected_projects_for_rules.csv'
final_df.to_csv(output_file_path, index=False)

output_file_path
