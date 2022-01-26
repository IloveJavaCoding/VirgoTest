package com.harine.virgotest.component;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

/**
 * @author nepalese on 2021/3/2 15:27
 * @usage
 */
public class VirgoWaveView extends View {
    private static final String TAG = "VirgoWaveView";

    private Paint paintLine;//线
    private ValueAnimator mAnimator;

    private int mWidth, mHeight;//宽高
    private int mDuration;//动画间隔
    private int mWaveWidth;//波长
    private int mWaveNum;//波数
    private int mMaxHeight;//波峰
    private int mBaseLine;//波中心线
    private float mOffset;//偏移
    
    public VirgoWaveView(Context context) {
        this(context,null);
    }

    public VirgoWaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mWaveNum = 5;
        mMaxHeight = 100;
        mDuration = 1000;
        mOffset = 0;
        
        paintLine = new Paint();
        paintLine.setColor(Color.RED);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(6);
        paintLine.setAntiAlias(true);
    }

    private void setAnimator(){
        //设置一个波长的偏移
        mAnimator = ValueAnimator.ofFloat(0,mWaveWidth);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (float)animation.getAnimatedValue();//不断的设置偏移量，并重画
                postInvalidate();
            }
        });
        mAnimator.setDuration(mDuration);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.start();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        
        mWaveWidth = mWidth/mWaveNum*2;
        mBaseLine = mHeight/2;
        setAnimator();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(getPath(), paintLine);
    }

    private Path getPath() {
        int itemWidth = mWaveWidth/2;//半个波长
        Path mPath = new Path();
        mPath.moveTo(-itemWidth, mBaseLine);//起始坐标

        for (int i = -3; i < mWaveNum; i++) {
            int startX = i * itemWidth;
            mPath.quadTo(startX + itemWidth/2f + mOffset, getWaveHeigh(i),
                    startX + itemWidth + mOffset, mBaseLine);
        }

        mOffset += itemWidth/2;
        mPath.moveTo(-itemWidth*2,  mBaseLine);//起始坐标
        for (int i = -5; i < mWaveNum; i++) {
            int startX = i * itemWidth;
            mPath.quadTo(startX + itemWidth/2f + mOffset, getWaveHeigh(i),
                    startX + itemWidth + mOffset, mBaseLine);
        }

        mOffset += itemWidth/2;
        mPath.moveTo(-itemWidth,  mBaseLine);//起始坐标
        for (int i = -7; i < mWaveNum; i++) {
            int startX = i * itemWidth;
            mPath.quadTo(startX + itemWidth/2f + mOffset, getWaveHeigh(i),
                    startX + itemWidth + mOffset, mBaseLine);
        }

        return  mPath;
    }

    //奇数峰值是正的，偶数峰值是负数
    private int getWaveHeigh(int num){
        if(num % 2 == 0){
            return mBaseLine + mMaxHeight;
        }
        return mBaseLine - mMaxHeight;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAnimator.end();
    }
}
