package engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import utils.Logger;

public class EngineVersionControl implements IEngineVersionControl {
    private Map<String, Boolean> isUntracked;
    private String projectPath;

    public EngineVersionControl(String projectPath) {
        this.isUntracked = null;
        this.projectPath = projectPath;
    }

    /**
     * Checks if the file is tracked to avoid reporting generated files
     */
    @Override
    public Boolean isTrackedFile(String filepath) {
        try {
            // This is just to make sure that the path goes through java's Path class
            String normalizedFullPath = Paths.get(filepath).normalize().toString();
            if (this.isUntracked == null)
                this.loadUntracked();
            return !isUntracked.containsKey(normalizedFullPath) || !isUntracked.get(normalizedFullPath);
        } catch (Exception ex) {
            Logger.log("Error processing: " + filepath + " => " + ex.toString());
        }
        return false;
    }

    /**
     * Load the untracked files
     */
    private void loadUntracked() {
        this.isUntracked = new HashMap<String, Boolean>();
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.setGitDir(new File(this.projectPath, ".git"))
                    .readEnvironment()
                    .findGitDir()
                    .build();

            try (Git git = new Git(repository)) {
                Status status = git.status().call();
                Set<String> untracked = status.getUntracked();
                for (String filepath : untracked) {
                    Path path = repository.getDirectory().toPath().getParent().resolve(filepath).normalize();
                    if (filepath.endsWith(".java") || filepath.endsWith(".xml"))
                        this.isUntracked.put(path.toString(), true);
                }
                Logger.log(String.format("Number of untracked files: %d", untracked.size()));
            }

        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            Logger.log("Error processing git for: " + this.projectPath + " (" + ioEx.getMessage() + ")");
        } catch (NoWorkTreeException nwtEx) {
            nwtEx.printStackTrace();
            Logger.log("Error processing git for: " + this.projectPath + " (" + nwtEx.getMessage() + ")");
        } catch (GitAPIException gapiEx) {
            gapiEx.printStackTrace();
            Logger.log("Error processing git for: " + this.projectPath + " (" + gapiEx.getMessage() + ")");
        }

    }
}
