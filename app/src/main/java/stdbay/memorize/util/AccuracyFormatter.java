package stdbay.memorize.util;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

import stdbay.memorize.adapter.ListDropDownAdapter;

public class AccuracyFormatter extends ValueFormatter {
    //单位
    private final String company="%";

    private ListDropDownAdapter viewAdapter;

    private final DecimalFormat mFormat;

    public AccuracyFormatter(ListDropDownAdapter viewAdapter){
        mFormat=new DecimalFormat("0.00");
        this.viewAdapter= viewAdapter;
    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value)+company;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        if (axis instanceof XAxis) {
            return mFormat.format(value);
        } else if (value >= 0) {
            if(viewAdapter.getSelectPosition()==0){//按次数查询
                return (int)value+"次";
            }
            else if(viewAdapter.getSelectPosition()==1){//按正确率查询
                return mFormat.format(value) + company;
            }
        } else {
            return mFormat.format(value);
        }
        return "";
    }

}
