package stdbay.memorize.util;

import java.util.ArrayList;
import java.util.List;

import stdbay.memorize.model.BaseItem;

public class MessageEvent {
    private int type;

    public static final int ITEM_CHANGED=1;
    public static final int SELECT_KNOWLEDGE=2;
    public static final int KNOWLEDGE_RETURN=3;
    public static final int FIND_IN_TREE=4;
    public static final int CANCEL_SELECT=5;

    public static BaseItem findKnowledge;

    public static List<BaseItem> selectedknowledges=new ArrayList<>();

    public MessageEvent(int type){
        this.type=type;
    }

    public int getType() {
        return type;
    }
}
