package stdbay.memorize.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.shizhefei.view.hvscrollview.HVScrollView;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import stdbay.memorize.R;
import stdbay.memorize.adapter.FlexboxLayoutAdapter;
import stdbay.memorize.model.BaseItem;
import stdbay.memorize.model.DrawGeometryView;
import stdbay.memorize.model.MemorizeDB;
import stdbay.memorize.model.TreeNode;
import stdbay.memorize.util.MessageEvent;

public class KnowledgeTreeFragment extends Fragment {

    public boolean isSelectMode=false;

    private MaterialDialog knowledgeDialog;
    private FlexboxLayoutAdapter fAdapter;


    private XUISimplePopup popup;
//    private CookieBar cookieBar;

    private LinearLayout infoBanner;
    private TextView selectInfo;
    private Button returnKnowledge;

    private TextView notice;

    private View view;

    private MiniLoadingDialog loadingDialog;

    private MaterialSpinner mMaterialSpinner;

    private static  final int RENAME=-1;
    private static  final int ADD_KNOWLEDGE=0;
    private static  final int CHANGE_ANNOTATION=1;

    private int subId=0;
    private TreeNode nowTreeNode=new TreeNode();
    //root是树根
    private TreeNode root;

    private HVScrollView hv;


//    private ProgressDialog progressDialog;

    private ScaleAnimation animation;


    //在获取了数信息之后,改名宽高以适应屏幕

    private int height;
    private int width;

    private RelativeLayout insertLayout;

    private MemorizeDB memorizeDB;

    private Button findNode;

    @Override
    public void onDestroyView() {
        loadingDialog.recycle();
        MessageEvent.selectedknowledges.clear();
        super.onDestroyView();
    }

    public static KnowledgeTreeFragment getInstance(){
        Bundle bundle = new Bundle();
        KnowledgeTreeFragment myFragment = new KnowledgeTreeFragment();
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.knowledge_fragment, container, false);
        Bundle bundle = getArguments();
        if(bundle != null) {
            initView();
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void drawbutton(final List<TreeNode> node, float treeNodeY, final float treeNodeX, int treeLevel) {
        selectInfo.setText("已选择"+MessageEvent.selectedknowledges.size()+"项");
        if(node.isEmpty())return;


//        存储线的起点y坐标
        float lineStartY = treeNodeY+ (TreeNode.treeNodeH >> 1);

        //获取这一层次所需的宽度
        int leavesNum=0;
        for(int i=0;i<node.size();++i){
            leavesNum+=MemorizeDB.getLeavesNum(node.get(i));
        }

        //topY存储的是本层最高节点位置
        float topY = treeNodeY - (leavesNum * TreeNode.treeNodeIntervalY >> 1);

        //doneNum代表本次已经绘制的叶子节点个数,用于调整位置
        int doneNum=0;
        for (int i = 0; i < node.size(); i++) {
            final float finalTreeNodeY=(int)(topY+(doneNum+(float)(MemorizeDB.getLeavesNum(node.get(i)))/2)*TreeNode.treeNodeIntervalY);
            float finalTreeNodeX = treeNodeX + TreeNode.treeNodeIntervalX;

//            定义及设置button属性
            Button treeNodeView=new Button(getActivity());
            treeNodeView.setTextColor(Color.WHITE);
            treeNodeView.setEllipsize(TextUtils.TruncateAt.END);
            treeNodeView.setSingleLine(true);
            treeNodeView.setPadding(10,5,10,5);
            treeNodeView.setTextSize(10 - (int) Math.sqrt(node.get(i).getName().length() - 1));//调整字体
            treeNodeView.setText(node.get(i).getName());
//            定义及设置出场动画
            treeNodeView.startAnimation(animation);

            //把当前node实例化存储,用于view的点击事件的调用
            final TreeNode nodeInstance =node.get(i);

            final BaseItem item=new BaseItem();


//
//            for(int j=0;j<MessageEvent.selectedknowledges.size();++j){
//                if(nodeInstance.getId()==MessageEvent.selectedknowledges.get(j).getId()){
//                    treeNodeView.setSelected(true);
//                    treeNodeView.setBackgroundResource(R.drawable.red_corner);
//                }
//            }

            item.setType(BaseItem.KNOWLEDGE_TYPE);
            item.setId(nodeInstance.getId());
            item.setName(nodeInstance.getName());
            if(nodeInstance.getFather()!=null) {
                item.setFatherId(nodeInstance.getFather().getId());
            }

            boolean in=false;
            for(int j=0;j<MessageEvent.selectedknowledges.size();++j){
                if(item.getId()==MessageEvent.selectedknowledges.get(j).getId()){
                    in=true;
                    break;
                }
            }
            if(in) {
                treeNodeView.setSelected(true);
                treeNodeView.setBackgroundResource(R.drawable.red_corner);
            }else {
                treeNodeView.setSelected(false);
                treeNodeView.setBackgroundResource(R.drawable.green_corner);
            }

            final int W= (int) treeNodeX;
            final int H=(int) finalTreeNodeY;

            treeNodeView.setOnClickListener(view -> {
                if (isSelectMode){
                    infoBanner.setVisibility(View.VISIBLE);
                    if (!view.isSelected()){
                        view.setSelected(true);
                        view.setBackgroundResource(R.drawable.red_corner);
                        MessageEvent.selectedknowledges.add(item);
                    }else{
                        view.setSelected(false);
                        view.setBackgroundResource(R.drawable.green_corner);
                        for(int j=0;j<MessageEvent.selectedknowledges.size();++j){
                            if(MessageEvent.selectedknowledges.get(j).getId()==item.getId()) {
                                MessageEvent.selectedknowledges.remove(MessageEvent.selectedknowledges.get(j));
                            }
                        }
                    }
                    selectInfo.setText("已选择"+MessageEvent.selectedknowledges.size()+"项");
                }else {
                    infoBanner.setVisibility(View.GONE);
                    showCookieBar(nodeInstance.getName(), nodeInstance.getAnnotation());
                    hv.smoothScrollTo(W - 500, H - 500);
                }
            });

            //定位到目标处
            if(MessageEvent.findKnowledge!=null&&
                    MessageEvent.findKnowledge.getId()==nodeInstance.getId()){

                treeNodeView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                    hv.scrollTo(W-500,H-500);
                    MessageEvent.findKnowledge=null;
                });
                treeNodeView.setBackgroundResource(R.drawable.gray_corner);
                infoBanner.setVisibility(View.GONE);
                showCookieBar(nodeInstance.getName(), nodeInstance.getAnnotation());
            }
            treeNodeView.setOnLongClickListener(view -> {
                popup.showDown(view);
                nowTreeNode=nodeInstance;
                return true;
            });
//            把button通过布局add到页面里
            RelativeLayout.LayoutParams treeNodeParams = new RelativeLayout.LayoutParams(TreeNode.treeNodeW, TreeNode.treeNodeH);
            treeNodeParams.topMargin = (int) finalTreeNodeY;
            treeNodeParams.leftMargin = (int) treeNodeX;
            insertLayout.addView(treeNodeView, treeNodeParams);
//            把线绘制到页面里
            if (treeLevel > 0) {
                final int lineDeltaY = (int) ((finalTreeNodeY + TreeNode.treeNodeH/2)  - lineStartY);
                final int lineDeltaX = TreeNode.treeNodeIntervalX-TreeNode.treeNodeW;
                final int lineStartX= (int) (treeNodeX-TreeNode.treeNodeIntervalX);
                DrawGeometryView lineView;
                RelativeLayout.LayoutParams lineParams;
                if (lineDeltaY >= 0) {
                    //+5是为了保证线条完整
                    lineView = new DrawGeometryView(getActivity(), 0, 5, lineDeltaX, lineDeltaY+5);
                    lineParams = new RelativeLayout.LayoutParams(TreeNode.treeNodeIntervalX , (int) (finalTreeNodeY-treeNodeY)+10);
                    lineParams.topMargin = (int) lineStartY;
                } else {
                    //从(0,0)开始绘制会导致图形丢失,因此需要调整位置
                    //+5是因为了抵消正反向绘制时的损失
                    lineView = new DrawGeometryView(getActivity(), 0, -lineDeltaY+5, lineDeltaX , 5);
                    lineParams = new RelativeLayout.LayoutParams(TreeNode.treeNodeIntervalX , (int) (treeNodeY-finalTreeNodeY)+10);
                    lineParams.topMargin = (int) (lineStartY+lineDeltaY);
                }
                lineView.invalidate();
                lineParams.leftMargin = lineStartX + TreeNode.treeNodeW;
                insertLayout.addView(lineView, lineParams);
            }
            doneNum+=(MemorizeDB.getLeavesNum(node.get(i)));
            drawbutton(node.get(i).getChildren(),finalTreeNodeY, finalTreeNodeX,treeLevel + 1);
        }
    }

    private void showKnowledgeTree(){
        loadingDialog.show();
        memorizeDB.GoThroughKnowledge(subId,  new MemorizeDB.callBackListener() {
            @Override
            public void onFinished() {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    loadingDialog.dismiss();

                    root=MemorizeDB.getTreeRoot();

                    List<TreeNode>Root=new ArrayList<>();
                    Root.add(root);
                    //计算需要画布的大小,防止图形显示不全
                    //由于relativeLayour会自动向右下方扩展,所以只需要计算高度
                    Display defaultDisplay = getActivity().getWindowManager().getDefaultDisplay();
                    Point point = new Point();
                    defaultDisplay.getSize(point);
                    int h=point.y;
                    height=MemorizeDB.getLeavesNum(root)*TreeNode.treeNodeIntervalY;
                    if(h>height)
                        height=h;
                    width=MemorizeDB.getTreeDepth()*TreeNode.treeNodeIntervalX;
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,height);
                    insertLayout.setLayoutParams(layoutParams);
//                        zoom.setLayoutParams(layoutParams);
                    drawbutton(Root,(height-TreeNode.treeNodeH)/2, TreeNode.treeNodeW/2, 0);
                });
            }
            @Override
            public void onError(final Exception e) {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    SnackbarUtils.Short(hv,"出现错误")
                            .danger().show();
                    e.printStackTrace();
                });
            }
        });
    }



    private void showInput( final int type){
        String s;
        if(type==ADD_KNOWLEDGE) {
            s="新建知识点";
            new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                    .title(s)
                    .customView(R.layout.add_knowledge_dialog,true)
                    .positiveText("确认")
                    .onPositive((dialog, which) -> {
                        assert (dialog).getCustomView() != null;
                        String name=((EditText) ((dialog).getCustomView().
                                findViewById(R.id.knowledge_name))).getText().toString();
                        String annotation=((EditText)(dialog.getCustomView().
                                findViewById(R.id.knowledge_annotation))).getText().toString();
                        memorizeDB.addKnowledge(nowTreeNode.getId(), subId, name, annotation,new MemorizeDB.callBackListener() {
                            @Override
                            public void onFinished() {
                                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                    SnackbarUtils.Short(hv,"添加知识点成功")
                                            .confirm().show();
                                    insertLayout.removeAllViews();
                                    showKnowledgeTree();
                                });
                            }

                            @Override
                            public void onError(Exception e) {
                                Objects.requireNonNull(getActivity()).runOnUiThread(() -> SnackbarUtils.Short(hv,"添加知识点失败,请检查是否有同名项")
                                        .danger().show());
                            }
                        });
                    })
                    .negativeText("取消")
                    .cancelable(true)
                    .show();
        }
        else if(type==RENAME) {
            s="改名";
            new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                    .title(s)
                    .negativeText("取消")
                    .positiveText("确认")
                    .input("请输入新名称", nowTreeNode.getName(), false, (dialog, input) -> {
                        String name=input.toString();
                        BaseItem item=new BaseItem();
                        item.setType(BaseItem.KNOWLEDGE_TYPE);
                        item.setId(nowTreeNode.getId());
                        memorizeDB.reName(item, name, new MemorizeDB.callBackListener() {
                            @Override
                            public void onFinished() {
                                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                    SnackbarUtils.Short(hv,"改名成功")
                                            .confirm().show();
                                    insertLayout.removeAllViews();
                                    showKnowledgeTree();
//                                    clearCookieBar();
                                });
                            }

                            @Override
                            public void onError(Exception e) {
                                Objects.requireNonNull(getActivity()).runOnUiThread(() -> SnackbarUtils.Short(hv,"改名失败,请检查是否有同名项")
                                        .danger().show());
                            }
                        });
                    }).show();
        }
        else if(type==CHANGE_ANNOTATION) {
            s="修改注释";
            new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                    .title(s)
                    .negativeText("取消")
                    .positiveText("确认")
                    .input("请输入新注释", nowTreeNode.getAnnotation(), true, (dialog, input) -> memorizeDB.changeKnowledgeAnnotation(nowTreeNode.getId(), input.toString(), new MemorizeDB.callBackListener() {
                        @Override
                        public void onFinished() {
                            SnackbarUtils.Short(hv,"修改注释成功")
                                    .confirm().show();
                            insertLayout.removeAllViews();
                            showKnowledgeTree();
//                            clearCookieBar();
                        }

                        @Override
                        public void onError(Exception e) {
                            SnackbarUtils.Short(hv,"修改注释失败")
                                    .danger().show();
                        }
                    })).show();
        }
    }

    private void initListPopup() {
        String[] tmp = new String[]{"新建知识点","改名","修改注释","删除"};
        popup = new XUISimplePopup(Objects.requireNonNull(getContext()), tmp)
                .create(DensityUtils.dp2px(getContext(), 170), (adapter, item, position) -> {
                    //id等于0说明是根节点,不能改名
                    if(!item.getTitle().toString().equals("新建知识点")
                            &&nowTreeNode.getId()==0){
                        SnackbarUtils.Short(hv, "不能对根节点操作")
                                .warning().show();
                        return;
                    }
                    switch(item.getTitle().toString()){
                        case "新建知识点":
                            showInput(ADD_KNOWLEDGE);
                            break;
                        case "改名":
                            showInput(RENAME);
                            break;
                        case "修改注释":
                            showInput(CHANGE_ANNOTATION);
                            break;
                        case "删除":
                            new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                                    .content("确认删除吗?")
                                    .positiveText("确认")
                                    .positiveColor(Color.parseColor("#cc5555"))
                                    .negativeText("取消")
                                    .onPositive((dialog, which) -> {
                                        BaseItem baseItem = new BaseItem();
                                        baseItem.setId(nowTreeNode.getId());
                                        baseItem.setType(BaseItem.KNOWLEDGE_TYPE);
                                        memorizeDB.deleteItem(baseItem.getId(),baseItem.getType(), new MemorizeDB.callBackListener() {
                                            @Override
                                            public void onFinished() {
                                                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                                    SnackbarUtils.Short(hv, "删除成功")
                                                            .confirm().show();
                                                    insertLayout.removeAllViews();
                                                    showKnowledgeTree();
//                                                    clearCookieBar();
                                                });
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
//                                Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
                                                    SnackbarUtils.Short(hv, "删除失败")
                                                            .danger().show();
                                                });
                                                Log.d("sql", Objects.requireNonNull(e.getMessage()));
                                            }
                                        });
                                    })
                                    .show();
                            break;
                    }
                })
                .setHasDivider(true);
    }

    @SuppressLint("ResourceType")
    private void showCookieBar(String title, String content){
//        clearCookieBar();
//        cookieBar=CookieBar.builder(getActivity())
//                .setTitle(title)
//                .setMessage(content)
//                .setDuration(-1)
//                .setBackgroundColor(R.color.dark_green)
//                .setActionColor(android.R.color.white)
//                .setTitleColor(android.R.color.white)
//                .setAction("关闭", view -> {
//
//                })
//                .show();
    }

//    public void clearCookieBar(){
//        if(cookieBar!=null)
//            cookieBar.dismiss();
//    }

    private void initDropDownMenu(int subId){
        final Map<String,Integer> subjects= memorizeDB.getSubjects();
        List<String>l=new ArrayList<>();
        for(Map.Entry<String,Integer>entry:subjects.entrySet()){
            l.add(entry.getKey());
        }
        if(subId!=0)
            this.subId = subId;
        else
            this.subId=subjects.get(l.get(0));
        if(!l.isEmpty()) {
            notice.setVisibility(View.GONE);
            hv.setVisibility(View.VISIBLE);
            mMaterialSpinner.setItems(l);
            mMaterialSpinner.setOnItemSelectedListener((spinner, position, id, item) -> {
                this.subId = subjects.get(item.toString());
                insertLayout.removeAllViews();
                showKnowledgeTree();
            });
            mMaterialSpinner.setEnabled(true);
            int index=0;
            for(Map.Entry<String,Integer>entry:subjects.entrySet()){
                if(entry.getValue()==subId){
                    mMaterialSpinner.setSelectedIndex(index);
                    break;
                }
                index++;
            }
        }else{
            mMaterialSpinner.setItems(new ArrayList<>());
            notice.setVisibility(View.VISIBLE);
            hv.setVisibility(View.GONE);
        }
    }

    private void initView(){

        loadingDialog = WidgetUtils.getMiniLoadingDialog(Objects.requireNonNull(getContext()));
        mMaterialSpinner=view.findViewById(R.id.spinner);
        memorizeDB=MemorizeDB.getInstance(getActivity());
        hv = view.findViewById(R.id.hvscroll);
        insertLayout=view.findViewById(R.id.canvas);



        notice=view.findViewById(R.id.notice);

        infoBanner=view.findViewById(R.id.info_banner);
        selectInfo=view.findViewById(R.id.select_info);
        returnKnowledge=view.findViewById(R.id.return_knowledge);
        returnKnowledge.setOnClickListener(view1 -> EventBus.getDefault().post(new MessageEvent(MessageEvent.KNOWLEDGE_RETURN)));

        animation= new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setStartOffset(50);// 动画延迟
        animation.setFillAfter(false);
        animation.setDuration(500);
        initListPopup();
        initDropDownMenu(MessageEvent.findKnowledge==null?0:memorizeDB.getSubId(MessageEvent.findKnowledge.getId()));
        if(hv.getVisibility()==View.VISIBLE)
            showKnowledgeTree();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        if(event.getType()==MessageEvent.ITEM_CHANGED){
            //bookFragment的内容变动,这边相应需要更新
            insertLayout.removeAllViews();
            initDropDownMenu(subId);
            if(hv.getVisibility()==View.VISIBLE)
                showKnowledgeTree();
        }
        else if(event.getType()==MessageEvent.FIND_IN_TREE){
            insertLayout.removeAllViews();
            initDropDownMenu(memorizeDB.getSubId(MessageEvent.findKnowledge.getId()));
            if(hv.getVisibility()==View.VISIBLE) {
                showKnowledgeTree();
//                hv.smoothScrollTo(findX-500,findY-500);
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        if(isSelectMode)
            infoBanner.setVisibility(View.VISIBLE);
        else
            infoBanner.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden) {
//            Toast.makeText(getContext(), "暂停", Toast.LENGTH_SHORT).show();
        }
        else {
            if(isSelectMode)
                infoBanner.setVisibility(View.VISIBLE);
            else
                infoBanner.setVisibility(View.GONE);
//            Toast.makeText(getContext(), "开启", Toast.LENGTH_SHORT).show();
        }
    }
}
