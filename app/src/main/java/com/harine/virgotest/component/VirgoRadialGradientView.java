package com.harine.virgotest.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author nepalese on 2021/3/23 17:18
 * @usage
 */
public class VirgoRadialGradientView extends View {
    private static final String TAG = "VirgoRadialGradientView";

    private Paint mPaint1;//画笔1
    private Paint mPaint2;//画笔2
    private Shader mShader1;//渐变渲染
    private Shader mShader2;//渐变渲染

    private int mWidth, mHeight;//宽高
    private float mRadius1, mRadius2;
    private int[] mColor1, mColor2;
    private int mBgColor;

    public VirgoRadialGradientView(Context context) {
        this(context, null);
    }

    public VirgoRadialGradientView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoRadialGradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint1 = new Paint();
        mPaint1.setAntiAlias(true);
        mPaint1.setDither(true);
        mPaint1.setStyle(Paint.Style.FILL);

        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setDither(true);
        mPaint2.setStyle(Paint.Style.FILL);

        //初始化默认颜色
        initColor();
    }

    private void initColor() {
        mBgColor = Color.argb(255, 0,0,0);
        mColor1 = new int[]{Color.parseColor("#a8edea"), Color.parseColor("#fed6e3"), Color.parseColor("#86fde8")};
        mColor2 = new int[]{Color.parseColor("#acb6e5"), Color.parseColor("#86fde8")};
    }

    private void setShader(){
        mRadius1 = mWidth;
        mRadius2 = mWidth/2f;
        mShader1 = new RadialGradient(mWidth, mHeight/10f, mRadius1, mColor1, null, Shader.TileMode.CLAMP);
        mShader2 = new RadialGradient(mWidth/2f, mHeight/2f, mRadius2, mColor2, null, Shader.TileMode.CLAMP);

        mPaint1.setShader(mShader1);
        mPaint2.setShader(mShader2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //在measure之后， layout之前
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setShader();

//        canvas.drawColor(mBgColor);

//        canvas.drawPath(mPath, mPaint1);
        canvas.drawRect(new RectF(0, 0, mWidth, mHeight), mPaint1);
//        canvas.drawRect(new RectF(0, 0, mWidth/2, mHeight/2), mPaint2);
    }
}