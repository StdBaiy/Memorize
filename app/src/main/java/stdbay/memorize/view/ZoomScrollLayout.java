package stdbay.memorize.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

/**
 * Created by wg on 2017/4/21.
 */

public class ZoomScrollLayout extends RelativeLayout implements ScaleGestureDetector.OnScaleGestureListener {
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 2.0f;

    private int mLeft, mTop, mRight, mBottom;
    private float mLastScale = 1.0f;
    private float totleScale = 1.0f;

    // childview
    private View mChildView;

    // 拦截滑动事件
    float mDistansX, mDistansY, mTouchSlop;

    private enum MODE {
        ZOOM, DRAG, NONE
    }

    private MODE mode;

    public ZoomScrollLayout(Context context) {
        super(context);
        init(context);
    }

    public ZoomScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZoomScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mChildView = getChildAt(0);
        if(mChildView!=null)
            mChildView.layout(0, 0, mChildView.getMeasuredWidth(), mChildView.getMeasuredHeight());
    }

    public void init(Context context) {
        mScaleDetector = new ScaleGestureDetector(context, this);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mode == MODE.DRAG) {
                    if (mChildView == null) {
                        mChildView = getChildAt(0);

                    }
                    mLeft = mChildView.getLeft();
                    mTop = mChildView.getTop();
                    mRight = mChildView.getRight();
                    mBottom = mChildView.getBottom();

                    int newL = mLeft - (int) distanceX;
                    int newT = mTop - (int) distanceY;
                    int newR = mRight - (int) distanceX;
                    int newB = mBottom - (int) distanceY;
                    mChildView.layout(newL, newT, newR, newB);
                }
                return true;
            }
        });

        // 系统最小滑动距离
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int action = e.getActionMasked();
        int currentX = (int) e.getX();
        int currentY = (int) e.getY();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                //记录上次滑动的位置
                mDistansX = currentX;
                mDistansY = currentY;

                //将当前的坐标保存为起始点
                mode = MODE.DRAG;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(mDistansX - currentX) >= mTouchSlop || Math.abs(mDistansY - currentY) >= mTouchSlop){//父容器拦截
                    return true;
                }
                break;
            //指点杆保持按下，并且进行位移
            //有手指抬起，将模式设为NONE
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = MODE.NONE;
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        if (mode == MODE.ZOOM) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            float tempScale = mLastScale * scaleFactor;
            if (tempScale <= MAX_ZOOM && tempScale >= MIN_ZOOM) {
                totleScale = tempScale;
                applyScale(totleScale);
            }
        }
        return false;
    }

    /**
     * 执行缩放操作
     */
    public void applyScale(float scale) {
        mChildView.setScaleX(scale);
        mChildView.setScaleY(scale);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        mode = MODE.ZOOM;
        if (mChildView == null) {
            mChildView = getChildAt(0);

        }

        mLeft = mChildView.getLeft();
        mTop = mChildView.getTop();
        mRight = mChildView.getRight();
        mBottom = mChildView.getBottom();

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        mLastScale = totleScale;
    }

}