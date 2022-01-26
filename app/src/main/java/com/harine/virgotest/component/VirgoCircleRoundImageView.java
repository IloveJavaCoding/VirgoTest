package com.harine.virgotest.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.harine.virgotest.R;

/**
 * @author nepalese on 2021/3/2 10:55
 * @usage 自定义圆形、圆角图片
 */
public class VirgoCircleRoundImageView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = "VirgoImageView";

    public static final int ROTATE_DEGREE = 30;//边框旋转偏移角度
    public static final int MODE_ROUND = 1;//圆角模式
    public static final int MODE_CIRCLE = 2;//圆形模式（默认）

    private Paint mPaint;//位图画笔
    private Paint mBorderPaint;//边框画笔

    private BitmapShader mShader;//位图渲染效果
    private Shader mSweepGradient;//边框渐变渲染
    private Matrix mMatrix;//用于记录图片缩放规则
    private RectF mRoundRectF;//圆角矩形

    private int mWidth, mHeight;//宽高
    private int mMode;//选择的模式
    private int mRoundRadius;//圆角半径
    private int mCircleRadius;//圆形半径
    private int mBorderWidth;//边框宽度

    private int mBorderStartColor;//边框渐变起始颜色
    private int mBorderEndColor;//边框渐变末端颜色
    private boolean mAddBorder;//圆形模式下是否添加边框(默认不添加)

    public VirgoCircleRoundImageView(@NonNull Context context) {
        this(context, null);
    }

    public VirgoCircleRoundImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoCircleRoundImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        Log.i(TAG, "init: ");
        //自定义属性
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.VirgoCircleRoundImageView);
        mMode = typedArray.getInt(R.styleable.VirgoCircleRoundImageView_vimgMode, MODE_CIRCLE);
        mRoundRadius = typedArray.getDimensionPixelSize(R.styleable.VirgoCircleRoundImageView_vimgRoundRadious, 20);
        mAddBorder = typedArray.getBoolean(R.styleable.VirgoCircleRoundImageView_vimgAddBorder, false);
        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.VirgoCircleRoundImageView_vimgBorderWidth, 4);
        mBorderStartColor = typedArray.getColor(R.styleable.VirgoCircleRoundImageView_vimgBorderStartColor, Color.WHITE);
        mBorderEndColor = typedArray.getColor(R.styleable.VirgoCircleRoundImageView_vimgBorderEndColor, Color.rgb(102,204, 255));
        typedArray.recycle();

        //初始化画笔
        mMatrix = new Matrix();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    private void setShader(){
        Log.i(TAG, "setShader: ");
        /*
         * CLAMP 拉伸
         * REPEAT 重复
         * MIRROR 镜像
         */
        //位图，存放图片源
        Bitmap mBitmap = drawableToBitamp(getDrawable());
        mShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale;
        if(mMode==MODE_ROUND){
            /*
             * 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；
             * 缩放后的图片的宽高，一定要大于我们view的宽高；
             * 所以我们这里取大值；
             */
            scale = Math.max(mWidth*1.0f/ mBitmap.getWidth(), mHeight*1.0f/ mBitmap.getHeight());
            mRoundRectF = new RectF(0, 0, mWidth, mHeight);
        }else{//默认 MODE_CIRCLE
            //宽高取最小值作为新的边长
            int min = Math.min(mBitmap.getWidth(), mBitmap.getHeight());

            if(mAddBorder){
                mSweepGradient = new SweepGradient(mCircleRadius,mCircleRadius,
                        new int[] { mBorderStartColor, mBorderEndColor },null);
                mBorderPaint.setShader(mSweepGradient);

                scale = (mWidth-mBorderWidth) * 1.0f / min;
            }else{
                scale = mWidth * 1.0f / min;
            }
        }

        //保证原图宽高比不变
        mMatrix.setScale(scale, scale);
        mShader.setLocalMatrix(mMatrix);
        mPaint.setShader(mShader);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //如果类型是圆形，则强制改变view的宽高一致，以小值为准
        if(mMode==MODE_CIRCLE){
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mCircleRadius = mWidth/2;
            setMeasuredDimension(mWidth, mWidth);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mMode == MODE_ROUND){
            mWidth = getWidth();
            mHeight = getHeight();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //不要super.OnDraw()
        Log.i(TAG, "onDraw: ");
        if(getDrawable()==null){
            return;
        }
        
        setShader();

        if(mMode==MODE_ROUND){
            //圆角
            canvas.drawRoundRect(mRoundRectF, mRoundRadius, mRoundRadius, mPaint);
        }else{
            //边框
            if (mAddBorder && mBorderWidth != 0) {
                //圆形
                canvas.drawCircle(mCircleRadius, mCircleRadius, mCircleRadius-mBorderWidth, mPaint);

                canvas.save();
                //适量转动边框
                canvas.rotate(ROTATE_DEGREE, mCircleRadius, mCircleRadius);
                canvas.drawCircle(mCircleRadius, mCircleRadius, mCircleRadius-mBorderWidth/2f, mBorderPaint);
                canvas.restore();
            }else{
                //圆形
                canvas.drawCircle(mCircleRadius, mCircleRadius, mCircleRadius, mPaint);
            }
        }
    }

    private Bitmap drawableToBitamp(Drawable drawable){
        if(drawable==null){
            return null;
        }
        if (drawable instanceof BitmapDrawable){
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setmMode(int mMode) {
        this.mMode = mMode;
    }

    public void setmRoundRadius(int mRoundRadius) {
        this.mRoundRadius = mRoundRadius;
    }

    public void setmBorderWidth(int mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
    }

    public void setmBorderStartColor(int mBorderStartColor) {
        this.mBorderStartColor = mBorderStartColor;
    }

    public void setmBorderEndColor(int mBorderEndColor) {
        this.mBorderEndColor = mBorderEndColor;
    }

    public void setmAddBorder(boolean mAddBorder) {
        this.mAddBorder = mAddBorder;
    }
}