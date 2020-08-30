package stdbay.memorize.model;

public class Subject {
    private int subjectId;
    private String subjectName;
    private String subjectDescription;

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectDescription() {
        return subjectDescription;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setSubjectDescription(String subjectDescription) {
        this.subjectDescription = subjectDescription;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }
}
