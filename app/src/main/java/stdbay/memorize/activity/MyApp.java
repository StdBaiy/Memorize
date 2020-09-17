package stdbay.memorize.activity;

import android.app.Application;
import android.content.Context;

import com.xuexiang.xui.XUI;

public class MyApp extends Application {
    private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        XUI.init(this);
        XUI.debug(true);
        mContext=getApplicationContext();
    }
}
