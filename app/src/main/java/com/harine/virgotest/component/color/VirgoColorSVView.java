package com.harine.virgotest.component.color;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * @author nepalese on 2021/5/11 08:57
 * @usage 颜色面板
 */
public class VirgoColorSVView extends View {
    private static final String TAG = "VirgoColorView";

    private Paint mPaint;
    private int mWidth,mHeight;

    //H表示色相(0-360)，S表示饱和度(0-1)，V表示亮度(0-1)
    private final float[] hsv = new float[3];
    private final int[] mColors = new int[2];//颜色组

    public VirgoColorSVView(Context context) {
        this(context, null);
    }

    public VirgoColorSVView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoColorSVView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Color.colorToHSV(Color.RED, hsv);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mWidth = this.getWidth();
        this.mHeight = this.getHeight();
    }

    private void setShader(){
        //渐变渲染
        Shader mShader = new LinearGradient(0, 0, mWidth, 0, mColors, null, Shader.TileMode.CLAMP);
        mPaint.setShader(mShader);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int h = 0; h < mHeight; h++) {
            hsv[2] = (mHeight - h) / (mHeight * 1f);

            mColors[0] = getC1();
            mColors[1] = getC2();
            setShader();
            canvas.drawLine(0, h, mWidth, h, mPaint);
        }
    }

    private int getC1(){
        hsv[1] = 0;
        return  Color.HSVToColor(hsv);
    }

    private int getC2(){
        hsv[1] = 1;
        return  Color.HSVToColor(hsv);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 设置色相
     * @param h; 0-360
     */
    public void setmColorH(float h) {
        if(h>360 || h<0){
            return;
        }
        hsv[0] = h;
        invalidate();
    }

    /**
     * 设置颜色
     * @param color
     */
    public void setColor(@ColorInt int color){
        Color.colorToHSV(color, hsv);
        invalidate();
    }
}
