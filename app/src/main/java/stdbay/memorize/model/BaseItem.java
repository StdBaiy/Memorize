package stdbay.memorize.model;

public class BaseItem {
    public static final int SUBJECT_TYPE=1;
    public static final int PROBLEM_SET_TYPE=2;
    public static final int PROBLEM_TYPE=3;
    public static final int KNOWLEDGE_TYPE=4;

    private int id;
    private String name;
    private int type;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
