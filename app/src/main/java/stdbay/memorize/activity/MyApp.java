package stdbay.memorize.activity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.xuexiang.xui.XUI;

public class MyApp extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        XUI.init(this);
        XUI.debug(true);
        mContext=getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
