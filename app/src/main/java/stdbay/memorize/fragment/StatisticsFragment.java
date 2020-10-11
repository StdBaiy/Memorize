package stdbay.memorize.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.spinner.DropDownMenu;
import com.xuexiang.xutil.resource.ResUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import stdbay.memorize.R;
import stdbay.memorize.adapter.FlexboxLayoutAdapter;
import stdbay.memorize.adapter.ListDropDownAdapter;
import stdbay.memorize.model.BaseItem;
import stdbay.memorize.model.KnowledgeIssue;
import stdbay.memorize.model.MemorizeDB;
import stdbay.memorize.util.DayAxisValueFormatter;
import stdbay.memorize.util.MoneyValueFormatter;
import stdbay.memorize.util.Util;
import stdbay.memorize.util.XYMarkerView;

public class StatisticsFragment extends Fragment implements OnChartValueSelectedListener{
    private Map<Integer, KnowledgeIssue> knowledgeMap;
    private List<Integer>selectedSubjectId=new ArrayList<>();

    private MemorizeDB memorizeDB;

    private FlexboxLayoutAdapter fAdapter;

    private String[] viewType;
    private String[] sortType;


    private View view;
    private DropDownMenu mDropDownMenu;
    private List<View> mPopupViews = new ArrayList<>();
    private String[] mHeaders = {"科目","查看方式", "排序"};
    private TextView notice;

    BarChart barChart;
    private PieChart pieChart;
    private static final int PIE_CHART=0;
    private static final int BAR_CHART=1;

    @SuppressLint({"DefaultLocale", "InflateParams"})
    private void initView(){
        memorizeDB=MemorizeDB.getInstance(getContext());

        fAdapter = new FlexboxLayoutAdapter(memorizeDB.getSubjectList());

        fAdapter.setIsMultiSelectMode(true);
        fAdapter.setCancelable(false);
        fAdapter.setOnItemClickListener((itemView, item, position) -> fAdapter.select(position));
        //选中并添加到subId列表保存
        selectedSubjectId.clear();
        for(int i=0;i<fAdapter.getData().size();++i){
            if(!fAdapter.isSelected(i))
                fAdapter.select(i);
            selectedSubjectId.add(fAdapter.getData().get(i).getId());
        }

        LinearLayout subjectInflate = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.subject_item, null, false);
        RecyclerView subjectRV= subjectInflate.findViewById(R.id.subject_items);
        subjectRV.setLayoutManager(Util.getFlexboxLayoutManager(getContext()));
        subjectRV.setItemAnimator(null);
        subjectRV.setAdapter(fAdapter);
//        subjectRV.setVisibility(View.GONE);

        //设置选择按钮
        RoundButton chooseAll=subjectInflate.findViewById(R.id.choose_all);
        RoundButton chooseNone=subjectInflate.findViewById(R.id.choose_none);
        RoundButton chooseOther=subjectInflate.findViewById(R.id.choose_other);

        chooseAll.setOnClickListener(view1 -> {
            selectedSubjectId.clear();
            for(int i=0;i<fAdapter.getData().size();++i){
                if(!fAdapter.isSelected(i))
                    fAdapter.select(i);
                selectedSubjectId.add(fAdapter.getData().get(i).getId());
            }
        });

        chooseNone.setOnClickListener(view1 -> {
            selectedSubjectId.clear();
            for(int i=0;i<fAdapter.getData().size();++i){
                if(fAdapter.isSelected(i))
                    fAdapter.select(i);
            }
        });

        chooseOther.setOnClickListener(view1 -> {
            selectedSubjectId.clear();
            for(int i=0;i<fAdapter.getData().size();++i){
                fAdapter.select(i);
                if(fAdapter.isSelected(i))
                    selectedSubjectId.add(fAdapter.getData().get(i).getId());
            }
        });


//        notice.setVisibility(View.VISIBLE);

        mDropDownMenu=view.findViewById(R.id.ddm_content);
        RoundButton startDate = view.findViewById(R.id.start_date);
        startDate.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(Objects.requireNonNull(getContext())
                    , DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT
                    , (view, year, monthOfYear, dayOfMonth) -> ((TextView)view1)
                    .setText(String.format("%d-%2d-%2d", year, (monthOfYear + 1), dayOfMonth))
                    // 设置初始日期
                    , calendar.get(Calendar.YEAR)
                    , calendar.get(Calendar.MONTH)
                    , calendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });

        RoundButton endDate = view.findViewById(R.id.end_date);
        endDate.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(Objects.requireNonNull(getContext())
                    , DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT
                    , (view, year, monthOfYear, dayOfMonth) -> ((TextView)view1)
                    .setText(String.format("%d-%2d-%2d", year, (monthOfYear + 1), dayOfMonth))
                    // 设置初始日期
                    , calendar.get(Calendar.YEAR)
                    , calendar.get(Calendar.MONTH)
                    , calendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        endDate.setText(formatter.format(c.getTime()));
        c.add(Calendar.MONTH,-1);
        startDate.setText(formatter.format(c.getTime()));

        ListDropDownAdapter viewAdapter;
        ListDropDownAdapter sortAdapter;

        viewType = ResUtils.getStringArray(R.array.view_type);
        sortType = ResUtils.getStringArray(R.array.sort_type);

        final ListView viewBody = new ListView(getContext());
        viewAdapter = new ListDropDownAdapter(getContext(), viewType);
        viewBody.setDividerHeight(0);
        viewBody.setAdapter(viewAdapter);

        //init age menu
        final ListView sortBody = new ListView(getContext());
        sortBody.setDividerHeight(0);
        sortAdapter = new ListDropDownAdapter(getContext(), sortType);
        sortBody.setAdapter(sortAdapter);


        mPopupViews.add(subjectInflate);
        mPopupViews.add(viewBody);
        mPopupViews.add(sortBody);

        //init context view


        LinearLayout chartInflate = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.statistics_chart, null, false);
        
        pieChart=chartInflate.findViewById(R.id.pie_chart);
        barChart=chartInflate.findViewById(R.id.bar_chart);
        notice= chartInflate.findViewById(R.id.notice);


        initPieChartStyle(pieChart);
        initPieChartLabel(pieChart);

        initBarChartLabel(barChart);
        initBarChartLabel(barChart);
        setBarChartData(12, 50,barChart);
        barChart.setOnChartValueSelectedListener(this);

        knowledgeMap=memorizeDB.queryBySelection(selectedSubjectId,0,0,"","");
        setPieChartData(pieChart,knowledgeMap);
        pieChart.setOnChartValueSelectedListener(this);

        //add item click event
        viewBody.setOnItemClickListener((parent, view, position, id) -> {
            viewAdapter.setSelectPosition(position);
            mDropDownMenu.setTabMenuText(viewType[position]);
            mDropDownMenu.closeMenu();
        });

        sortBody.setOnItemClickListener((parent, view, position, id) -> {
            sortAdapter.setSelectPosition(position);
            mDropDownMenu.setTabMenuText(sortType[position]);
            mDropDownMenu.closeMenu();
        });

        mDropDownMenu.setDropDownMenu(mHeaders, mPopupViews, chartInflate);

        viewAdapter.setSelectPosition(0);
        mDropDownMenu.setTabMenuText(viewType[0]);

        sortAdapter.setSelectPosition(0);
        mDropDownMenu.setTabMenuText(sortType[0]);

        ImageView search=view.findViewById(R.id.search);
        search.setOnClickListener(view1 -> {

            if(viewAdapter.getSelectPosition()==PIE_CHART){
                pieChart.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.GONE);
                selectedSubjectId.clear();
                for(BaseItem item:fAdapter.getMultiContent()){
                    selectedSubjectId.add(item.getId());
                }
                knowledgeMap=memorizeDB.queryBySelection(selectedSubjectId,0,0,"","");
                setPieChartData(pieChart,knowledgeMap);

            }else if(viewAdapter.getSelectPosition()==BAR_CHART){
                pieChart.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);
                setBarChartData(12, 50,barChart);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.statistics_fragment, container, false);
        Bundle bundle = getArguments();
        if(bundle != null) {
            initView();

        }
        return view;
    }

    public static StatisticsFragment getInstance(){
        Bundle bundle = new Bundle();
        StatisticsFragment myFragment = new StatisticsFragment();
        myFragment.setArguments(bundle);
        return myFragment;
    }

    private void initPieChartStyle(PieChart pieChart) {
        //使用百分比显示
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(25, 0, 25, 5);
        //设置拖拽的阻尼，0为立即停止
        pieChart.setDragDecelerationFrictionCoef(0.9f);

        pieChart.getDescription().setEnabled(false);

        //禁止显示文字
        pieChart.setDrawEntryLabels(false);
        //设置图标中心文字
//        pieChart.setCenterText(generateCenterSpannableText());
        pieChart.setDrawCenterText(false);
        //设置图标中心空白，空心
        pieChart.setDrawHoleEnabled(false);

        //设置可以旋转
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
    }
//    /**
//     * 生成饼图中间的文字
//     *
//      * @return
//     */
//    private SpannableString generateCenterSpannableText() {
//        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
//        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
//        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
//        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
//        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
//        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
//        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
//        return s;
//    }

    /**
     * 初始化图表的 标题
     */
    private void initPieChartLabel(PieChart pieChart) {
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(5f);
        // entry label styling
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(15f);
    }



    protected void setPieChartData(PieChart pieChart, Map<Integer, KnowledgeIssue>map) {
        if(map.isEmpty()){
            notice.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.GONE);
            return;
        }else{
            notice.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
        }

        List<PieEntry> entries = new ArrayList<>();

        //借助list实现hashMap排序//
        //注意 ArrayList<>() 括号里要传入map.entrySet()
        List<Map.Entry<Integer, KnowledgeIssue>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> {
            //按照得分值，从大到小排序
            return o2.getValue().occurrence - o1.getValue().occurrence;
        });

        //注意这里遍历的是list，也就是我们将map.Entry放进了list，排序后的集合
        int rest = KnowledgeIssue.totalOccurences;
        int num=0;
        for (Map.Entry<Integer,KnowledgeIssue> entry : list){
            entries.add(new PieEntry(entry.getValue().occurrence,entry.getValue().name));
            rest-=entry.getValue().occurrence;
            if(num++>=15)break;
        }

        if(rest>0)
            entries.add(new PieEntry(rest,"其他"));

        PieDataSet dataSet = new PieDataSet(entries, "cool");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(0);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        List<Integer> colors = new ArrayList<>();

        //20种预设颜色
        for(int i=0;i<5;++i){
            colors.add(ColorTemplate.COLORFUL_COLORS[i]);
            colors.add(ColorTemplate.LIBERTY_COLORS[i]);
            colors.add(ColorTemplate.PASTEL_COLORS[i]);
            colors.add(ColorTemplate.JOYFUL_COLORS[i]);
        }

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);

        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
        pieChart.animateY(1000, Easing.EaseInOutQuad);
    }


    /**
     * 初始化图表的样式
     */
    protected void initBarChartStyle(BarChart barChart) {
        //关闭描述
        barChart.getDescription().setEnabled(false);
        barChart.setDrawBarShadow(false);

        //开启在柱状图顶端显示值
        barChart.setDrawValueAboveBar(true);
        //设置显示值时，最大的柱数量
        barChart.setMaxVisibleValueCount(60);

        //设置不能同时在x轴和y轴上缩放
        barChart.setPinchZoom(false);
        //设置不画背景网格
        barChart.setDrawGridBackground(false);

        initXYAxisStyle(barChart);
    }

    /**
     * 初始化图表X、Y轴的样式
     */
    private void initXYAxisStyle(BarChart barChart) {
        //设置X轴样式
        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(barChart);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        //间隔一天显示
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        //设置Y轴的左侧样式
        ValueFormatter yAxisFormatter = new MoneyValueFormatter("$");
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(yAxisFormatter);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        //设置Y轴的右侧样式
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(yAxisFormatter);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        //设置图表的数值指示器
        XYMarkerView mv = new XYMarkerView(getContext(), xAxisFormatter, yAxisFormatter);
        mv.setChartView(pieChart);
        pieChart.setMarker(mv);
    }

    /**
     * 初始化图表的 标题 样式
     */
    protected void initBarChartLabel(BarChart barChart) {
        //设置图表 标题 的样式
        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }

    /**
     * 设置图表数据
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    protected void setBarChartData(int count, float range,BarChart barChart) {
        float start = 1f;
        List<BarEntry> values = new ArrayList<>();
        //设置数据源
        for (int i = (int) start; i < start + count; i++) {
            float val = (float) (Math.random() * (range + 1));
            if (Math.random() * 100 < 25) {
                //设置图表，标星
                values.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.ic_star_green)));
            } else {
                values.add(new BarEntry(i, val));
            }
        }

        BarDataSet set1;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "The year 2019");

            //设置是否画出图标
            set1.setDrawIcons(false);

            int startColor1 = ContextCompat.getColor(getContext(), android.R.color.holo_orange_light);
            int startColor2 = ContextCompat.getColor(getContext(), android.R.color.holo_blue_light);
            int startColor3 = ContextCompat.getColor(getContext(), android.R.color.holo_orange_light);
            int startColor4 = ContextCompat.getColor(getContext(), android.R.color.holo_green_light);
            int startColor5 = ContextCompat.getColor(getContext(), android.R.color.holo_red_light);
            int endColor1 = ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark);
            int endColor2 = ContextCompat.getColor(getContext(), android.R.color.holo_purple);
            int endColor3 = ContextCompat.getColor(getContext(), android.R.color.holo_green_dark);
            int endColor4 = ContextCompat.getColor(getContext(), android.R.color.holo_red_dark);
            int endColor5 = ContextCompat.getColor(getContext(), android.R.color.holo_orange_dark);

            List<GradientColor> gradientColors = new ArrayList<>();
            gradientColors.add(new GradientColor(startColor1, endColor1));
            gradientColors.add(new GradientColor(startColor2, endColor2));
            gradientColors.add(new GradientColor(startColor3, endColor3));
            gradientColors.add(new GradientColor(startColor4, endColor4));
            gradientColors.add(new GradientColor(startColor5, endColor5));

            //设置渐变色
            set1.setGradientColors(gradientColors);

            //这里只设置了一组数据
            List<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            barChart.setData(data);
        }
        barChart.animateY(1000);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if(pieChart.getVisibility()==View.VISIBLE)
            Toast.makeText(getContext(), ((PieEntry)e).getLabel() ,Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), String.valueOf(e.getY()),Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onNothingSelected() {

    }
}
