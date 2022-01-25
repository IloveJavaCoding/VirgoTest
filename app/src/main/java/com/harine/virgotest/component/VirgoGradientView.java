package com.harine.virgotest.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.harine.virgotest.R;

/**
 * @author nepalese on 2021/3/11 14:24
 * @usage 多色渐变调板：支持圆角模式，支持线性、扫描和环形渐变，线性渐变支持0-180角度设置；
 */
public class VirgoGradientView extends View {
    private static final String TAG = "VirgoGradientView";

    public static final int MODE_ROUND = 1;//圆角模式
    public static final int MODE_RECT= 2;//矩形模式（默认）

    public static final int GRADIENT_LINEAR = 1;//线性渐变
    public static final int GRADIENT_SWEEP = 2;//扫描渐变
    public static final int GRADIENT_RADIAL = 3;//环形渐变

    private Paint mPaint;//画笔
    private RectF mRectF;//画布矩形
    private Shader mShader;//渐变渲染

    private int mWidth, mHeight;//宽高
    private int mMode;//图形模式
    private int mGradientMode;//渐变模式
    private int mAngle;//线性渐变角度(从左->右：0-180）
    private int mRoundRadius;//圆角半径（圆角模式）
    private int[] mColors;//颜色组

    public VirgoGradientView(Context context) {
        this(context, null);
    }

    public VirgoGradientView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoGradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        String strColors;//颜色按顺序，以空格间开
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.VirgoGradientView);
        mMode = typedArray.getInt(R.styleable.VirgoGradientView_vgMode, MODE_RECT);
        mAngle = typedArray.getInt(R.styleable.VirgoGradientView_vgLinearAngle, 0);
        mGradientMode = typedArray.getInt(R.styleable.VirgoGradientView_vgGradientMode, GRADIENT_LINEAR);
        mRoundRadius = typedArray.getDimensionPixelSize(R.styleable.VirgoGradientView_vgRoundRadious, 20);
        strColors = typedArray.getString(R.styleable.VirgoGradientView_vgColors);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);

        //初始化默认颜色
        initColor(strColors);
    }

    private void initColor(String strColors) {
        if(strColors==null){
            strColors = "#69EACB #EACCF8 #6654F1";//默认
        }
        String[] colors = strColors.split(" ");
        mColors = new int[colors.length];
        for(int i=0; i<colors.length; i++){
            mColors[i] = Color.parseColor(colors[i]);
        }

//        //彩虹色
//        mColors = new int[7];
//        mColors[0] = Color.rgb(255,0,0);
//        mColors[1] = Color.rgb(255,165,0);
//        mColors[2] = Color.rgb(255,255,0);
//        mColors[3] = Color.rgb(0,255,0);
//        mColors[4] = Color.rgb(0,127,255);
//        mColors[5] = Color.rgb(0,0,255);
//        mColors[6] = Color.rgb(139,0,255);
    }

    private void setShader(){
        switch (mGradientMode){
            case GRADIENT_LINEAR:
                //x0,y0,x1,y1是起始位置和渐变的结束位置
                //positions指定颜色数组的相对位置: [0…1], 如果传null，渐变就线性变化
                //角度正切值
                double tan = Math.tan(Math.PI*mAngle/180);
                if(45>mAngle && mAngle>=0){
                    //[0,45)
                    mShader = new LinearGradient(0,(float) ((mHeight - tan*mWidth)/2), mWidth, (float) ((mHeight + tan*mWidth)/2), mColors, null, Shader.TileMode.CLAMP);
                }else if(135>=mAngle && mAngle>=45){
                    //[45,135]
                    if(mAngle==90){
                        mShader = new LinearGradient(mWidth/2f,0, mWidth/2f, mHeight, mColors, null, Shader.TileMode.CLAMP);
                    }else{
                        mShader = new LinearGradient((float) ((mWidth - mHeight/tan)/2), 0, (float) ((mWidth + mHeight/tan)/2), mHeight, mColors, null, Shader.TileMode.CLAMP);
                    }
                }else if(180>=mAngle && mAngle>135){
                    mShader = new LinearGradient(mWidth, (float) ((mHeight + tan*mWidth)/2), 0, (float) ((mHeight - tan*mWidth)/2), mColors, null, Shader.TileMode.CLAMP);
                }else{
                    //默认左 -> 右
                    mShader = new LinearGradient(0,0, mWidth, 0, mColors, null, Shader.TileMode.CLAMP);
                }
                break;
            case GRADIENT_SWEEP:
                //cx,cy,圆的中心坐标
                mShader = new SweepGradient(mWidth/2f, mHeight/2f, mColors,null);
                break;
            case GRADIENT_RADIAL:
                //cx,cy,中心坐标
                //radius Must be positive. The radius of the circle for this gradient.
                int max = Math.max(mWidth, mHeight);
                mShader = new RadialGradient(mWidth/2f, mHeight/2f, max/2f, mColors, null, Shader.TileMode.CLAMP);
                break;
        }

        mPaint.setShader(mShader);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //在measure之后， layout之前
        mRectF = new RectF(0, 0, w, h);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setShader();

        if(mMode==MODE_RECT){
            //矩形
            canvas.drawRect(mRectF, mPaint);
        }else{
            //圆角
            canvas.drawRoundRect(mRectF, mRoundRadius, mRoundRadius, mPaint);
        }
    }

    //////////////////////////////////////api//////////////////////////////////
    public void setmMode(int mMode) {
        this.mMode = mMode;
    }

    public void setmGradientMode(int mGradientMode) {
        this.mGradientMode = mGradientMode;
    }

    public void setmAngle(int mAngle) {
        this.mAngle = mAngle%180;
    }

    public void setmRoundRadius(int mRoundRadius) {
        this.mRoundRadius = mRoundRadius;
    }

    //颜色种类不少于2
    public void setmColors(int[] mColors) {
        if(mColors.length<2){
            return;
        }
        this.mColors = mColors;
    }
}
