package stdbay.memorize.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.utils.StatusBarUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import stdbay.memorize.R;
import stdbay.memorize.fragment.BookFragment;
import stdbay.memorize.fragment.KnowledgeTreeFragment;
import stdbay.memorize.fragment.MyFragment;
import stdbay.memorize.util.MessageEvent;

public class BaseActivity extends FragmentActivity implements View.OnClickListener{

    public final static String KEY_IS_DISPLAY = "key_is_display";
    public final static String KEY_ENABLE_ALPHA_ANIM = "key_enable_alpha_anim";

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

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        checkPermission();
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
        if(knowledgeTreeFragment!=null && knowledgeTreeFragment.isSelectMode){
            SnackbarUtils.Short(observe,"请先完成选择操作")
                    .warning().show();
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        clearSelected();
        switch (v.getId()){
            case R.id.observe:
                if(bookFragment == null){    // 如果 bookFragment 不存在 就创建一个
                    bookFragment = BookFragment.getInstance();
                    transaction.add(R.id.fragment_container, bookFragment);    // 将碎片添加进R.id.fragment的碎片管理器
                } else {    // 如果存在 就 显示出来  因为点击事件一旦触发 就会将 所有碎片隐藏
                    if(!bookFragment.isHidden())
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
                if(myFragment3 == null){
                    myFragment3 = MyFragment.getInstance("发现");
                    transaction.add(R.id.fragment_container, myFragment3);
                } else {
//                    if(myFragment3.isHidden())
                    transaction.show(myFragment3);
                }
                statistics.setSelected(true);  // 设置 为 选中状态
                break;
            case R.id.more:
                if(myFragment4 == null){
                    myFragment4 = MyFragment.getInstance("我的");
                    transaction.add(R.id.fragment_container, myFragment4);
                } else {
//                    if(myFragment4.isHidden())
                    transaction.show(myFragment4);
                }
                more.setSelected(true);  // 设置 为 选中状态
                break;
        }
        transaction.commit();
        if(knowledgeTreeFragment!=null)
            knowledgeTreeFragment.clearCookieBar();
    }


    @Override
    public void onBackPressed() {
        if(observe.isSelected()&&bookFragment.getNowItem()!=null)
            bookFragment.onBackPressed();
        else
            finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bookFragment.onActivityResult(requestCode,resultCode,data);
    }

    public void checkPermission() {
        boolean isGranted = true;
        if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED||
                this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            this.requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                            .ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    102);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        if(event.getType()==MessageEvent.SELECT_KNOWLEDGE){
            knowledge.callOnClick();
            knowledgeTreeFragment.isSelectMode=true;
        }
        if(event.getType()==MessageEvent.KNOWLEDGE_RETURN){
            knowledgeTreeFragment.isSelectMode=false;
            observe.callOnClick();
            bookFragment.updateKnowledgeItems();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}