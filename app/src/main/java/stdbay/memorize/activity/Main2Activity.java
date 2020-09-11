package stdbay.memorize.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.shizhefei.view.hvscrollview.HVScrollView;

import java.util.ArrayList;
import java.util.List;

import stdbay.memorize.R;
import stdbay.memorize.model.DrawGeometryView;
import stdbay.memorize.model.MemorizeDB;
import stdbay.memorize.model.TreeNode;

@SuppressWarnings("ALL")
public class Main2Activity extends Activity {

    TreeNode root;
    private int treeNodeIntervalY = 300;
    private ProgressDialog progressDialog;
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


    private int height;
    private int width;

    private List<Integer> treeArragment;
    private RelativeLayout insertLayout;
    private HVScrollView hv;
    private LayoutParams layoutParams;
    private LayoutParams layoutParams1;
    private boolean model = true;

    private MemorizeDB memorizeDB;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TextView murp_nodemodel_title;
        murp_nodemodel_title = findViewById(R.id.murp_nodemodel_title);
        murp_nodemodel_title.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), !model ? "已切换到擦除模式，点击节点会擦除后面节点，赶快试试吧。" : "已切换到正常模式，所有节点在一张图上，赶快试试吧。", Toast.LENGTH_LONG).show();
                model = !model;
            }
        });
        insertLayout=findViewById(R.id.layout_zone);
        hv = findViewById(R.id.hvscrollview);
        WindowManager wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height=wm.getDefaultDisplay().getHeight();

        ViewGroup.LayoutParams p=insertLayout.getLayoutParams();
        p.height=height;
        p.width=width;
        insertLayout.setLayoutParams(p);

        memorizeDB=MemorizeDB.getInstance(this);

        DrawGeometryView view = new DrawGeometryView(this, 100, 100, 300, 400);
        layoutParams1 = new LayoutParams(1000, 1000);
        view.invalidate();
        layoutParams1.topMargin = 00;// lineStartY-600;//Math.min(lineStartY+100,treeNodeY+100
        layoutParams1.leftMargin = 00;// lineX+300;
//        layoutParams1.leftMargin -= 100;
        insertLayout.addView(view, layoutParams1);

        view = new DrawGeometryView(this, 100, 100, 300, 400);
        layoutParams1 = new LayoutParams(1000, 1000);
        view.invalidate();
        layoutParams1.topMargin = 300;// lineStartY-600;//Math.min(lineStartY+100,treeNodeY+100
        layoutParams1.leftMargin = 300;// lineX+300;
        insertLayout.addView(view, layoutParams1);
//        Button bt=new Button(this);
//        layoutParams=new RelativeLayout.LayoutParams(100,100);
//        layoutParams.topMargin=50;
//        layoutParams.leftMargin=50;
//        insertLayout.addView(bt,layoutParams);
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//        showKnowledgeTree(5);

    }
    @SuppressLint("SetTextI18n")
    public void drawbutton(final List<TreeNode> node, int treeNodeY, int treeNodeX, final int treeLevel,int[]levelNum) {
        if(node.isEmpty())return;

        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setStartOffset(50);// 动画延迟
        animation.setFillAfter(true);
        animation.setDuration(500);

//        存储线的起点y坐标
        int lineStartY = treeNodeY;
//        得到下一层级需要绘制的数量
        int num = 1;
        if (treeLevel > 0) num = node.size();// 下一层个数
        int treeNodeW=200;
        int treeNodeH = 200;
//        得到下一级第一个按钮的y坐标
        //转换格式是为了不损失精度
        treeNodeY = (int)((float)height - (float)(treeArragment.get(treeLevel) - 1) * (float) treeNodeIntervalY)/2;
//        移动当前布局到页面中心
//        if (treeLevel > 0) hv.scrollTo(treeNodeX - 400, treeNodeY - 100);

//        存储下一级首个button坐标
        final int finalTreeNodeY = treeNodeY;
        final int finalTreeNodeX = treeNodeX;
        for (int i = 0; i < num; i++) {
//            int treeNodeW = treeLevel % 2 == 0 ? treeNodeIntervalY : 200;
//            定义及设置button属性
            Button treeNodeView=new Button(Main2Activity.this);
            treeNodeView.setBackgroundResource(R.drawable.green_corner);
            treeNodeView.setTextColor(Color.WHITE);
            treeNodeView.setTextSize(15 - (int) Math.sqrt(node.get(i).getName().length() - 1));//调整字体
            treeNodeView.setText(node.get(i).getName());
//            定义及设置出场动画
            treeNodeView.startAnimation(animation);

            //把当前node实例化存储,用于view的点击事件的调用
            final TreeNode nodeInstance =node.get(i);
            treeNodeView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(Main2Activity.this,nodeInstance.getName(),Toast.LENGTH_SHORT).show();
                    view.setBackgroundResource(R.drawable.red_corner);
                    return true;
                }
            });
//            把button通过布局add到页面里
            layoutParams = new LayoutParams(treeNodeW, treeNodeH);
            layoutParams.topMargin = treeNodeY + (levelNum[treeLevel] * treeNodeIntervalY);
            layoutParams.leftMargin = treeNodeX;
            insertLayout.addView(treeNodeView, layoutParams);
//            把线绘制到页面里
            if (treeLevel > 0) {
                final int i2 = treeNodeY  + levelNum[treeLevel] * treeNodeIntervalY - lineStartY ;
                int lineEndX=treeNodeX+100;
                int lineEndY=treeNodeY+levelNum[treeLevel] * treeNodeIntervalY;
                DrawGeometryView view;
                int lineStartX=treeNodeX-treeNodeIntervalY / 2 * 3;
                if (i2 >= 0) {
                    view = new DrawGeometryView(this, lineStartX, lineStartY,  lineEndX, lineEndY );
                    layoutParams1 = new LayoutParams(Math.abs(lineStartX - treeNodeX) + 500, 300 + treeNodeY + levelNum[treeLevel] * 300 - lineStartY);
                    view.invalidate();
                    layoutParams1.topMargin = (lineStartY + 100);// lineStartY-600;//Math.min(lineStartY+100,treeNodeY+100
                    layoutParams1.leftMargin = lineStartX + treeNodeW;
                    insertLayout.addView(view, layoutParams1);
                } else {
                    view = new DrawGeometryView(this, 0, -i2, treeNodeX - lineStartX - 200 + 100, 0);
                    layoutParams1 = new LayoutParams(Math.abs(lineStartX - treeNodeX) + 500, 100 + Math.abs(treeNodeY + levelNum[treeLevel] * 300 - lineStartY));
                    view.invalidate();
                    layoutParams1.topMargin = (treeNodeY + 100 + levelNum[treeLevel] * 300) ;// lineStartY-600;//Math.min(lineStartY+100,treeNodeY+100
                    layoutParams1.leftMargin = lineStartX + treeNodeW;// lineStartX+300;
                    insertLayout.addView(view, layoutParams1);
                }
            }
            levelNum[treeLevel]++;
            int Y=finalTreeNodeY+levelNum[treeLevel]*treeNodeIntervalY;
            int X = finalTreeNodeX + treeNodeIntervalY / 2 * 3;
            drawbutton(node.get(i).getChildren(),Y, X,treeLevel + 1,levelNum);
        }
    }
//    @SuppressLint("SetTextI18n")
//    public void drawbutton(final List<TreeNode> node, int treeNodeY, int treeNodeX, final int treeLevel,int[]levelNum) {
//        if(node.isEmpty())return;
//
//        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        animation.setInterpolator(new OvershootInterpolator());
//        animation.setStartOffset(5000);// 动画延迟
//        animation.setFillAfter(true);
//        animation.setDuration(500);
//
////        存储线的起点y坐标
//        int lineStartY = treeNodeY;
////        得到下一层级需要绘制的数量
//        int num = 1;
//        if (treeLevel > 0) num = node.size();// 下一层个数
//        int treeNodeW=200;
//        int treeNodeH = 200;
//        int treeNodeIntervalY = 300;
////        得到下一级第一个按钮的y坐标
//        //转换格式是为了不损失精度
//        treeNodeY = (int)((float)width/2 - (float)(treeArragment.get(treeLevel) - 1) / 2 * (float) treeNodeIntervalY);
////        移动当前布局到页面中心
//        if (treeLevel > 0) hv.scrollTo(treeNodeX - 400, treeNodeY - 100);
//
////        存储下一级首个button坐标
//        final int finalTreeNodeY = treeNodeY;
//        final int finalTreeNodeX = treeNodeX;
//        for (int i = 0; i < num; i++) {
////            int treeNodeW = treeLevel % 2 == 0 ? treeNodeIntervalY : 200;
////            定义及设置button属性
//            Button treeNodeView=new Button(Main2Activity.this);
//            treeNodeView.setBackgroundResource(R.drawable.green_corner);
//            treeNodeView.setTextColor(Color.WHITE);
//            treeNodeView.setTextSize(15 - (int) Math.sqrt(node.get(i).getName().length() - 1));//调整字体
//            treeNodeView.setText(node.get(i).getName());
////            定义及设置出场动画
//            treeNodeView.startAnimation(animation);
//
//            //把当前node实例化存储,用于view的点击事件的调用
//            final TreeNode nodeInstance =node.get(i);
//            treeNodeView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    Toast.makeText(Main2Activity.this,nodeInstance.getName(),Toast.LENGTH_SHORT).show();
//                    view.setBackgroundResource(R.drawable.red_corner);
//                    return true;
//                }
//            });
////            把button通过布局add到页面里
//            layoutParams = new LayoutParams(treeNodeW, treeNodeH);
//            layoutParams.topMargin = treeNodeY + (levelNum[treeLevel] * treeNodeIntervalY);
//            layoutParams.leftMargin = treeNodeX;
//            insertLayout.addView(treeNodeView, layoutParams);
////            把线绘制到页面里
//            if (treeLevel > 0) {
//                final int i2 = treeNodeY  + levelNum[treeLevel] * treeNodeIntervalY - lineStartY ;
//                DrawGeometryView view;
//                int lineStartX=treeNodeX-treeNodeIntervalY / 2 * 3;
//                if (i2 >= 0) {
//                    view = new DrawGeometryView(this, 0, 0, treeNodeX + 100 - (lineStartX + treeNodeIntervalY) + 100, i2 );
//                    layoutParams1 = new LayoutParams(Math.abs(lineStartX - treeNodeX) + 500, 300 + treeNodeY + levelNum[treeLevel] * 300 - lineStartY);
//                    view.invalidate();
//                    layoutParams1.topMargin = (lineStartY + 100);// lineStartY-600;//Math.min(lineStartY+100,treeNodeY+100
//                    layoutParams1.leftMargin = lineStartX + treeNodeW;
//                    insertLayout.addView(view, layoutParams1);
//                } else {
//                    view = new DrawGeometryView(this, 0, -i2, treeNodeX - lineStartX - 200 + 100, 0);
//                    layoutParams1 = new LayoutParams(Math.abs(lineStartX - treeNodeX) + 500, 100 + Math.abs(treeNodeY + levelNum[treeLevel] * 300 - lineStartY));
//                    view.invalidate();
//                    layoutParams1.topMargin = (treeNodeY + 100 + levelNum[treeLevel] * 300) ;// lineStartY-600;//Math.min(lineStartY+100,treeNodeY+100
//                    layoutParams1.leftMargin = lineStartX + treeNodeW;// lineStartX+300;
//                    insertLayout.addView(view, layoutParams1);
//                }
//            }
//            levelNum[treeLevel]++;
//            int Y=finalTreeNodeY+i*treeNodeIntervalY / 2;
//            int X = finalTreeNodeX + treeNodeIntervalY / 2 * 3;
//            drawbutton(node.get(i).getChildren(),Y, X,treeLevel + 1,levelNum);
//        }
//    }

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
                        Toast.makeText(Main2Activity.this,"成功",Toast.LENGTH_SHORT).show();
                        List<TreeNode>Root=new ArrayList<>();
                        Root.add(root);
                        //计算需要画布的大小,防止图形显示不全
                        //由于relativeLayour会自动向右下方扩展,所以只需要计算高度
                        int max=1;
                        for(int i=0;i<treeArragment.size();++i){
                            max=(treeArragment.get(i)>max)?treeArragment.get(i):max;
                        }
                        height=max*treeNodeIntervalY;
                        drawbutton(Root,height, 200, 0,new int[treeArragment.size()]);
                        hv.scrollTo(0,height/2);
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

}