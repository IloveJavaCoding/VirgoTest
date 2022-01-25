package com.harine.virgotest.component.clock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nepalese on 2021/3/19 10:46
 * @usage 电子数字显示器
 */
public class VirgoElecDigitalView extends View {
    private static final String TAG = "VirgoElecDigitalView";
    private static final int minWigth = 100;
    private static final float GOLD_RATE = 0.5f;//宽高比

    private static final int ALPHA_NO = 50;//未点亮数字透明度
    private static final int ALPHA_LIGHT = 255;

    private Paint mPaint;

    private int mWidth, mHeight;
    private int mPadding;//内部缩进
    private int mColor;//数字颜色
    private int mBgColor;//背景颜色
    private int mLineL;//数字边长
    private int mLingW;//数字宽度
    private int mOffset;//数字相邻边的间隔
    private int mNum;//要展示的数字

    private final List<Path> pathList = new ArrayList<>();

    public VirgoElecDigitalView(Context context) {
        this(context, null);
    }

    public VirgoElecDigitalView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoElecDigitalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mColor = Color.RED;
        mBgColor = Color.BLACK;
        mNum = 0;

        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setAlpha(ALPHA_NO);
        mPaint.setStyle(Paint.Style.FILL);
    }

    private void initFrame(){
        float leftW = (mWidth- mLineL)/2f;
        float rightW = (mWidth+ mLineL)/2f;
        float topH = mHeight/2f - mLineL;
        float bottomH = mHeight/2f + mLineL;
        
        //line 1
        Path path1 = new Path();
        path1.moveTo(leftW,topH - mOffset*2);
        path1.lineTo(rightW,topH - mOffset*2);
        path1.lineTo(rightW - mLingW,topH + mLingW - mOffset*2);
        path1.lineTo(leftW + mLingW,topH + mLingW - mOffset*2);
        path1.close();
        pathList.add(path1);

        Path path2 = new Path();
        path2.moveTo(leftW - mOffset,topH - mOffset);
        path2.lineTo(leftW - mOffset,mHeight/2f - mOffset);
        path2.lineTo(leftW + mLingW - mOffset,mHeight/2f - mLingW - mOffset);
        path2.lineTo(leftW + mLingW - mOffset,topH + mLingW - mOffset);
        path2.close();
        pathList.add(path2);

        Path path3 = new Path();
        path3.moveTo(rightW + mOffset,topH - mOffset);
        path3.lineTo(rightW + mOffset,mHeight/2f - mOffset);
        path3.lineTo(rightW - mLingW + mOffset,mHeight/2f - mLingW - mOffset);
        path3.lineTo(rightW - mLingW + mOffset,topH + mLingW - mOffset);
        path3.close();
        pathList.add(path3);

        Path path4 = new Path();
        path4.moveTo(leftW,mHeight/2f);
        path4.lineTo(leftW + mLingW,mHeight/2f - mLingW/2f);
        path4.lineTo(rightW - mLingW,mHeight/2f - mLingW/2f);
        path4.lineTo(rightW,mHeight/2f);
        path4.lineTo(rightW - mLingW,mHeight/2f + mLingW/2f);
        path4.lineTo(leftW + mLingW,mHeight/2f + mLingW/2f);
        path4.close();
        pathList.add(path4);

        Path path5 = new Path();
        path5.moveTo(leftW - mOffset,mHeight/2f + mOffset);
        path5.lineTo(leftW - mOffset,bottomH + mOffset);
        path5.lineTo(leftW + mLingW - mOffset,bottomH - mLingW + mOffset);
        path5.lineTo(leftW + mLingW - mOffset,mHeight/2f + mLingW + mOffset);
        path5.close();
        pathList.add(path5);

        Path path6 = new Path();
        path6.moveTo(rightW + mOffset,mHeight/2f + mOffset);
        path6.lineTo(rightW + mOffset,bottomH + mOffset);
        path6.lineTo(rightW - mLingW + mOffset,bottomH - mLingW + mOffset);
        path6.lineTo(rightW - mLingW + mOffset,mHeight/2f + mLingW + mOffset);
        path6.close();
        pathList.add(path6);

        Path path7 = new Path();
        path7.moveTo(leftW,bottomH + mOffset*2);
        path7.lineTo(rightW,bottomH + mOffset*2);
        path7.lineTo(rightW - mLingW,bottomH - mLingW + mOffset*2);
        path7.lineTo(leftW + mLingW,bottomH - mLingW + mOffset*2);
        path7.close();
        pathList.add(path7);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getRealSize(widthMeasureSpec);
        mPadding = mWidth/8;
        mHeight = (int) (mWidth/GOLD_RATE) - mPadding*2;
        mLineL = mWidth - mPadding*2;
        mLingW = mLineL/10;
        mOffset = mLingW/5;
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initFrame();
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mBgColor);

        switch (mNum){
            case 0:
                drawNum0(canvas);
                break;
            case 1:
                drawNum1(canvas);
                break;
            case 2:
                drawNum2(canvas);
                break;
            case 3:
                drawNum3(canvas);
                break;
            case 4:
                drawNum4(canvas);
                break;
            case 5:
                drawNum5(canvas);
                break;
            case 6:
                drawNum6(canvas);
                break;
            case 7:
                drawNum7(canvas);
                break;
            case 8:
                drawNum8(canvas);
                break;
            case 9:
                drawNum9(canvas);
                break;
        }
    }

    private void drawNum9(Canvas canvas) {
        for(int i=0; i<pathList.size(); i++){
            if(i==4){
                mPaint.setAlpha(ALPHA_NO);
            }else{
                mPaint.setAlpha(ALPHA_LIGHT);
            }
            canvas.drawPath(pathList.get(i), mPaint);
        }
    }

    private void drawNum8(Canvas canvas) {
        mPaint.setAlpha(ALPHA_LIGHT);
        for(Path path: pathList){
            canvas.drawPath(path, mPaint);
        }
    }

    private void drawNum7(Canvas canvas) {
        for(int i=0; i<pathList.size(); i++){
            if(i==0 || i==2 || i==5){
                mPaint.setAlpha(ALPHA_LIGHT);
            }else{
                mPaint.setAlpha(ALPHA_NO);
            }
            canvas.drawPath(pathList.get(i), mPaint);
        }
    }

    private void drawNum6(Canvas canvas) {
        for(int i=0; i<pathList.size(); i++){
            if(i==2){
                mPaint.setAlpha(ALPHA_NO);
            }else{
                mPaint.setAlpha(ALPHA_LIGHT);
            }
            canvas.drawPath(pathList.get(i), mPaint);
        }
    }

    private void drawNum5(Canvas canvas) {
        for(int i=0; i<pathList.size(); i++){
            if(i==2 || i==4){
                mPaint.setAlpha(ALPHA_NO);
            }else{
                mPaint.setAlpha(ALPHA_LIGHT);
            }
            canvas.drawPath(pathList.get(i), mPaint);
        }
    }

    private void drawNum4(Canvas canvas) {
        for(int i=0; i<pathList.size(); i++){
            if(i==0 || i==4 || i==6){
                mPaint.setAlpha(ALPHA_NO);
            }else{
                mPaint.setAlpha(ALPHA_LIGHT);
            }
            canvas.drawPath(pathList.get(i), mPaint);
        }
    }

    private void drawNum3(Canvas canvas) {
        for(int i=0; i<pathList.size(); i++){
            if(i==1 || i==4){
                mPaint.setAlpha(ALPHA_NO);
            }else{
                mPaint.setAlpha(ALPHA_LIGHT);
            }
            canvas.drawPath(pathList.get(i), mPaint);
        }
    }

    private void drawNum2(Canvas canvas) {
        for(int i=0; i<pathList.size(); i++){
            if(i==1 || i==5){
                mPaint.setAlpha(ALPHA_NO);
            }else{
                mPaint.setAlpha(ALPHA_LIGHT);
            }
            canvas.drawPath(pathList.get(i), mPaint);
        }
    }

    private void drawNum1(Canvas canvas) {
        for(int i=0; i<pathList.size(); i++){
            if(i==2 || i==5){
                mPaint.setAlpha(ALPHA_LIGHT);
            }else{
                mPaint.setAlpha(ALPHA_NO);
            }
            canvas.drawPath(pathList.get(i), mPaint);
        }
    }

    private void drawNum0(Canvas canvas) {

        for(int i=0; i<pathList.size(); i++){
            if(i==3){
                mPaint.setAlpha(ALPHA_NO);
            }else{
                mPaint.setAlpha(ALPHA_LIGHT);
            }
            canvas.drawPath(pathList.get(i), mPaint);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setmColor(int mColor) {
        this.mPaint.setColor(mColor);
        invalidate();
    }

    public void setmBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
        invalidate();
    }

    public void setmNum(int mNum) {
        this.mNum = mNum;
        invalidate();
    }
}