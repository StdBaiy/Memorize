package stdbay.memorize.model;

public class Knowledge extends BaseItem{
    private int subId;
    private String annotation;

    public int getSubId() {
        return subId;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
}
