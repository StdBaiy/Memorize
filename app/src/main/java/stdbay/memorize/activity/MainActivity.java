package stdbay.memorize.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;

import java.util.ArrayList;
import java.util.List;

import stdbay.memorize.util.QuickAdapter;
import stdbay.memorize.R;
import stdbay.memorize.model.Subject;
import stdbay.memorize.db.MyDatabase;

public class MainActivity extends Activity{
    private RecyclerView rv;
    private List<Subject> data;
    private QuickAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private MyDatabase dbHelper;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        rv=(RecyclerView)findViewById(R.id.recycler_view);
//        rv.setHasFixedSize(true);
//        mLayoutManager=new LinearLayoutManager(this);
//        rv.setLayoutManager(mLayoutManager);
//        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
////        StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
////        rv.setLayoutManager(staggeredGridLayoutManager);
//        mAdapter=new ItemAdapter(this, new ItemAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int pos) {
//                Toast.makeText(MainActivity.this,"这是第"+pos+"个元素",Toast.LENGTH_SHORT).show();
//            }
//        });
//        ((ItemAdapter) mAdapter).addItem("addItem",5);
//        rv.setAdapter(mAdapter);
//        rv.setItemAnimator(new DefaultItemAnimator());
//        Button add=(Button)findViewById(R.id.add);
//        add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((ItemAdapter) mAdapter).addItem("addItem",5);
//            }
//        });
//        Button remove =(Button)findViewById(R.id.remove);
//        remove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((ItemAdapter) mAdapter).removeItem(4);
//            }
//        });

        rv=(RecyclerView)findViewById(R.id.recycler_view);
        data=new ArrayList<>();
        Subject subject;
        for(int i=1;i<=20;++i){
            subject=new Subject();
            subject.setSubjectName("Item"+i);
            subject.setSubjectDescription("这是描述内容");
            data.add(subject);
        }

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);
        mAdapter=new QuickAdapter(R.layout.list_item,data);
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(rv);

// 开启拖拽
        mAdapter.enableDragItem(itemTouchHelper, R.layout.list_item, true);
        mAdapter.setOnItemDragListener(onItemDragListener);

// 开启滑动删除
        mAdapter.enableSwipeItem();
        mAdapter.setOnItemSwipeListener(onItemSwipeListener);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(MainActivity.this, "点击了第" + (position + 1) + "条条目", Toast.LENGTH_SHORT).show();
            }
        });

//        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
//
//                Toast.makeText(MainActivity.this, "长按了第" + (position + 1) + "条条目", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

        mAdapter.openLoadAnimation(new BaseAnimation(){

            @Override
            public Animator[] getAnimators(View view) {

                return new Animator[]{
                        ObjectAnimator.ofFloat(view,"scaleX",1,1.1f,1),
                        ObjectAnimator.ofFloat(view,"scaleY",1,1.1f,1)
                };
            }
        });
        mAdapter.isFirstOnly(false);
        rv.setAdapter(mAdapter);

    }
    OnItemDragListener onItemDragListener = new OnItemDragListener() {
        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos){}
        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {}
        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {}
    };

    OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {}
        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {}
        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {}

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float v, float v1, boolean b) {

        }
    };
}
