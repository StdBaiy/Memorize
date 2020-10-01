package stdbay.memorize.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class OpenActivity extends AppCompatActivity {


    public final static String KEY_IS_DISPLAY = "key_is_display";
    public final static String KEY_ENABLE_ALPHA_ANIM = "key_enable_alpha_anim";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_open);
        Intent i = new Intent();
        i.putExtra(KEY_IS_DISPLAY, true);
        i.setClass(this, SplashActivity.class);
        i.putExtra(KEY_ENABLE_ALPHA_ANIM, true);
        startActivity(i);
    }
}
