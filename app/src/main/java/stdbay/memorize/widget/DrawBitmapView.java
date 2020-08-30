package stdbay.memorize.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import stdbay.memorize.R;


public class DrawBitmapView extends View {
    public DrawBitmapView(Context context){
//        super(context);
        this(context,null);
    }
    public DrawBitmapView(Context  context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public DrawBitmapView(Context context, AttributeSet attrs, int defStyleAtt) {
        super(context,attrs,defStyleAtt);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3);
//        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        Bitmap dstbmp=Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight());
//        canvas.drawBitmap(dstbmp, 100, 100, paint);
        Bitmap bm= BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_round);
        canvas.drawBitmap(bm,100,100,paint);
    }
}