package stdbay.memorize.model;

public class Subject {
    private int id;
    private String name;
    private String description;
    private int fatherId;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public int getFatherId(){
        return fatherId;
    }

    public void setName(String subjectName) {
        this.name = subjectName;
    }

    public void setDescription(String subjectDescription) {
        this.description = subjectDescription;
    }

    public void setId(int subjectId) {
        this.id = subjectId;
    }

    public void setFatherId(int fatherId) {
        this.fatherId = fatherId;
    }
}
