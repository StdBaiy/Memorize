package stdbay.memorize.util;

import android.icu.text.NumberFormat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

import stdbay.memorize.adapter.ListDropDownAdapter;

/**
 * This IValueFormatter is just for convenience and simply puts a "%" sign after
 * each value. (Recommeded for PieChart)
 *
 * @author Philipp Jahoda
 */
public class PieChartFormatter extends ValueFormatter
{

    private ListDropDownAdapter viewAdapter;
    public DecimalFormat mFormat;
    private PieChart pieChart;

    public PieChartFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0");
    }

    // Can be used to remove percent signs if the chart isn't in percent mode
    public PieChartFormatter(PieChart pieChart,ListDropDownAdapter viewAdapter) {
        this();
        this.pieChart = pieChart;
        this.viewAdapter= viewAdapter;
    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value) + " %";
    }

    @Override
    public String getPieLabel(float value, PieEntry pieEntry) {
        if (pieChart != null && pieChart.isUsePercentValuesEnabled()) {
            // Converted to percent
            return getFormattedValue(value);
        } else {
            // raw value, skip percent sign
            if(viewAdapter.getSelectPosition()==0){//按出现次数
                return (int)value+"次";
            }else if(viewAdapter.getSelectPosition()==1){//按正确率
                NumberFormat formatter = new android.icu.text.DecimalFormat("0.0");
                return formatter.format(value*100)+"%";
            }
        }
        return "";
    }

}
