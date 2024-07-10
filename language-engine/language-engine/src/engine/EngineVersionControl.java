package engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
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
    private Map<String, Boolean> ignored;
    private String projectPath;

    public EngineVersionControl(String projectPath) {
        this.ignored = null;
        this.projectPath = projectPath;
    }

    /**
     * Check if the file is ignored (or generated) to avoid reporting extra files
     * 
     */
    @Override
    public Boolean isNonIgnoredFile(String filepath) {
        try {
            // This is just to make sure that the path goes through java's Path class
            Path normalizedPath = Paths.get(filepath).normalize();
            if (this.ignored == null)
                this.loadIgnored();

            // Check the file itself and all its parent directories
            while (normalizedPath != null) {
                String pathToCheck = normalizedPath.toString();
                if (this.ignored.containsKey(pathToCheck) && this.ignored.get(pathToCheck)) {
                    return false;
                }
                normalizedPath = normalizedPath.getParent();
            } 
            // TODO: In future, we need to improve this logic of checking for each path

        } catch (Exception ex) {
            Logger.log("Error processing: " + filepath + " => " + ex.toString());
        }
        return true;
    }

    /**
     * Load the ignored files and folders
     */
    private void loadIgnored() {
        this.ignored = new HashMap<String, Boolean>();
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.setGitDir(new File(this.projectPath, ".git"))
                    .readEnvironment()
                    .findGitDir()
                    .build();

            Git git = new Git(repository);
            git.close();
            Status status = git.status().call();
            Set<String> ignoredSet = new HashSet<String>();
            ignoredSet.addAll(status.getIgnoredNotInIndex());

            for (String filepath : ignoredSet) {
                Path path = repository.getDirectory().toPath().getParent().resolve(filepath).normalize();
                this.ignored.put(path.toString(), true);
            }
            Logger.log(String.format("Number of non-ignored files: %d", ignoredSet.size()));

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.log("Error processing git for: " + this.projectPath + " (" + ex.getMessage() + ")");
        }
    }
}
