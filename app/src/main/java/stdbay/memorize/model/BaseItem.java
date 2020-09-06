package stdbay.memorize.model;

import java.util.Map;

public class BaseItem {
    public static final int SUBJECT_TYPE=1;
    public static final int PROBLEM_SET_TYPE=2;
    public static final int PROBLEM_TYPE=3;
    public static final int KNOWLEDGE_TYPE=4;

    private int id;
    private String name;
    private int type;
    private  int fatherId;
    private Map<String, Integer> childrenData;

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

    public int getFatherId() {
        return fatherId;
    }

    public void setFatherId(int fatherId) {
        this.fatherId = fatherId;
    }

    public Map<String, Integer> getChildrenData() {
        return childrenData;
    }

    public void setChildrenData(Map<String, Integer> childrenData) {
        this.childrenData = childrenData;
    }
}
