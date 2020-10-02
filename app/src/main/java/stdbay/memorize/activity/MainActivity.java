package stdbay.memorize.activity;

//import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.xuexiang.xui.XUI;
import com.xuexiang.xutil.resource.ResUtils;

import stdbay.memorize.R;
import stdbay.memorize.adapter.FlexboxLayoutAdapter;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_flowtaglayout);
        RecyclerView recyclerView4=findViewById(R.id.recycler_view_4);
        recyclerView4.setLayoutManager(getFlexboxLayoutManager(this));
        recyclerView4.setItemAnimator(null);
        FlexboxLayoutAdapter mAdapter4;
        String[] array = ResUtils.getStringArray(R.array.tags_values);
//        recyclerView4.setAdapter(mAdapter4 = new FlexboxLayoutAdapter(array).setIsMultiSelectMode(true));
//        mAdapter4.setCancelable(false);
////        mAdapter4.
//        mAdapter4.multiSelect(1, 2, 3);
//        mAdapter4.setOnItemClickListener((itemView, item, position) -> {
//            mAdapter4.select(position);
////            XToastUtils.toast("选中的内容：" + StringUtils.listToString(mAdapter4.getMultiContent(), ","));
//            Toast.makeText(MainActivity.this, StringUtils.listToString(mAdapter4.getMultiContent(),",") ,Toast.LENGTH_SHORT).show();
//        });

    }

    private FlexboxLayoutManager getFlexboxLayoutManager(Context context) {
        //设置布局管理器
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(context);
        //flexDirection 属性决定主轴的方向（即项目的排列方向）。类似 LinearLayout 的 vertical 和 horizontal:
        // 主轴为水平方向，起点在左端。
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        //flexWrap 默认情况下 Flex 跟 LinearLayout 一样，都是不带换行排列的，但是flexWrap属性可以支持换行排列:
        // 按正常方向换行
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        //justifyContent 属性定义了项目在主轴上的对齐方式:
        // 交叉轴的起点对齐
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        return flexboxLayoutManager;
    }
}
