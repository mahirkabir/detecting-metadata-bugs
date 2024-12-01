import os
import sys
import requests
from git import Repo

def get_repo_info(repo_path):
    """Function to extract GitHub repo URL"""
    try:
        repo = Repo(repo_path)
        remote_url = next(repo.remote().urls)
        repo_name = remote_url.split("/")[-1].replace(".git", "")
        owner_name = remote_url.split("/")[-2]
        return remote_url, repo_name, owner_name
    except Exception as e:
        print(f"Error accessing repo in path {repo_path}: {str(e)}")
        return None, None, None

def get_github_repo_data(owner_name, repo_name, headers):
    """Function to get information using GitHub API"""
    try:
        response = requests.get(f"https://api.github.com/repos/{owner_name}/{repo_name}", headers=headers)
        notes = []
        if response.status_code == 200:
            repo_data = response.json()
            stars = repo_data.get("stargazers_count", 0)
            watches = repo_data.get("subscribers_count", 0)  # GitHub uses subscribers_count for watchers
            forks = repo_data.get("forks_count", 0)

            # Check if the repo is archived
            if repo_data.get("archived", False):
                notes.append("REPO ARCHIVED")

            # Check if the repo is a fork
            if repo_data.get("fork", False):
                source_url = repo_data.get("parent", {}).get("html_url", "")
                if source_url:
                    notes.append(f"Forked from {source_url}")

            # Get the number of contributors (with pagination handling)
            contributors = 0
            page = 1
            while True:
                contributors_response = requests.get(
                    f"https://api.github.com/repos/{owner_name}/{repo_name}/contributors",
                    headers=headers,
                    params={"per_page": 100, "page": page}
                )
                
                if contributors_response.status_code == 200:
                    contributors_page = contributors_response.json()
                    if len(contributors_page) == 0:
                        break
                    contributors += len(contributors_page)
                    page += 1
                else:
                    print(f"Failed to fetch contributors for {repo_name}: {contributors_response.status_code}")
                    break

            return stars, watches, forks, contributors, ", ".join(notes)
        else:
            if response.status_code == 404:
                notes.append("REPO NO LONGER THERE")
            print(f"Failed to fetch GitHub data for {repo_name}: {response.status_code}")
            return 0, 0, 0, 0, ", ".join(notes)
    except Exception as e:
        print(f"Error while accessing GitHub API: {str(e)}")
        return 0, 0, 0, 0, "Error occurred"

def get_selected_repos():
    """Get the list of selected projects"""
    return [
        "angular-js-spring-mybatis",
        "heroku-spike-tomcat",
        "BooSpring",
        "spring-rest-security",
        "basis-webapp-sample",
        "daw02",
        "dspace-rest",
        "springproject",
        "trip-service",
        "mongoservice",
        "mobileiq",
        "MyTest",
        "ACMEReservedMeetingRooms",
        "rest-app",
        "FreeClassFinder_Server",
        "lushtext",
        "MongoDBSpringRest",
        "ShcUtils",
        "OnzeVencedor",
        "spring-vaadin",
        "green-webflow",
        "jbpm-multimodulo",
        "jee_baseapp",
        "voice-control-tunnel",
        "cv-web",
        "eFood",
        "training-djpk-2014-01",
        "chinabank-application2",
        "biyam_repository",
        "cas-webapp-jboss7",
        "ett-integration",
        "test-ejb-arquiteture",
        "BacklogTool",
        "aw2m-vulture",
        "PurchaseManagement",
        "smorales-app-headbanging-ee6",
        "DataAnalyzePlatform",
        "generica",
        "ComicRate",
        "Dino",
        "collection-manager",
        "FarmaciaFuturo",
        "Longminder",
        "EduServer",
        "Kognitywistyka",
        "LIBRARY",
        "lite-framework",
        "kie-wb-distributions",
        "feedAggregate",
        "homesources",
        "aab-main",
        "spring-petclinic",
        "enterprise-routing-system",
        "jarvis",
        "I377-esk",
        "genetic-program",
        "nhimeyecms",
        "FileExplorer",
        "cipol",
        "rop",
        "johnsully83_groovy",
        "E2-Demo",
        "wombat",
        "aerogear-unifiedpush-server",
        "aioweb",
        "emite",
        "kolomet",
        "bennu-renderers",
        "Lemo-Data-Management-Server",
        "CWISE-Portal"
    ]

if __name__ == "__main__":
    """Collect git info"""
    # Parent directory containing all the git repos
    parent_folder = sys.argv[1]

    # Set your GitHub Personal Access Token here for authentication
    github_token = sys.argv[2]
    headers = {"Authorization": f"token {github_token}"}

    # Output file to store the git info
    git_info_file = "git_info.txt"

    dirs = get_selected_repos()
    # Iterate over all repos in the parent folder and gather information
    with open(git_info_file, "w") as file:
        file.write("repo_name\trepo_url\tnumber_of_stars\tnumber_of_watches\tnumber_of_forks\tnumber_of_contributors\tNote\n")
        
        for repo_name in dirs:
            repo_path = os.path.join(parent_folder, repo_name)
            if os.path.isdir(repo_path):
                repo_url, repo_name, owner_name = get_repo_info(repo_path)
                if repo_url:
                    stars, watches, forks, contributors, notes = get_github_repo_data(owner_name, repo_name, headers=headers)
                    file.write(f"{repo_name}\t{repo_url}\t{stars}\t{watches}\t{forks}\t{contributors}\t{notes}\n")

    print(f"Git information has been written to {git_info_file}")