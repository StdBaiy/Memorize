package stdbay.memorize.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.StatusBarUtils;

import stdbay.memorize.R;
import stdbay.memorize.fragment.BookFragment;
import stdbay.memorize.fragment.KnowledgeTreeFragment;
import stdbay.memorize.fragment.MyFragment;

public class BaseActivity extends FragmentActivity implements View.OnClickListener{

    private LinearLayout observe;
    private LinearLayout knowledge;
    private LinearLayout statistics;
    private LinearLayout more;
    public MyFragment  myFragment3, myFragment4;
    public BookFragment bookFragment;
    public KnowledgeTreeFragment knowledgeTreeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        StatusBarUtils.translucent(this);
        setContentView(R.layout.activity_base);

        bindView();
        getWindow().setNavigationBarColor(Color.parseColor("#557755"));

        //默认进入首页
        observe.setSelected(true);
        bookFragment = BookFragment.getInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, bookFragment);
        transaction.commit();

    }

    private void bindView(){
        observe = findViewById(R.id.observe);
        knowledge = findViewById(R.id.knowledge);
        statistics = findViewById(R.id.statistics);
        more = findViewById(R.id.more);
        observe.setOnClickListener(this);
        knowledge.setOnClickListener(this);
        statistics.setOnClickListener(this);
        more.setOnClickListener(this);
    }

    //重置所有文本的选中状态
    public void clearSelected(){
        observe.setSelected(false);
        knowledge.setSelected(false);
        statistics.setSelected(false);
        more.setSelected(false);
    }

    //隐藏所有Fragment
    public void hideAllFragment(FragmentTransaction transaction){
        if(bookFragment != null){
            transaction.hide(bookFragment);
        }
        if(knowledgeTreeFragment != null){
            transaction.hide(knowledgeTreeFragment);
        }
        if(myFragment3 != null){
            transaction.hide(myFragment3);
        }
        if(myFragment4 != null){
            transaction.hide(myFragment4);
        }
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        clearSelected();
        switch (v.getId()){
            case R.id.observe:
                if(bookFragment == null){    // 如果 bookFragment 不存在 就创建一个
                    bookFragment = BookFragment.getInstance();
                    transaction.add(R.id.fragment_container, bookFragment);    // 将碎片添加进R.id.fragment的碎片管理器
                } else {    // 如果存在 就 显示出来  因为点击事件一旦触发 就会将 所有碎片隐藏
                    if(!bookFragment.isHidden() && bookFragment.getNowItem()!=null)
                        bookFragment.onBackPressed();
                    transaction.show(bookFragment);
                }
                observe.setSelected(true);  // 设置 为 选中状态
                break;

            case R.id.knowledge:
                if(knowledgeTreeFragment == null){    // 如果 bookFragment 不存在 就创建一个
                    knowledgeTreeFragment = KnowledgeTreeFragment.getInstance();
                    transaction.add(R.id.fragment_container, knowledgeTreeFragment);    // 将碎片添加进R.id.fragment的碎片管理器
                } else {    // 如果存在 就 显示出来  因为点击事件一旦触发 就会将 所有碎片隐藏
//                    if(knowledgeTreeFragment.isHidden())
                    transaction.show(knowledgeTreeFragment);
                }
                knowledge.setSelected(true);  // 设置 为 选中状态
                break;
            case R.id.statistics:
                statistics.setSelected(true);  // 设置 为 选中状态
                if(myFragment3 == null){
                    myFragment3 = MyFragment.getInstance("发现");
                    transaction.add(R.id.fragment_container, myFragment3);
                } else {
//                    if(myFragment3.isHidden())
                        transaction.show(myFragment3);
                }
                break;
            case R.id.more:
                more.setSelected(true);  // 设置 为 选中状态
                if(myFragment4 == null){
                    myFragment4 = MyFragment.getInstance("我的");
                    transaction.add(R.id.fragment_container, myFragment4);
                } else {
//                    if(myFragment4.isHidden())
                        transaction.show(myFragment4);
                }
                break;
        }
        transaction.commit();
        if(knowledgeTreeFragment!=null)
            knowledgeTreeFragment.clearCookieBar();
    }


    @Override
    public void onBackPressed() {
        if(observe.isSelected())
            bookFragment.onBackPressed();
        else
            finish();
    }


}