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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.ScreenUtils;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import stdbay.memorize.R;
import stdbay.memorize.adapter.FlexboxLayoutAdapter;
import stdbay.memorize.adapter.FullyGridLayoutManager;
import stdbay.memorize.adapter.GridImageAdapter;
import stdbay.memorize.model.BaseItem;
import stdbay.memorize.model.DrawGeometryView;
import stdbay.memorize.model.KnowledgeItem;
import stdbay.memorize.model.MemorizeDB;
import stdbay.memorize.model.TreeNode;
import stdbay.memorize.util.DeleteUtil;
import stdbay.memorize.util.MessageEvent;
import stdbay.memorize.util.Util;

public class KnowledgeTreeFragment extends Fragment {

    private static List<LocalMedia> mResult=new ArrayList<>();
    public boolean isSelectMode=false;

    private XUISimplePopup popup;
//    private CookieBar cookieBar;

    private LinearLayout infoBanner;
    private TextView selectInfo;

    private TextView notice;

    private View view;

    private MiniLoadingDialog loadingDialog;

    private MaterialSpinner mMaterialSpinner;


    private int subId=0;
    private TreeNode nowTreeNode=new TreeNode();
    //root是树根
    private TreeNode root;

    private HVScrollView hv;

    public static int INSERT=1;
    private static int ADD=2;

//    private ProgressDialog progressDialog;

    private ScaleAnimation animation;


    //在获取了数信息之后,改名宽高以适应屏幕

    private int height;
    private int width;

    private RelativeLayout insertLayout;

    private MemorizeDB memorizeDB;

    private RelativeLayout knowledgeInflate;
    private MaterialDialog knowledgeDialog;
    private FlexboxLayoutAdapter fAdapter;
    private GridImageAdapter gAdapter;
    private EditText annotation;
    private ImageView lock;
    private EditText name;

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
                //id等于0说明是根节点
                if(nodeInstance.getId()==0)
                    return;
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

                    KnowledgeItem knowledgeItem = memorizeDB.getKnowledge(nodeInstance.getId());

                    name.setEnabled(false);
                    annotation.setEnabled(false);

                    name.setText(knowledgeItem.getName());
                    annotation.setText(knowledgeItem.getAnnotation());

                    fAdapter.resetDataSource(knowledgeItem.getProblems());
                    //默认选中所有
                    for (int j=0;j<knowledgeItem.getProblems().size();++j){
                        if(!fAdapter.isSelected(j))
                            fAdapter.select(j);
                    }
                    fAdapter.notifyDataSetChanged();

                    gAdapter.setViewType(GridImageAdapter.VIEW_PIC);
                    gAdapter.setList(knowledgeItem.getPictures());
                    gAdapter.notifyDataSetChanged();
                    
                    //修改锁属性
                    lock.setVisibility(View.VISIBLE);
                    lock.setSelected(false);
                    lock.setOnClickListener(view22 -> {
                        if (view22.isSelected()) {
                            view22.setSelected(false);
                            name.setEnabled(false);
                            annotation.setEnabled(false);
                            gAdapter.setViewType(GridImageAdapter.VIEW_PIC);
                            memorizeDB.changeKnowledge(nodeInstance.getId(), name.getText().toString(), annotation.getText().toString(), gAdapter.getData(), new MemorizeDB.callBackListener() {
                                @Override
                                public void onFinished() {
                                    SnackbarUtils.Custom(hv,"修改成功",700)
                                            .confirm().show();
                                }

                                @Override
                                public void onError(Exception e) {
                                    SnackbarUtils.Custom(hv,"修改失败",700)
                                            .danger().show();
                                }
                            });
                        }else{
                            view22.setSelected(true);
                            name.setEnabled(true);
                            annotation.setEnabled(true);
                            gAdapter.setViewType(GridImageAdapter.SELECT_PIC);
                        }
                        gAdapter.notifyDataSetChanged();
                    });

                    knowledgeDialog = new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                            .backgroundColorRes(R.color.problem_blue)
                            .positiveColorRes(R.color._ccc)
                            .customView(knowledgeInflate, true)
                            .build();
                    knowledgeDialog.show();
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
//                showCookieBar(nodeInstance.getName(), nodeInstance.getAnnotation());
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

    private void showInput(int type){
        String s;
//        if(KnowledgeTreeFragment.ADD_KNOWLEDGE ==ADD_KNOWLEDGE) {
            s="新建知识点";

            name.setEnabled(true);
            annotation.setEnabled(true);

            name.setText("");
            annotation.setText("");

            gAdapter.setViewType(GridImageAdapter.SELECT_PIC);
            gAdapter.setList(new ArrayList<>());
            gAdapter.notifyDataSetChanged();

            MessageEvent.selectedknowledges=new ArrayList<>();
            fAdapter.resetDataSource(new ArrayList<>());
            fAdapter.notifyDataSetChanged();

            //隐藏锁图标
            lock.setVisibility(View.GONE);
            lock.setSelected(true);

            //用于判断本次添加是否作废
            final boolean[] isEffective = {true};
            knowledgeDialog = new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                    .title(s)
                    .titleColorRes(R.color._ccc)
                    .backgroundColorRes(R.color.problem_blue)
                    .positiveColorRes(R.color._ccc)
                    .customView(knowledgeInflate,true)
                    .positiveText("添加")
                    .onPositive((dialog, which) -> {
                        isEffective[0] =false;
                        String nm=name.getText().toString();
                        String ann=annotation.getText().toString();
                        memorizeDB.addKnowledge(type,nowTreeNode.getId(), subId, nm, ann,gAdapter.getData(),new MemorizeDB.callBackListener() {
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
                    }).build();
            knowledgeDialog.setOnDismissListener(dialogInterface -> {
                //作废的话就删除缓存文件以减少存储
                if(isEffective[0]){
                    for(LocalMedia media:mResult){
                        DeleteUtil.delete(media);
                    }
                }
            });
            knowledgeDialog.show();
//        }
    }

    private void initListPopup() {
        String[] tmp = new String[]{"新建知识点","插入知识点","删除"};
        popup = new XUISimplePopup(Objects.requireNonNull(getContext()), tmp)
                .create(DensityUtils.dp2px(getContext(), 170), (adapter, item, position) -> {
                    //id等于0说明是根节点,不能删除
                    if(nowTreeNode.getId()==0&&(!item.getTitle().toString().equals("新建知识点")||
                            !item.getTitle().toString().equals("插入知识点"))
                            ){
                        SnackbarUtils.Short(hv, "不能对根节点操作")
                                .warning().show();
                        return;
                    }
                    switch(item.getTitle().toString()){
                        case "新建知识点":
                            showInput(ADD);
                            break;
                        case "插入知识点":
                            showInput(INSERT);
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

    private void initDropDownMenu(int subId){
        final Map<String,Integer> subjects= memorizeDB.getSubjects();
        List<String>l=new ArrayList<>();
        for(Map.Entry<String,Integer>entry:subjects.entrySet()){
            l.add(entry.getKey());
        }
        if(subId!=0&&memorizeDB.isExist(subId))
            this.subId = subId;
        else
            if(!subjects.isEmpty())
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

    @SuppressLint("InflateParams")
    private void initView(){

        loadingDialog = WidgetUtils.getMiniLoadingDialog(Objects.requireNonNull(getContext()));
        mMaterialSpinner=view.findViewById(R.id.spinner);
        memorizeDB=MemorizeDB.getInstance(getActivity());
        hv = view.findViewById(R.id.hvscroll);
        insertLayout=view.findViewById(R.id.canvas);

        Button cancelSelect=view.findViewById(R.id.cancel_select);
        cancelSelect.setOnClickListener(view1 -> {
            MessageEvent.selectedknowledges.clear();
            EventBus.getDefault().post(new MessageEvent(MessageEvent.CANCEL_SELECT));
            EventBus.getDefault().post(new MessageEvent(MessageEvent.ITEM_CHANGED));
        });

        gAdapter=Util.initGAdapter(getActivity());
        gAdapter.setmOnAddPicClickListener(Util.initOnAddPicListener(gAdapter,getActivity(),new MyResultCallback(gAdapter)));

        knowledgeInflate= (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.knowledge_item,null,false);

        RecyclerView recyclerView = knowledgeInflate.findViewById(R.id.recycler_show);
        recyclerView.setAdapter(gAdapter);
        FullyGridLayoutManager fManager = new FullyGridLayoutManager(getActivity(),
                3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(fManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3,
                ScreenUtils.dip2px(Objects.requireNonNull(getActivity()), 8), false));
        name=knowledgeInflate.findViewById(R.id.name);
        annotation= knowledgeInflate.findViewById(R.id.annotation);
        lock= knowledgeInflate.findViewById(R.id.lock);

        LinearLayout l1=knowledgeInflate.findViewById(R.id.linear1);
        LinearLayout l2=knowledgeInflate.findViewById(R.id.linear2);

        l2.setOnClickListener(view1 -> {
            if (lock.isSelected()) {
                gAdapter.callOnAddPicClick();
            }
            else {
                SnackbarUtils.Custom(hv,"解锁后才能修改",700)
                        .confirm().show();
            }
        });

        RecyclerView knowledgeRV = knowledgeInflate.findViewById(R.id.knowledge_items);
        knowledgeRV.setLayoutManager(Util.getFlexboxLayoutManager(getContext()));
        knowledgeRV.setItemAnimator(null);
        knowledgeRV.setAdapter(fAdapter);

        fAdapter = new FlexboxLayoutAdapter(new ArrayList<>());
        fAdapter.setIsMultiSelectMode(true);
        fAdapter.setCancelable(false);
        fAdapter.setOnItemClickListener((itemView, item, position) -> {
            MessageEvent.findProblem=fAdapter.getData().get(position);
            EventBus.getDefault().post(new MessageEvent(MessageEvent.FIND_IN_PROBLEM));
            knowledgeDialog.dismiss();
        });
        knowledgeRV.setAdapter(fAdapter);

        notice=view.findViewById(R.id.notice);

        infoBanner=view.findViewById(R.id.info_banner);
        selectInfo=view.findViewById(R.id.select_info);
        Button returnKnowledge = view.findViewById(R.id.return_knowledge);
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

    private static class MyResultCallback implements OnResultCallbackListener<LocalMedia> {
        private WeakReference<GridImageAdapter> mAdapterWeakReference;

        MyResultCallback(GridImageAdapter adapter) {
            super();
            this.mAdapterWeakReference = new WeakReference<>(adapter);
        }

        @Override
        public void onResult(List<LocalMedia> result) {
//          TODO 可以通过PictureSelectorExternalUtils.getExifInterface();方法获取一些额外的资源信息，如旋转角度、经纬度等信息
            mResult=result;
            if (mAdapterWeakReference.get() != null) {
                mAdapterWeakReference.get().setList(result);
                mAdapterWeakReference.get().notifyDataSetChanged();
            }

//            for(LocalMedia media:beforeMediaList){
//                boolean in=false;
//                for(int i=0;i<result.size();++i){
//                    if(media.getRealPath().equals(beforeMediaList.get(i).getRealPath())){
//                        in=true;
//                        break;
//                    }
//                }
//                if(!in)
//                    DeleteUtil.delete(media);
//            }
        }

        @Override
        public void onCancel() {
        }
    }
}
