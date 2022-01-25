package com.harine.virgotest.component.color;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.harine.virgotest.bean.VirgoPointf;

/**
 * @author nepalese on 2021/5/11 14:38
 * @usage 圆形选择器
 */
public class VirgoSelectCy extends View {
    private static final String TAG = "VirgoSelectCy";

    private Paint mPaint;
    private VirgoPointf point;
    private PointCallback callback;
    private int mWidth,mHeight;
    private int mRadius;
    private float mS, mV;

    public VirgoSelectCy(Context context) {
        this(context, null);
    }

    public VirgoSelectCy(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoSelectCy(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRadius = 15;
        mS = 1f;
        mV = 1f;

        point = new VirgoPointf();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mWidth = this.getWidth();
        this.mHeight = this.getHeight();
        point.set(mS*mWidth, (1-mV)*mHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                //生成新子
                updatePoint(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }

    private void updatePoint(float x, float y) {
        if(x<0){
            x=0;
        } if(y<0){
            y=0;
        }
        if(x>mWidth){
            x=mWidth;
        }
        if(y>mHeight){
            y=mHeight;
        }
        point.set(x, y);
        invalidate();
        float s = x/mWidth;
        float v = 1-y/mHeight;
        if(s<0){
            s=0;
        }
        if(v<0){
            v=0;
        }
        callback.onUpdateSV(s, v);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawCircle(point.getX(), point.getY(), mRadius, mPaint);
    }

    public interface PointCallback{
        void onUpdateSV(float s, float v);
    }

    //////////////////////////////////////////////////////////
    public void setCallback(PointCallback callback) {
        this.callback = callback;
    }

    public void setSV(float s, float v){
        this.mS = s;
        this.mV = v;
        if(mWidth>0){
            point.set(s*mWidth, (1-v)*mHeight);
        }
    }

    public void setmRadius(int mRadius) {
        this.mRadius = mRadius;
        invalidate();
    }
}
