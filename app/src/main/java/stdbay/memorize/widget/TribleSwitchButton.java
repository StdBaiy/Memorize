package stdbay.memorize.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import stdbay.memorize.R;

public class TribleSwitchButton extends View {
    private onSwitchListener mListener;
    private int oldProgress=35;
    private int nowProgress=35;
    private int innerColor;
    private float startX=0,startY=0,endX=0,endY=0;
    private int direct;
    public TribleSwitchButton(Context context){
        this(context,null);
    }
    public TribleSwitchButton(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public TribleSwitchButton(Context context,AttributeSet attrs,int defStyleAtt){
        super(context,attrs,defStyleAtt);
        TypedArray a=context.getTheme().obtainStyledAttributes(R.styleable.TribleSwitchButton);
        innerColor=a.getColor(R.styleable.TribleSwitchButton_innerColor, Color.parseColor("#555555"));
        a.recycle();
    }
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        paint.setShadowLayer(5,10,10,Color.parseColor("#555555"));
        //这是主滑动槽
        RectF r=new RectF(15f, 5f, 135f, 245f);
        canvas.drawRoundRect(r,10f,10f,paint);
        paint.setColor(Color.GRAY);
        RectF r1=new RectF(55,35,95,215);
        paint.setShadowLayer(0,10,10,Color.parseColor("#555555"));
        canvas.drawRoundRect(r1,10,10,paint);

        //次滑动槽
        paint.setColor(innerColor);
        nowProgress= (int) (oldProgress+(endY-startY));
        if(nowProgress>215)nowProgress=215;
        if(nowProgress<35)nowProgress=35;
        RectF r2=new RectF(55,35,95,nowProgress);
        canvas.drawRoundRect(r2,10,10,paint);
    }

    private int getMySize(int measureSpec,boolean isWidth) {
        int mySize = (isWidth?150:260);

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED://如果没有指定大小，就设置为默认大小
                break;
            case MeasureSpec.EXACTLY: //如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            //如果是固定的大小，那就不要去改变它
        }
        return mySize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMySize(widthMeasureSpec,true);
        int height = getMySize(heightMeasureSpec,false);
        setMeasuredDimension(width, height);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX=event.getX();
                startY=event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                endX=event.getX();
                endY=event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                oldProgress=nowProgress;
                if(oldProgress<95){
                    oldProgress=35;
                    mListener.onSwitchUp();
                }else if(oldProgress>155){
                    oldProgress=215;
                    mListener.onSwitchDown();
                }else{
                    oldProgress=125;
                    mListener.onSwitchMid();
                }
                startY=endY=0;
                invalidate();
                break;
            default:
        }

        return true;
    }

//    private int judgeDirect(float startX,float startY,float endX,float endY){
//        float w=endX-startX;
//        float h=endY-startY;
//        if(Math.abs(w)<Math.abs(h)){
//            if(h>30)return 1;//down
//            else if(h<-30)return -1;//up
//        }
//        return 0;//没有上下操作
//    }

    public void setOnSwitchListener(onSwitchListener listener){
        mListener=listener;
    }

    public interface onSwitchListener{
        public void onSwitchUp();

        public void onSwitchMid();

        public void onSwitchDown();
    }
}
