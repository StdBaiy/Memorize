package stdbay.memorize.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import stdbay.memorize.R;

public class FanBtn extends View{
    private int level=0;//共有0~6挡,对应时间选择
    private int indicatorColor;
    private int radius;
    private double oldArc=0,nowArc=0;
    private boolean onMove;
    private float endX=0,endY=0;
    private int cycle=0;
    private onSwitchListener mListener;
    public FanBtn(Context context){
        this(context,null);
    }
    public FanBtn(Context  context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public FanBtn(Context context, AttributeSet attrs, int defStyleAtt){
        super(context,attrs,defStyleAtt);
        TypedArray a=context.getTheme().obtainStyledAttributes(attrs, R.styleable.FanBtn,defStyleAtt,0);
        level = a.getInt(R.styleable.FanBtn_level, 0);
        indicatorColor=a.getColor(R.styleable.FanBtn_indicatorColor,Color.parseColor("#555555"));
        radius=(int)a.getDimension(R.styleable.FanBtn_radius,
           TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP,50,getResources().getDisplayMetrics()));
        a.recycle();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        int center=getWidth()/2;
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setShadowLayer(5,10,10,Color.parseColor("#555555"));
        canvas.drawCircle(center,center,radius, paint);
        paint.setShadowLayer(35,10,20,Color.parseColor("#aaaaaa"));
        canvas.drawCircle(center,center,radius-40,paint);
        paint.setShadowLayer(0,10,10,Color.rgb(5,5,5));
        int margin=10*getResources().getDisplayMetrics().densityDpi/160;
        paint.setStrokeWidth(8);
        RectF oval=new RectF(center-radius-margin,center-radius-margin,
                center+radius+margin,center+radius+margin);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(indicatorColor);

        canvas.save();
        if(onMove){
            nowArc=calAngle(endX,endY,center);
        }
        if (Math.abs(nowArc - oldArc) <= Math.PI / 4) {
            oldArc = nowArc;
        } else {
            nowArc = oldArc;
        }
        canvas.translate(center, center);
        canvas.rotate((float) (nowArc/Math.PI*180));
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        if(onMove){
            RectF r3=new RectF(-13,-radius+margin+30,13,-radius+margin+50);
//            canvas.drawCircle(0, -radius+margin, 10, paint);
            canvas.drawRoundRect(r3,10,10,paint);
        }
        else canvas.drawCircle(0, -radius+margin+40, 10, paint);
        canvas.restore();
//        canvas.drawLine(pointX,pointY,center,center,paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#aaaaaa"));
        paint.setStrokeWidth(8);
        canvas.drawArc(oval,0,360,false,paint);
        paint.setColor(Color.parseColor("#555555"));
        canvas.drawArc(oval, -90, (float) (nowArc * 180 / Math.PI), false, paint);
    }

    private int getMySize(int measureSpec) {
        int mySize = 350;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED://如果没有指定大小，就设置为默认大小
                mySize = 350;
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
        int width = getMySize(widthMeasureSpec);
        int height = getMySize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private double calAngle(double x,double y,double c){//给出
        double a2=Math.pow(x-c,2)+Math.pow(y,2);
        double b2=c*c;
        double c2=Math.pow(x-c,2)+Math.pow(y-c,2);
        double cosA=(b2+c2-a2)/(2*Math.sqrt(b2*c2));
        return (x>=c)?( Math.acos(cosA)):(2*Math.PI-Math.acos(cosA));
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                onMove=true;
                endX=event.getX();
                endY=event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                onMove=false;
                Log.d("角度",""+nowArc/Math.PI*60);
                switch ((int) (nowArc/Math.PI*6)){
                    case 0:
                        oldArc=nowArc=0;
                        mListener.ON_1();
                        break;
                    case 1:
                    case 2:
                        oldArc=nowArc=Math.PI/3;
                        mListener.ON_2();
                        break;
                    case 3:
                    case 4:
                        oldArc=nowArc=Math.PI*2/3;
                        mListener.ON_3();
                        break;
                    case 5:
                    case 6:
                        oldArc=nowArc=Math.PI;
                        mListener.ON_4();
                        break;
                    case 7:
                    case 8:
                        oldArc=nowArc=Math.PI*4/3;
                        mListener.ON_5();
                        break;
                    case 9:
                    case 10:
                        oldArc=nowArc=Math.PI*5/3;
                        mListener.ON_6();
                        break;
                    case 11:
                        oldArc=nowArc=Math.PI*2;
                        mListener.ON_7();
                        break;
                    default:
                }
                invalidate();
                break;
            default:
        }
        return true;
    }
    
    int getLevel(){
        return this.level;
    }

    public void setOnSwitchListener(onSwitchListener listener){
        mListener=listener;
    }

    public interface onSwitchListener{
        public void ON_1();
        public void ON_2();
        public void ON_3();
        public void ON_4();
        public void ON_5();
        public void ON_6();
        public void ON_7();
    }

//    // 为每一个接口设置监听器
//    public void setOnDownActionListener(OnDownActionListener down) {
//        mDown = down;
//    }
//
//
//    // 定义三个接口
//    public interface OnDownActionListener {
//        public void OnDown();
//    }


}
