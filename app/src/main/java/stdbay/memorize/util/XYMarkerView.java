package stdbay.memorize.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import stdbay.memorize.R;
import stdbay.memorize.adapter.ListDropDownAdapter;

/**
 * 柱状图的数字指示器
 *
 * @author xuexiang
 * @since 2019/4/10 下午11:34
 */
@SuppressLint("ViewConstructor")
public class XYMarkerView extends MarkerView {
    private final ListDropDownAdapter viewAdapter;

    private final TextView tvContent;
    private final ValueFormatter yAxisValueFormatter;

    public XYMarkerView(Context context, ValueFormatter yAxisValueFormatter,ListDropDownAdapter viewAdapter) {
        super(context, R.layout.marker_view_xy);
        this.yAxisValueFormatter = yAxisValueFormatter;
        this.viewAdapter= viewAdapter;
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if(viewAdapter.getSelectPosition()==0)
            tvContent.setText(String.format("%s 出现%s次", e.getData(), (int)e.getY()));
        else if(viewAdapter.getSelectPosition()==1)
            tvContent.setText(String.format("%s 正确率%s", e.getData(), yAxisValueFormatter.getFormattedValue(e.getY()*100)));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() >> 1), -getHeight());
    }
}
