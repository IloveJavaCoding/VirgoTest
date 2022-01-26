package com.harine.virgotest.component.color;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * @author nepalese on 2021/5/11 14:03
 * @usage
 */
public class VirgoColorAView extends View {
    private static final String TAG = "VirgoColorAView";

    private Paint mPaint;
    private RectF rectF;
    private int mHeight;
    private final int[] mColors = new int[2];

    public VirgoColorAView(Context context) {
        this(context, null);
    }

    public VirgoColorAView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoColorAView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mColors[0] = Color.parseColor("#00000000");
        mColors[1] = Color.parseColor("#ffff0000");

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    private void setShader(){
        //渐变渲染
        Shader mShader = new LinearGradient(0, mHeight, 0, 0, mColors, null, Shader.TileMode.CLAMP);
        mPaint.setShader(mShader);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //在measure之后， layout之前
        rectF = new RectF(0, 0, w, h);
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setShader();
        canvas.drawRect(rectF, mPaint);
    }

    /////////////////////////////////////////////////
    public void setColor(@ColorInt int color){
        mColors[1] = color;
        invalidate();
    }

    public void setmColorH(float h) {
        if(h>360 || h<0){
            return;
        }
        float[] hsv = new float[3];
        hsv[0] = h;
        hsv[1] = 1f;
        hsv[2] = 1f;
        setColor(Color.HSVToColor(hsv));
    }
}
