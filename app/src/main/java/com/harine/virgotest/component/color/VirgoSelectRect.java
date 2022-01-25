package com.harine.virgotest.component.color;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author nepalese on 2021/5/11 17:52
 * @usage 矩形选择器
 */
public class VirgoSelectRect extends View {
    private static final String TAG = "VirgoSelectCy";

    private Paint mPaint;
    private RectF rectF;
    private RectCallback callback;
    private int mWidth,mHeight;
    private int mMaxProgress;//最大进度值
    private int mRH;//rect 高
    private float mProgress;//进度值
    private float mRXY;//圆角半径

    public VirgoSelectRect(Context context) {
        this(context, null);
    }

    public VirgoSelectRect(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoSelectRect(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRH = 20;
        mRXY = 5;
        mProgress = 0;
        mMaxProgress = 100;

        rectF = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mWidth = this.getWidth();
        this.mHeight = this.getHeight() - mRH;//保留底部
        float t = mProgress/mMaxProgress*mHeight;
        rectF.set(0, t, mWidth, t+mRH);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                //生成新子
                updateRect(event.getY());
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }

    private void updateRect(float y) {
        if(y<0){
            y=0;
        }
        if(y>mHeight){
            y=mHeight;
        }

        rectF.set(0, y, mWidth, y+mRH);
        invalidate();

        mProgress = y / mHeight * mMaxProgress;
        callback.onProgress(mProgress);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawRoundRect(rectF, mRXY, mRXY, mPaint);
    }

    public interface RectCallback{
        void onProgress(float progress);
    }

    //////////////////////////////////////////////////////////
    public void setCallback(RectCallback callback) {
        this.callback = callback;
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        if(mHeight>0){
            float top = progress/mMaxProgress*mHeight;
            rectF.set(0, top, mWidth, top + mRH);
            invalidate();
        }
    }

    public void setmMaxProgress(int mMaxProgress) {
        this.mMaxProgress = mMaxProgress;
    }

    public void setmRH(int mRH) {
        this.mRH = mRH;
        invalidate();
    }
}
