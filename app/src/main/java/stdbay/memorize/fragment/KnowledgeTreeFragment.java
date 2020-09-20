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
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.shizhefei.view.hvscrollview.HVScrollView;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;
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
import stdbay.memorize.model.BaseItem;
import stdbay.memorize.model.DrawGeometryView;
import stdbay.memorize.model.MemorizeDB;
import stdbay.memorize.model.TreeNode;
import stdbay.memorize.util.MessageEvent;

public class KnowledgeTreeFragment extends Fragment {

    private XUISimplePopup popup;
    private CookieBar cookieBar;

    private View view;

    private MiniLoadingDialog loadingDialog;

    private MaterialSpinner mMaterialSpinner;

    private static  final int RENAME=-1;
    private static  final int ADD_KNOWLEDGE=0;

    private int subId;
    private TreeNode nowTreeNode=new TreeNode();
    //root是树根
    private TreeNode root;

    private HVScrollView hv;


//    private ProgressDialog progressDialog;

    private ScaleAnimation animation;


    //在获取了数信息之后,修改宽高以适应屏幕

    private int height;
    private int width;

    private RelativeLayout insertLayout;

    private MemorizeDB memorizeDB;

    @Override
    public void onDestroyView() {
        loadingDialog.recycle();
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
            showKnowledgeTree();
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void drawbutton(List<TreeNode> node, float treeNodeY, final float treeNodeX, int treeLevel) {
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
            treeNodeView.setBackgroundResource(R.drawable.green_corner);
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
            final int W= (int) treeNodeX;
            final int H=(int) finalTreeNodeY;
            treeNodeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCookieBar(nodeInstance.getName(),nodeInstance.getAnnotation());
                    hv.smoothScrollTo(W-500,H-500);
                }
            });

            treeNodeView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    popup.showDown(view);
                    nowTreeNode.setId(nodeInstance.getId());
                    return true;
                }
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
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                    }
                });
            }
            @Override
            public void onError(final Exception e) {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                        SnackbarUtils.Short(hv,"出现错误")
                                .danger().show();
                        e.printStackTrace();
                    }
                });
            }
        });
    }



    private void showInput(final int fatherId, final int type){
        String s="";
        if(type==ADD_KNOWLEDGE)
            s="新建知识点";
        else if(type==RENAME)
            s="重命名";
        new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                .title(s)
                .customView(R.layout.add_knowledge_dialog,true)
                .positiveText("确认")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String name=((EditText) ((dialog).getCustomView().
                                findViewById(R.id.knowledge_name))).getText().toString();
                        String annotation=((EditText)(dialog.getCustomView().
                                findViewById(R.id.knowledge_annotation))).getText().toString();
                        if(type==ADD_KNOWLEDGE){
                            memorizeDB.addKnowledge(fatherId, subId, name, annotation,new MemorizeDB.callBackListener() {
                                @Override
                                public void onFinished() {
                                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SnackbarUtils.Short(hv,"添加知识点成功")
                                                    .confirm().show();
                                            insertLayout.removeAllViews();
                                            showKnowledgeTree();
                                        }
                                    });
                                }

                                @Override
                                public void onError(Exception e) {
                                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SnackbarUtils.Short(hv,"添加知识点失败,请检查是否有同名项")
                                                    .danger().show();
                                        }
                                    });
                                }
                            });
                        }else if(type==RENAME){
                            BaseItem item=new BaseItem();
                            item.setType(BaseItem.KNOWLEDGE_TYPE);
                            item.setId(nowTreeNode.getId());
                            memorizeDB.reName(item, name, new MemorizeDB.callBackListener() {
                                @Override
                                public void onFinished() {
                                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SnackbarUtils.Short(hv,"改名成功")
                                                    .confirm().show();
                                            insertLayout.removeAllViews();
                                            showKnowledgeTree();
                                        }
                                    });
                                }

                                @Override
                                public void onError(Exception e) {
                                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SnackbarUtils.Short(hv,"改名失败,请检查是否有同名项")
                                                    .danger().show();
                                        }
                                    });
                                }
                            });
                        }
                    }
                })
                .negativeText("取消")
                .cancelable(true)
                .show();
    }

    private void initListPopup() {
        String[] tmp = new String[]{"新建知识点","重命名","删除"};
        popup = new XUISimplePopup(Objects.requireNonNull(getContext()), tmp)
                .create(DensityUtils.dp2px(getContext(), 170), new XUISimplePopup.OnPopupItemClickListener() {
                    @Override
                    public void onItemClick(XUISimpleAdapter adapter, AdapterItem item, int position) {
                        switch(item.getTitle().toString()){
                            case "新建知识点":
                                showInput(nowTreeNode.getId(),ADD_KNOWLEDGE);
                                break;
                            case "重命名":
                                showInput(nowTreeNode.getId(),RENAME);
                                break;
                            case "删除":
                                new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                                        .content("确认删除吗?")
                                        .positiveText("确认")
                                        .positiveColor(Color.parseColor("#cc5555"))
                                        .negativeText("取消")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                BaseItem baseItem = new BaseItem();
                                                baseItem.setId(nowTreeNode.getId());
                                                baseItem.setType(BaseItem.KNOWLEDGE_TYPE);
                                                memorizeDB.deleteItem(baseItem, new MemorizeDB.callBackListener() {
                                                    @Override
                                                    public void onFinished() {
                                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                SnackbarUtils.Short(hv, "删除成功")
                                                                        .confirm().show();
                                                                insertLayout.removeAllViews();
                                                                showKnowledgeTree();
//                                hv.scrollTo(nowW,nowH);
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
//                                Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
                                                                SnackbarUtils.Short(hv, "删除失败")
                                                                        .danger().show();
                                                            }
                                                        });
                                                        Log.d("sql", Objects.requireNonNull(e.getMessage()));
                                                    }
                                                });
                                            }
                                        })
                                        .show();
                                break;
                        }
                    }
                })
                .setHasDivider(true);
    }

    @SuppressLint("ResourceType")
    private void showCookieBar(String title, String content){
        clearCookieBar();
        cookieBar=CookieBar.builder(getActivity())
                .setTitle(title)
                .setMessage(content)
                .setDuration(-1)
                .setBackgroundColor(R.color.dark_green)
                .setActionColor(android.R.color.white)
                .setTitleColor(android.R.color.white)
                .setAction("关闭", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .show();
    }

    public void clearCookieBar(){
        if(cookieBar!=null)
            cookieBar.dismiss();
    }

    private void initDropDownMenu(){
        final Map<String,Integer> subjects= memorizeDB.getSubjects();
        List<String>l=new ArrayList<>();
        for(Map.Entry<String,Integer>entry:subjects.entrySet()){
            l.add(entry.getKey());
        }
        if(!l.isEmpty())
            subId=subjects.get(l.get(0));
        mMaterialSpinner.setItems(l);
        mMaterialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(MaterialSpinner spinner, int position, long id, Object item) {
                subId = subjects.get(item.toString());
                insertLayout.removeAllViews();
                showKnowledgeTree();
            }
        }).setSelectedIndex(0);
        mMaterialSpinner.setEnabled(true);

    }

    private void initView(){

        loadingDialog = WidgetUtils.getMiniLoadingDialog(Objects.requireNonNull(getContext()));
        mMaterialSpinner=view.findViewById(R.id.spinner);
        memorizeDB=MemorizeDB.getInstance(getActivity());
        hv = view.findViewById(R.id.hvscroll);
        insertLayout=view.findViewById(R.id.canvas);

        animation= new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setStartOffset(50);// 动画延迟
        animation.setFillAfter(false);
        animation.setDuration(500);
        initListPopup();
        initDropDownMenu();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        if(event.getType()==MessageEvent.ITEM_CHANGED){
            //bookFragment的内容变动,这边相应需要更新
            insertLayout.removeAllViews();
            initDropDownMenu();
            showKnowledgeTree();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
