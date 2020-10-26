package stdbay.memorize.util;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

import stdbay.memorize.model.BaseItem;

public class SubjectFormatter extends ValueFormatter {
    private final List<BaseItem> itemList;
    public SubjectFormatter(List<BaseItem> list){
        this.itemList=list;
    }

    @Override
    public String getFormattedValue(float value) {
        //传入的参数已经排好序,只需要按次取出即可
        return itemList.get((int) value-1).getName();
    }
}
