package stdbay.memorize.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("DrawAllocation")
public class DrawGeometryView extends View {
    private int beginx=0;
    private int beginy=0;
    private int stopx=100;
    private int stopy=100;
    private int offset=0;
    private String word="dd";
    /**
     *
     */
    public DrawGeometryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     *
     */
    public DrawGeometryView(Context context, int beginx, int beginy, int stopx, int stopy) {
        super(context);
        this.beginx=beginx;
        this.beginy=beginy;
        this.stopx=stopx;
        this.stopy=stopy;
    }
//    public int Dp2Px(Context context, float dp) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dp * scale + 0.5f);
//    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint redPaint = new Paint(); // 红色画笔
        redPaint.setAntiAlias(true); // 抗锯齿效果,显得绘图平滑
        redPaint.setColor(Color.WHITE); // 设置画笔颜色
        redPaint.setStrokeWidth(5.0f);// 设置笔触宽度
        redPaint.setStyle(Style.STROKE);// 设置画笔的填充类型
//        redPaint.setTextSize(50);//字体

        Path mPath=new Path();
        mPath.reset();
        //起点
        mPath.moveTo(beginx, beginy);
        //贝塞尔曲线
        mPath.cubicTo((stopx-beginx)/3, beginy, (stopx-beginx)/3, stopy, stopx, stopy);
        //画path
        canvas.drawPath(mPath, redPaint);
    }

}