package com.harine.virgotest.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.harine.virgotest.bean.VirgoDrop;
import com.harine.virgotest.util.TempUtil;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nepalese on 2021/5/24 10:06
 * @usage
 */
public class VirgoRainView extends View {
    private static final String TAG = "VirgoRainView";
    private static final int MOVE_DELAY = 100;//刷新间隔
    private static final float GA = 9.8f;//重力加速度；
    //一般而言，直径0.5mm的毛毛雨收尾速度为2米每秒，而直径5、5mm左右的暴雨雨滴最大收尾速度为8到9米每秒。
    private static final float MAX_SPEED_2 = 2f;

    private Paint mPaint;

    private int mWidth, mHeight;
    private int mColor;//主体颜色
    private int mBgColor;//背景色
    private int mAlpha;//起始透明度[0,255]
    private int mDropNum;//同时显示最大雨点数
    private int mPart;//纵向划分等分数
    private float mTextSize;//字体大小

    private final int[] mColors = new int[2];
    private final List<VirgoDrop> dropList = new ArrayList<>();
    private final List<VirgoDrop> clearDrop = new ArrayList<>();

    public VirgoRainView(Context context) {
        this(context, null);
    }

    public VirgoRainView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoRainView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTextSize = 18f;
        mDropNum = 30;
        mPart = 3;
        mAlpha = 50;
        mColor = Color.parseColor("#ff00ff00");
        mBgColor = Color.BLACK;

        parseColors(mColor);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        mPaint.setStyle(Paint.Style.FILL);
    }

    //初始化雨滴
    private void initDrops() {
        dropList.clear();

        int number = mDropNum/mPart;//每个区域雨滴数
        int spaceW = mWidth / number;//横向划分区
        int spaceH = mHeight / mPart;//纵向划分区
        for(int n=0; n<mPart; n++){
            for(int i=0; i<mDropNum; i++){
                int x = TempUtil.getRandomInt(i*spaceW, (i+1)*spaceW);
                int y = TempUtil.getRandomInt(n*spaceH,(n+1)*spaceH);
                VirgoDrop drop = new VirgoDrop(x, y);
                dropList.add(drop);
            }
        }
    }

    private void parseColors(@ColorInt int mColor) {
        mColors[0] = (mAlpha << 24) | (mColor & 0x00ff0000) | (mColor & 0x0000ff00) | (mColor & 0x000000ff);
        mColors[1] = mColor;
    }

    private Path getPath(VirgoDrop drop){
        float len = mPaint.measureText(drop.getContent());
        Path path = new Path();
        path.moveTo(drop.getStartX(), drop.getStartY());
        path.lineTo(drop.getStartX(), drop.getStartY()+len);
        path.close();
        return path;
    }

    /**
     * 设置画笔渲染样式
     * @param drop
     */
    private void setShader(VirgoDrop drop){
        float len = mPaint.measureText(drop.getContent());
        //渐变渲染
        Shader mShader = new LinearGradient(drop.getStartX(), drop.getStartY(),
                drop.getStartX(), drop.getStartY()+len, mColors,
                null, Shader.TileMode.CLAMP);
        mPaint.setShader(mShader);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mWidth = this.getWidth();
        this.mHeight = this.getHeight();

        initDrops();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景
        canvas.drawColor(mBgColor);

        //画雨滴
        for(VirgoDrop drop: dropList){
            setShader(drop);
            canvas.drawTextOnPath(drop.getContent(),
                    getPath(drop), 0,0, mPaint);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void startMove(){
        stopMove();
        handler.post(moveTask);
    }

    private void stopMove(){
        handler.removeCallbacks(moveTask);
    }

    private final Runnable moveTask = new Runnable() {
        @Override
        public void run() {
            moveDrop();
            handler.postDelayed(moveTask, MOVE_DELAY);
        }
    };

    private void moveDrop() {
        clearDrop.clear();
        //需新生成个数
        int mNewNum = 0;
        for(VirgoDrop d: dropList){
            d.setMoveTime(d.getMoveTime()+ MOVE_DELAY/1000f);

            //s = 1/2*g*t^2
            float t = d.getMoveTime();
            float s;
            if(GA*t<MAX_SPEED_2){
                //1. 加速运动
                s = (float) (0.5f*GA*Math.pow(t, 2));
            }else{
                //2. 匀速运动
                s = MAX_SPEED_2 * (t - MAX_SPEED_2/GA);
            }
            d.setStartY(d.getStartY()+s);

            if(d.getStartY()>mHeight){
                mNewNum++;
                clearDrop.add(d);
            }
        }

        if(mNewNum>0){
            dropList.removeAll(clearDrop);

            for(int i = 0; i< mNewNum; i++){
                generateNewDrop();
            }
        }
        invalidate();
    }

    private void generateNewDrop(){
        int x = TempUtil.getRandomInt(1,mWidth);
        int y = TempUtil.getRandomInt(-mHeight/6,mHeight*5/6);
//        VirgoDrop virgoDrop = new VirgoDrop(x,0);
//        float len = mPaint.measureText(virgoDrop.getContent());
//        virgoDrop.setStartY(-len);
        dropList.add( new VirgoDrop(x,y));
    }

    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopMove();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != VISIBLE) {
            stopMove();
        } else {
            startMove();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 设置雨滴主体颜色
     * @param mColor
     */
    public void setmColor(@ColorInt int mColor) {
        this.mColor = mColor;
        parseColors(mColor);
    }

    /**
     * 设置背景色
     * @param mBgColor
     */
    public void setmBgColor(@ColorInt int mBgColor) {
        this.mBgColor = mBgColor;
    }

    /**
     * 设置雨滴起始透明度
     * @param mAlpha
     */
    public void setmAlpha(int mAlpha) {
        if(mAlpha>255 || mAlpha<0){
            throw new InvalidParameterException("[0,255]");
        }
        this.mAlpha = mAlpha;
        mColors[0] = (mAlpha << 24) | (mColor & 0x00ff0000) | (mColor & 0x0000ff00) | (mColor & 0x000000ff);
    }

    /**
     * 设置雨滴数量
     * @param mDropNum
     */
    public void setmDropNum(int mDropNum) {
        this.mDropNum = mDropNum;
    }

    /**
     * 设置雨滴大小
     * @param mTextSize
     */
    public void setmTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
        mPaint.setTextSize(mTextSize);
    }
}
