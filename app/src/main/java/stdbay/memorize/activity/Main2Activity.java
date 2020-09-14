package stdbay.memorize.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.shizhefei.view.hvscrollview.HVScrollView;
import com.xuliwen.zoom.ZoomLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import stdbay.memorize.R;
import stdbay.memorize.model.BaseItem;
import stdbay.memorize.model.DrawGeometryView;
import stdbay.memorize.model.MemorizeDB;
import stdbay.memorize.model.TreeNode;

@SuppressWarnings("ALL")
public class Main2Activity extends Activity {
    private static  final int RENAME=-1;
    private static  final int ADD_KNOWLEDGE=0;

    private int subId;
    private TreeNode nowTreeNode=new TreeNode();
    //root是树根
    TreeNode root;
    //treeArragment是按层次记录树的结构
    private List<List<TreeNode>> treeArragment;


    private ProgressDialog progressDialog;
    private ZoomLayout zoom;


    //在获取了数信息之后,修改宽高以适应屏幕

    private int height;
    private int width;
    private float nowW=0,nowH=0;

    private RelativeLayout insertLayout;

    private HVScrollView hv;
    private LayoutParams treeNodeParams;
    private LayoutParams lineParams;
    private MemorizeDB memorizeDB;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,R.id.add_knowledge,0,R.string.add_knowledge);
        menu.add(0, R.id.rename_item, 0, R.string.rename);
        menu.add(0, R.id.delete_item, 0, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_knowledge:
                inputTitleDialog(subId,nowTreeNode.getId(),ADD_KNOWLEDGE);
                break;
            case R.id.rename_item:
                inputTitleDialog(subId,nowTreeNode.getId(),RENAME);
                break;
            case R.id.delete_item:
                BaseItem baseItem=new BaseItem();
                baseItem.setId(nowTreeNode.getId());
                baseItem.setType(BaseItem.KNOWLEDGE_TYPE);
                memorizeDB.deleteItem(baseItem, new MemorizeDB.callBackListener() {
                    @Override
                    public void onFinished() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Main2Activity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                insertLayout.removeAllViews();
                                showKnowledgeTree(5);
//                                hv.scrollTo(nowW,nowH);
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Main2Activity.this,"删除失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d("sql", Objects.requireNonNull(e.getMessage()));
                    }
                });

        }
        return super.onContextItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        memorizeDB=MemorizeDB.getInstance(this);
        hv = findViewById(R.id.hvscrollview);

//        hv.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                int pointNum=motionEvent.getPointerCount();
//                if(pointNum==2)
//                switch (motionEvent.getAction()){
//                    case MotionEvent.ACTION_POINTER_DOWN:
//                    case MotionEvent.ACTION_MOVE:
////                        ViewGroup.LayoutParams p=new ViewGroup.LayoutParams(hv.getLayoutParams());
////                        p.width=(int) (hv.getWidth()/hv.getScaleX());
////                        p.height=(int)(hv.getHeight()/hv.getScaleY());
////                        hv.setLayoutParams(p);
////                        Log.d("TAG","w: "+hv.getWidth()+"h: "+hv.getHeight());
//                }
//                return false;
//            }
//        });


        insertLayout=findViewById(R.id.canvas);
        zoom=findViewById(R.id.zoom);
//        Rect outSize = new Rect();
//        getWindowManager().getDefaultDisplay().getRectSize(outSize);
//        int left = outSize.left;
//        int top = outSize.top;
//        width = outSize.right;
//        height = outSize.bottom;

        DisplayMetrics dm = getResources().getDisplayMetrics();
        //需要减去状态栏高度
        height = dm.heightPixels-getStatusBarHeight();
        width = dm.widthPixels;
//
        LayoutParams layoutParams = new LayoutParams(width,height);
        insertLayout.setLayoutParams(layoutParams);
//        registerForContextMenu(insertLayout);
        showKnowledgeTree(5);
    }


    @SuppressLint("SetTextI18n")
    public void drawbutton(List<TreeNode> node, float treeNodeY, float treeNodeX, int treeLevel) {
        if(node.isEmpty())return;

        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setStartOffset(50);// 动画延迟
        animation.setFillAfter(true);
        animation.setDuration(500);

//        存储线的起点y坐标
        float lineStartY = treeNodeY+TreeNode.treeNodeH/2;

        //获取这一层次所需的宽度
        int leavesNum=0;
        for(int i=0;i<node.size();++i){
            leavesNum+=MemorizeDB.getLeavesNum(node.get(i));
        }

        //topY存储的是本层最高节点位置
        float topY = treeNodeY - leavesNum * TreeNode.treeNodeIntervalY/2;

        //doneNum代表本次已经绘制的叶子节点个数,用于调整位置
        int doneNum=0;
        for (int i = 0; i < node.size(); i++) {
            float finalTreeNodeY=(int)(topY+(doneNum+(float)(memorizeDB.getLeavesNum(node.get(i)))/2)*TreeNode.treeNodeIntervalY);
            float finalTreeNodeX = treeNodeX + TreeNode.treeNodeIntervalX;

//            定义及设置button属性
            Button treeNodeView=new Button(Main2Activity.this);
            treeNodeView.setBackgroundResource(R.drawable.green_corner);
            treeNodeView.setTextColor(Color.WHITE);
            treeNodeView.setEllipsize(TextUtils.TruncateAt.END);
            treeNodeView.setSingleLine(true);
            treeNodeView.setPadding(10,5,10,5);
            treeNodeView.setTextSize(10 - (int) Math.sqrt(node.get(i).getName().length() - 1));//调整字体
            treeNodeView.setText(node.get(i).getName());
//            定义及设置出场动画
//            treeNodeView.startAnimation(animation);

            //把当前node实例化存储,用于view的点击事件的调用
            final TreeNode nodeInstance =node.get(i);
            final float H=finalTreeNodeY;
            final float W=treeNodeX;
            registerForContextMenu(treeNodeView);
            treeNodeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    hv.scrollTo((int) (W-(width/2/hv.getScaleX())), (int) (H-(500/hv.getScaleX())));
//                    hv.scrollTo((int) (W), (int) (H));
                    subId=5;
                    nowTreeNode.setId(nodeInstance.getId());
//                    view.showContextMenu();
                    float f=hv.getScaleX();
//                    zoom.smoothScale(1.5f, 0, 0);
                    hv.scrollTo(0,0);
//                    hv.scrollTo(0,0);
//                    Toast.makeText(Main2Activity.this,""+hv.getScaleX()+" w"+hv.getWidth()
//                            +" h:"+hv.getHeight(),Toast.LENGTH_SHORT).show();
                }
            });
//            把button通过布局add到页面里
            treeNodeParams = new LayoutParams(TreeNode.treeNodeW, TreeNode.treeNodeH);
            treeNodeParams.topMargin = (int) finalTreeNodeY;
            treeNodeParams.leftMargin = (int) treeNodeX;
            insertLayout.addView(treeNodeView, treeNodeParams);
//            把线绘制到页面里
            if (treeLevel > 0) {
                final int lineDeltaY = (int) ((finalTreeNodeY + TreeNode.treeNodeH/2)  - lineStartY);
                final int lineDeltaX = TreeNode.treeNodeIntervalX-TreeNode.treeNodeW;
                final int lineStartX= (int) (treeNodeX-TreeNode.treeNodeIntervalX);
                DrawGeometryView lineView;
                if (lineDeltaY >= 0) {
                    lineView = new DrawGeometryView(this, 0, 0, lineDeltaX, lineDeltaY );
                    lineParams = new LayoutParams(TreeNode.treeNodeIntervalX , (int) (finalTreeNodeY-treeNodeY+10));
                    lineParams.topMargin = (int) lineStartY;
                } else {
                    //如果deltaY<0,从(0,0)开始绘制会导致图形丢失,因此需要调整位置
                    //+2是因为了抵消正反向绘制时的损失
                    lineView = new DrawGeometryView(this, 0, -lineDeltaY+2, lineDeltaX , 2);
                    lineParams = new LayoutParams(TreeNode.treeNodeIntervalX , (int) (treeNodeY-finalTreeNodeY+10));
                    lineParams.topMargin = (int) (lineStartY+lineDeltaY);
                }
                lineView.invalidate();
//                lineView.startAnimation(animation);
                lineParams.leftMargin = lineStartX + TreeNode.treeNodeW;
                insertLayout.addView(lineView, lineParams);
            }
            doneNum+=(memorizeDB.getLeavesNum(node.get(i)));
            drawbutton(node.get(i).getChildren(),finalTreeNodeY, finalTreeNodeX,treeLevel + 1);
        }
    }

    private void showKnowledgeTree(int subId){
        showProgressDialog();
        memorizeDB.GoThroughKnowledge(5,  new MemorizeDB.callBackListener() {
            @Override
            public void onFinished() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        root=MemorizeDB.getTreeInfo().getRoot();
                        treeArragment=MemorizeDB.getTreeInfo().getTreeLevel();
                        closeProgressDialog();
//                        Toast.makeText(Main2Activity.this,"成功",Toast.LENGTH_SHORT).show();
                        List<TreeNode>Root=new ArrayList<>();
                        Root.add(root);
                        //计算需要画布的大小,防止图形显示不全
                        //由于relativeLayour会自动向右下方扩展,所以只需要计算高度
                        if(height<MemorizeDB.getLeavesNum(root)*TreeNode.treeNodeIntervalY)
                            height=MemorizeDB.getLeavesNum(root)*TreeNode.treeNodeIntervalY;
                        drawbutton(Root,height/2, 50, 0);
//                        hv.scrollTo(0,height/2);
                    }
                });
            }
            @Override
            public void onError(final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(Main2Activity.this,"出现错误",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            }
        });
    }


    private void inputTitleDialog(final int subId, final int fatherId, final int type) {
        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);
        String hint="请输入知识点名称";
        inputServer.setHint(hint);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(" ").setView(inputServer).setNegativeButton(
                getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String name=inputServer.getText().toString();
                        if(type==ADD_KNOWLEDGE){
                            memorizeDB.addKnowledge(fatherId, subId, name, new MemorizeDB.callBackListener() {
                                @Override
                                public void onFinished() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Main2Activity.this,"添加知识点成功",Toast.LENGTH_SHORT).show();
                                            insertLayout.removeAllViews();
                                            showKnowledgeTree(5);
//                                            hv.scrollTo((int)nowW+width/2,0);
                                        }
                                    });
                                }

                                @Override
                                public void onError(Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Main2Activity.this,"添加知识点失败,请检查是否有同名项",Toast.LENGTH_SHORT).show();
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
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Main2Activity.this,"改名成功",Toast.LENGTH_SHORT).show();
                                            insertLayout.removeAllViews();
                                            showKnowledgeTree(5);
//                                            hv.scrollTo(nowW,nowH);
                                        }
                                    });
                                }

                                @Override
                                public void onError(Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Main2Activity.this,"改名失败,请检查是否有同名项",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
        builder.show();
    }



    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog!=null)
            progressDialog.dismiss();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}