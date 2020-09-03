package stdbay.memorize.model;

public class Knowledge extends BaseItem{
    private int subId;
    private int fatherId;
    private String annotation;

    public int getSubId() {
        return subId;
    }

    public String getAnnotation() {
        return annotation;
    }

    public int getFatherId() {
        return fatherId;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setFatherId(int fatherId) {
        this.fatherId = fatherId;
    }
}
