package models;

public class VersionCategory {
    private String commitId;
    private String commitDate;
    private boolean hasBeanChanges;
    private boolean hasAnnotationChanges;
    private boolean hasJUnitsChanges;

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(String commitDate) {
        this.commitDate = commitDate;
    }

    public boolean hasBeanChanges() {
        return hasBeanChanges;
    }

    public void setHasBeanChanges(boolean hasBeanChanges) {
        this.hasBeanChanges = hasBeanChanges;
    }

    public boolean hasAnnotationChanges() {
        return hasAnnotationChanges;
    }

    public void setHasAnnotationChanges(boolean hasAnnotationChanges) {
        this.hasAnnotationChanges = hasAnnotationChanges;
    }

    public boolean hasJUnitsChanges() {
        return hasJUnitsChanges;
    }

    public void setHasJUnitsChanges(boolean hasJUnitsChanges) {
        this.hasJUnitsChanges = hasJUnitsChanges;
    }
}
