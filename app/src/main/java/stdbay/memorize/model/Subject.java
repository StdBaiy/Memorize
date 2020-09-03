package stdbay.memorize.model;

public class Subject extends BaseItem{
    private int fatherId;

    public int getFatherId(){
        return fatherId;
    }

    public void setFatherId(int fatherId) {
        this.fatherId = fatherId;
    }
}
