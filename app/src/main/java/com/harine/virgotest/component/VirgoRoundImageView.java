package com.harine.virgotest.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.harine.virgotest.R;

/**
 * @author nepalese on 2020/12/3 14:23
 * @usage
 */
public class VirgoRoundImageView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = "VirgoRoundImageView";

    private Paint mPaintRound;//画四角
    private Paint mPaintImage;//画图
    private int roundWidth;//圆角宽
    private int roundHeight;//圆角高

    public VirgoRoundImageView(Context context) {
        this(context, null);
    }

    public VirgoRoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoRoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VirgoRoundImageView);
        roundWidth = typedArray.getDimensionPixelSize(R.styleable.VirgoRoundImageView_vroundWidth, 20);
        roundHeight = typedArray.getDimensionPixelSize(R.styleable.VirgoRoundImageView_vroundHeight, 20);
        typedArray.recycle();

        mPaintRound = new Paint();
        mPaintRound.setColor(Color.WHITE);
        mPaintRound.setAntiAlias(true);
        mPaintRound.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        mPaintImage = new Paint();
        mPaintImage.setXfermode(null);
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap);
        super.draw(canvas2);
        drawLiftUp(canvas2);
        drawLiftDown(canvas2);
        drawRightUp(canvas2);
        drawRightDown(canvas2);
        canvas.drawBitmap(bitmap, 0, 0, mPaintImage);
        bitmap.recycle();
    }

    private void drawLiftUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, roundHeight);
        path.lineTo(0, 0);
        path.lineTo(roundWidth, 0);
        path.arcTo(new RectF(0, 0, roundWidth * 2, roundHeight * 2), -90, -90);
        path.close();
        canvas.drawPath(path, mPaintRound);
    }

    private void drawLiftDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, getHeight() - roundHeight);
        path.lineTo(0, getHeight());
        path.lineTo(roundWidth, getHeight());
        path.arcTo(new RectF(0, getHeight() - roundHeight * 2, roundWidth * 2, getHeight()), 90, 90);
        path.close();
        canvas.drawPath(path, mPaintRound);
    }

    private void drawRightDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth() - roundWidth, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight() - roundHeight);
        path.arcTo(new RectF(getWidth() - roundWidth * 2, getHeight() - roundHeight * 2, getWidth(), getHeight()), -0, 90);
        path.close();
        canvas.drawPath(path, mPaintRound);
    }

    private void drawRightUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth(), roundHeight);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth() - roundWidth, 0);
        path.arcTo(new RectF(getWidth() - roundWidth * 2, 0, getWidth(), roundHeight * 2), -90, 90);
        path.close();
        canvas.drawPath(path, mPaintRound);
    }
}