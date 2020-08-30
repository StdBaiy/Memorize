package stdbay.memorize.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatSeekBar;

public class ThreeSwitchButton extends AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener {
    private SeekTouchListener touchListener;
    private int newProgress=5;
    public ThreeSwitchButton(Context context) {
        super(context);
        setOnSeekBarChangeListener(this);
        setProgress(5);//三挡开关,为了解决thumb显示不全，所以设为5
    }

    public ThreeSwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnSeekBarChangeListener(this);
        setProgress(5);
    }

    public ThreeSwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnSeekBarChangeListener(this);
        setProgress(5);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        newProgress=i;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int thumbLeft=getThumb().getBounds().left;
        int thumbRight=getThumb().getBounds().right;
        int eventX=(int)event.getX();
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(eventX<=thumbLeft||eventX>=thumbRight)return false;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (newProgress < 30) {//如果progress<25,因为左右缩进了5%,所以加了5%,为30%则滑到第一档
            newProgress = 5;
            setProgress(5);
            if (touchListener != null) {
                touchListener.touchTop(seekBar);
            }

        } else if (newProgress >=70) {//如果progress>75,因为左右缩进了5%,所以减了5%,为70%则滑到第三档
            newProgress = 95;
            setProgress(95);
            if (touchListener != null) {
                touchListener.touchEnd(seekBar);
            }

        } else {//到中档
            newProgress = 50;
            setProgress(50);
            if (touchListener != null) {
                touchListener.touchMiddle(seekBar);
            }
        }
    }
    public interface SeekTouchListener {
        void touchTop(SeekBar seekBar);//滑到第一档

        void touchMiddle(SeekBar seekBar);//滑到中档

        void touchEnd(SeekBar seekBar);//滑到第三档

    }
    public void setSeekTouchListenr(SeekTouchListener touchListenr) {
        this.touchListener = touchListenr;
    }

//    public void setOnSwitchListener(OnSwitchListener switchListener){
//        mSwitch=switchListener;
//    }
//
//    public interface OnSwitchListener{
//        public void onSwitch();
//    }
}
