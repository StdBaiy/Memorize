package stdbay.memorize.activity;

import android.app.Activity;
import android.view.KeyEvent;

import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xutil.app.ActivityUtils;
//
//import com.xuexiang.xuidemo.utils.TokenUtils;
//import com.xuexiang.xutil.app.ActivityUtils;

import stdbay.memorize.R;
import stdbay.memorize.util.SettingSPUtils;

/**
 * 渐近式启动页
 *
 * @author xuexiang
 * @since 2018/11/27 下午5:24
 */
public class SplashActivity extends BaseSplashActivity {

    public final static String KEY_IS_DISPLAY = "key_is_display";
    public final static String KEY_ENABLE_ALPHA_ANIM = "key_enable_alpha_anim";

    private boolean isDisplay = false;

    @Override
    protected long getSplashDurationMillis() {
        return 500;
    }

    @Override
    public void onCreateActivity() {
        isDisplay = getIntent().getBooleanExtra(KEY_IS_DISPLAY, isDisplay);
        boolean enableAlphaAnim = getIntent().getBooleanExtra(KEY_ENABLE_ALPHA_ANIM, false);
//        SettingSPUtils spUtil = SettingSPUtils.getInstance();
//        if (spUtil.isFirstOpen()) {
//            spUtil.setIsFirstOpen(false);
//
//            finish();
//
//        } else {
            if (enableAlphaAnim) {
                initSplashView(R.drawable.banner_bg);
            } else {
                initSplashView(R.drawable.xui_config_bg_splash);
            }
            startSplash(enableAlphaAnim);
//        }
    }

    @Override
    public void onSplashFinished() {
//        if (!isDisplay) {
//            if (TokenUtils.hasToken()) {
//                ActivityUtils.startActivity(MainActivity.class);
//            } else {
//                ActivityUtils.startActivity(LoginActivity.class);
//            }
//        }
        ActivityUtils.startActivity(BaseActivity.class);
        finish();
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return KeyboardUtils.onDisableBackKeyDown(keyCode) && super.onKeyDown(keyCode, event);
    }

}
