package com.harine.virgotest.component.color;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author nepalese on 2021/5/11 09:57
 * @usage 色相选择器
 */
public class VirgoColorHView extends View {
    private static final String TAG = "VirgoColorHView";

    private Paint mPaint;
    private int mWidth,mHeight;
    //H表示色相(0-360)，S表示饱和度(0-1)，V表示亮度(0-1)
    private final float[] hsv = new float[3];

    public VirgoColorHView(Context context) {
        this(context, null);
    }

    public VirgoColorHView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoColorHView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int h=0; h<mHeight; h++){
            hsv[0] = h/(mHeight*1f)*360;
            mPaint.setColor(Color.HSVToColor(hsv));
            canvas.drawLine(0, h, mWidth, h, mPaint);
        }
    }
}
