package stdbay.memorize.model;

public class Knowledge {

    private int id;
    private int subId;
    private int fatherId;
    private String annotation;
    private String name;


    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getSubId() {
        return subId;
    }

    public String getAnnotation() {
        return annotation;
    }

    public int getFatherId() {
        return fatherId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setFatherId(int fatherId) {
        this.fatherId = fatherId;
    }
}
