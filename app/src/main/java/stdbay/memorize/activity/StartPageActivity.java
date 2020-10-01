package stdbay.memorize.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import stdbay.memorize.R;

public class StartPageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_start_page);
        Thread myThread = new Thread() {//创建子线程
            @Override
            public void run() {
                try {
                    sleep(300);//使程序休眠
                    Intent it = new Intent(getApplicationContext(), BaseActivity.class);
                    startActivity(it);
                    finish();//关闭当前活动
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();//启动线程

    }
}
