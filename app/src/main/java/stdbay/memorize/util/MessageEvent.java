package stdbay.memorize.util;

public class MessageEvent {
    private int type;

    public static final int ITEM_CHANGED=1;


    public MessageEvent(int type){
        this.type=type;
    }

    public int getType() {
        return type;
    }
}
