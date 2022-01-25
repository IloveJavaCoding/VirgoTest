package com.harine.virgotest.component.clock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nepalese on 2021/3/19 14:08
 * @usage
 */
public class VirgoElectDotView extends View {
    private static final String TAG = "VirgoElectDotView";

    private static final int minWigth = 50;

    private Paint mPaint;

    private int mWidth, mHeight;
    private int mColor;//点颜色
    private int mBgColor;//背景颜色
    private int mLineL;//边长

    private List<RectF> rectFList = new ArrayList<>();

    public VirgoElectDotView(Context context) {
        this(context, null);
    }

    public VirgoElectDotView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoElectDotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mColor = Color.RED;
        mBgColor = Color.BLACK;

        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getRealSize(widthMeasureSpec);
        mHeight = getRealSize(heightMeasureSpec);
        mLineL = mWidth/5;
        setMeasuredDimension(mWidth, mHeight);
    }

    public int getRealSize(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.UNSPECIFIED) {
            result = minWigth;
        } else {
            result = size;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initRect();
    }

    private void initRect() {
        RectF rectF1 = new RectF((mWidth-mLineL)/2f, mHeight/4f-mLineL/2f,
                (mWidth+mLineL)/2f, mHeight/4f + mLineL/2f);
        rectFList.add(rectF1);

        RectF rectF2 = new RectF((mWidth-mLineL)/2f, mHeight*3/4f-mLineL/2f,
                (mWidth+mLineL)/2f, mHeight*3/4f + mLineL/2f);
        rectFList.add(rectF2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mBgColor);

        for(RectF rectF: rectFList){
            canvas.drawRect(rectF, mPaint);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setmColor(int mColor) {
        this.mPaint.setColor(mColor);
        invalidate();
    }

    public void setmBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
        invalidate();
    }
}