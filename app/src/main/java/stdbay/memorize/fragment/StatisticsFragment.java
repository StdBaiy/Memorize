package stdbay.memorize.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
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
import stdbay.memorize.util.AccuracyFormatter;
import stdbay.memorize.util.MessageEvent;
import stdbay.memorize.util.PieChartFormatter;
import stdbay.memorize.util.SubjectFormatter;
import stdbay.memorize.util.Util;
import stdbay.memorize.util.XYMarkerView;

public class StatisticsFragment extends Fragment implements OnChartValueSelectedListener{
    private Map<Integer, KnowledgeIssue> knowledgeMap;
    private final List<Integer>selectedSubjectId=new ArrayList<>();

    private ListDropDownAdapter viewAdapter;
    private ListDropDownAdapter sortAdapter;
    private ListDropDownAdapter showAdapter;

    private MemorizeDB memorizeDB;

    private FlexboxLayoutAdapter fAdapter;

    private String[] viewType;
    private String[] sortType;
    private String[] showType;

    private String startDate;
    private String endDate;


    private View view;
    private DropDownMenu mDropDownMenu;
    private final List<View> mPopupViews = new ArrayList<>();
    private final String[] mHeaders = {"科目","查询方式","展示形式","排序"};
    private TextView notice;

    BarChart barChart;
    private PieChart pieChart;

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
                    , (view, year, monthOfYear, dayOfMonth) -> {
                        ((TextView) view1).setText(String.format("%d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth));
                        this.startDate= (String) ((TextView)view1).getText();
                    }
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
                    , (view, year, monthOfYear, dayOfMonth) -> {
                        ((TextView)view1).setText(String.format("%d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth));
                        this.endDate= (String) ((TextView)view1).getText();
                    }
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
        this.endDate= (String) endDate.getText();

        c.add(Calendar.MONTH,-1);
        startDate.setText(formatter.format(c.getTime()));
        this.startDate= (String) startDate.getText();

        viewType = ResUtils.getStringArray(R.array.view_type);
        sortType = ResUtils.getStringArray(R.array.sort_type);
        showType = ResUtils.getStringArray(R.array.show_type);

        final ListView viewBody = new ListView(getContext());
        viewAdapter = new ListDropDownAdapter(getContext(), viewType);
        viewBody.setDividerHeight(0);
        viewBody.setAdapter(viewAdapter);
        viewAdapter.setSelectPosition(0);

        final ListView showBody=new ListView(getContext());
        showAdapter = new ListDropDownAdapter(getContext(), showType);
        showBody.setDividerHeight(0);
        showBody.setAdapter(showAdapter);
        showAdapter.setSelectPosition(0);

        final ListView sortBody=new ListView(getContext());
        sortAdapter = new ListDropDownAdapter(getContext(), sortType);
        sortBody.setDividerHeight(0);
        sortBody.setAdapter(sortAdapter);
        sortAdapter.setSelectPosition(0);

        //init menu
        mPopupViews.add(subjectInflate);
        mPopupViews.add(viewBody);
        mPopupViews.add(showBody);
        mPopupViews.add(sortBody);

        //init context view
        LinearLayout chartInflate = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.statistics_chart, null, false);
        
        pieChart=chartInflate.findViewById(R.id.pie_chart);
        barChart=chartInflate.findViewById(R.id.bar_chart);
        notice= chartInflate.findViewById(R.id.notice);

        knowledgeMap=memorizeDB.queryBySelection(selectedSubjectId, this.startDate,this.endDate);
        List<BaseItem>items=new ArrayList<>();
        for (Map.Entry<Integer,KnowledgeIssue> entry : knowledgeMap.entrySet()){
            BaseItem item=new BaseItem();
            item.setType(BaseItem.KNOWLEDGE_TYPE);
            item.setId(entry.getKey());
            item.setName(entry.getValue().name);
            items.add(item);
        }

        initPieChartStyle(pieChart);
        initPieChartLabel(pieChart);

        initBarChartLabel(barChart);
        initBarChartStyle(barChart,items);
//        setBarChartData(barChart,knowledgeMap);
        barChart.setOnChartValueSelectedListener(this);

        List<Map.Entry<Integer, KnowledgeIssue>> ls = new ArrayList<>(knowledgeMap.entrySet());
        setPieChartData(pieChart,ls);
        pieChart.setOnChartValueSelectedListener(this);

        //add item click event
        viewBody.setOnItemClickListener((parent, view, position, id) -> {
            viewAdapter.setSelectPosition(position);
            mDropDownMenu.setTabMenuText(viewType[position]);
            mDropDownMenu.closeMenu();
        });

        showBody.setOnItemClickListener((parent, view, position, id) -> {
            showAdapter.setSelectPosition(position);
            mDropDownMenu.setTabMenuText(showType[position]);
            mDropDownMenu.closeMenu();
        });

        sortBody.setOnItemClickListener((adapterView, view, i, l) -> {
            sortAdapter.setSelectPosition(i);
            mDropDownMenu.setTabMenuText(sortType[i]);
            mDropDownMenu.closeMenu();
        });

        mDropDownMenu.setDropDownMenu(mHeaders, mPopupViews, chartInflate);

        viewAdapter.setSelectPosition(0);
        mDropDownMenu.setTabMenuText(viewType[0]);

        //查询按钮
        ImageView search=view.findViewById(R.id.search);
        search.setOnClickListener(view1 -> {
            selectedSubjectId.clear();
            for(BaseItem item:fAdapter.getMultiContent()){
                selectedSubjectId.add(item.getId());
            }

            knowledgeMap=memorizeDB.queryBySelection(selectedSubjectId, this.startDate,this.endDate);

            if(knowledgeMap.isEmpty()){
                notice.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.GONE);
                barChart.setVisibility(View.GONE);
                return;
            }else{
                notice.setVisibility(View.GONE);
            }
            //借助list实现hashMap排序//
            //注意 ArrayList<>() 括号里要传入map.entrySet()
            List<Map.Entry<Integer, KnowledgeIssue>> list = new ArrayList<>(knowledgeMap.entrySet());
            if(sortAdapter.getSelectPosition()!=-1&&sortAdapter.getSelectPosition()!=0) {
                if(viewAdapter.getSelectPosition()==0)//按出现次数排序
                    list.sort((o1, o2) -> {
                        //按照出现次数值，从大到小排序
                        if (sortAdapter.getSelectPosition() == 2)
                            return o1.getValue().occurrence - o2.getValue().occurrence;
                        else
                            return o2.getValue().occurrence - o1.getValue().occurrence;
                    });
                else if(viewAdapter.getSelectPosition()==1)//按错误率排序
                    list.sort((o1, o2) -> {
                        //按照得分率，从大到小排序
                        //*1000是为了保存精度
                        int rate1 = (int) (o1.getValue().grade / o1.getValue().totalGrade * 1000);
                        int rate2 = (int) (o2.getValue().grade / o2.getValue().totalGrade * 1000);
                        if (sortAdapter.getSelectPosition() == 2)
                            return rate1 - rate2;
                        else
                            return rate2 - rate1;

                    });
            }

            //判断显示方式是饼图还是柱状图
            if(showAdapter.getSelectPosition()==0){//饼图
                pieChart.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.GONE);
                setPieChartData(pieChart,list);
            }else if(showAdapter.getSelectPosition()==1){//柱状图
                pieChart.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);
                items.clear();
                for (Map.Entry<Integer,KnowledgeIssue> entry : list){
                    BaseItem item=new BaseItem();
                    item.setType(BaseItem.KNOWLEDGE_TYPE);
                    item.setId(entry.getKey());
                    item.setName(entry.getValue().name);
                    items.add(item);
                }
                initBarChartStyle(barChart,items);
                setBarChartData(barChart,list);
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
        pieChart.setExtraOffsets(0, 0, 10, 0);
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
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(5f);
        // entry label styling
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(15f);
    }



    protected void setPieChartData(PieChart pieChart, List<Map.Entry<Integer, KnowledgeIssue>> list) {

        List<PieEntry> entries = new ArrayList<>();
        //注意这里遍历的是list，也就是我们将map.Entry放进了list，排序后的集合
        int rest = KnowledgeIssue.totalOccurences;
        int num=0;
        String label="";
        if(viewAdapter.getSelectPosition()==0){//按出现次数查询
            pieChart.setUsePercentValues(false);
            for (Map.Entry<Integer,KnowledgeIssue> entry : list){
                PieEntry p=new PieEntry(entry.getValue().occurrence,entry.getValue().name+"  "+entry.getValue().occurrence+"次");
                p.setX(entry.getKey());
                entries.add(p);
                rest-=entry.getValue().occurrence;
                if(num++>=20)break;
            }
            if(rest>0)
                entries.add(new PieEntry(rest,"其他"));
            label="在题目中出现的次数";

        }else if(viewAdapter.getSelectPosition()==1){//按正确率查询
            pieChart.setUsePercentValues(false);
            NumberFormat formatter = new DecimalFormat("0.00");
            for (Map.Entry<Integer,KnowledgeIssue> entry : list){
                float rate=entry.getValue().grade/entry.getValue().totalGrade;
                PieEntry p=new PieEntry(rate,entry.getValue().name+"  "+formatter.format(rate));
                p.setX(entry.getKey());
                entries.add(p);
            }
            label="得分百分率";
        }

        PieDataSet dataSet = new PieDataSet(entries, label);
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
        data.setValueFormatter(new PieChartFormatter(pieChart,viewAdapter));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);

        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
        pieChart.animateY(1200, Easing.EaseInOutQuad);
    }


    /**
     * 初始化图表的样式
     */
    protected void initBarChartStyle(BarChart barChart,List<BaseItem>items) {
        //关闭描述
        barChart.getDescription().setEnabled(false);
        barChart.setDrawBarShadow(false);
        //开启在柱状图顶端显示值
        barChart.setDrawValueAboveBar(true);
        //设置显示值时，最大的柱数量
        barChart.setMaxVisibleValueCount(30);

        //设置不能同时在x轴和y轴上缩放
        barChart.setPinchZoom(false);
        //设置不画背景网格
        barChart.setDrawGridBackground(false);
        initXYAxisStyle(barChart,items);
    }

    /**
     * 初始化图表X、Y轴的样式
     */
    private void initXYAxisStyle(BarChart barChart,List<BaseItem>items) {
        //设置X轴样式

        ValueFormatter xAxisFormatter = new SubjectFormatter(items);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        //间隔显示
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        //设置Y轴的左侧样式
        ValueFormatter yAxisFormatter = new AccuracyFormatter(viewAdapter);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(yAxisFormatter);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(10f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        //设置Y轴的右侧样式
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(yAxisFormatter);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        //设置图表的数值指示器
        XYMarkerView mv = new XYMarkerView(getContext(), yAxisFormatter,viewAdapter);
        mv.setChartView(barChart);
        barChart.setMarker(mv);
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
    protected void setBarChartData(BarChart barChart,List<Map.Entry<Integer, KnowledgeIssue>> list) {

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
        //设置渐变色
        List<GradientColor> gradientColors = new ArrayList<>();
        gradientColors.add(new GradientColor(startColor1, endColor1));
        gradientColors.add(new GradientColor(startColor2, endColor2));
        gradientColors.add(new GradientColor(startColor3, endColor3));
        gradientColors.add(new GradientColor(startColor4, endColor4));
        gradientColors.add(new GradientColor(startColor5, endColor5));


        //注意这里遍历的是list，也就是我们将map.Entry放进了list，排序后的集合
        List<BarEntry> values = new ArrayList<>();
        List<IBarDataSet> dataSets = new ArrayList<>();
        int i=0;
        BarDataSet set1 = null;
        if(viewAdapter.getSelectPosition()==0){//按出现次数查询

            for (Map.Entry<Integer,KnowledgeIssue> entry : list){
                BarEntry b=new BarEntry(++i, (int)(entry.getValue().occurrence));
                b.setData(entry.getValue().name);
                values.add(b);
            }
            set1 = new BarDataSet(values, "出现次数  "+startDate+"~"+endDate);
            set1.setDrawIcons(false);
            set1.setGradientColors(gradientColors);

        }else if(viewAdapter.getSelectPosition()==1){//按正确率查询

            for (Map.Entry<Integer,KnowledgeIssue> entry : list){
                float rate=entry.getValue().grade/entry.getValue().totalGrade;
                BarEntry b=new BarEntry(++i,rate);
                b.setData(entry.getValue().name);
                values.add(b);
            }
            set1 = new BarDataSet(values, "得分百分比  "+startDate+"~"+endDate);
            set1.setDrawIcons(false);
            set1.setGradientColors(gradientColors);
        }
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.95f);
        barChart.setData(data);
        barChart.animateY(1200);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if(pieChart.getVisibility()==View.VISIBLE) {//如果当前显示的是饼图
//            Toast.makeText(getContext(), ((PieEntry) e).getLabel(), Toast.LENGTH_SHORT).show();
            if(((PieEntry) e).getLabel().equals("其他"))return;
            BaseItem item=new BaseItem();
            item.setId((int)e.getX());
            MessageEvent.findKnowledge=item;
            EventBus.getDefault().post(new MessageEvent(MessageEvent.FIND_IN_TREE));
        }
        else {

        }
    }


    @Override
    public void onNothingSelected() {

    }
}
