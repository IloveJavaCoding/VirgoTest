package com.harine.virgotest.component;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Administrator on 2021/9/27.
 * Usage:
 */

public class VirgoThreeImage2 extends View {
    private static final String TAG = "VirgoThreeImage2";
    private static final long INTERVAL_IMAGE_CHANGE = 10000L;//自动切图间隔
    private static final long INTERVAL_ANIMATION = 1000L;//动画时长
    private static final int MSG_INIT_OK = 1;

    private static final float RATE_SIZE_W = 1.6f;//大图较小图宽比例
    private static final float RATE_SIZE_H = 1.3f;//大图较小图高比例
    private static final int IMAGE_SIZE = 3;

    private List<File> resList;//资源缓存列表
    private Drawable[] drawables;//操作的三张图
    private ValueAnimator mAnimator;//值变化动画

    private Paint pcCur;//当前显示图片对应点
    private Paint pcOth;//其他点

    private int mWidth, mHeight;//控件宽高
    private int baseW, baseH;//正常图片宽高
    private int bigW, bigH;//大图图片宽高
    private int rCircle;//圆点半径
    private int rMargin;//圆点间隔
    private int x1,y1,x2,y2,x3,y3;//固定三个点的坐标
    private int m1,n1,m2,n2,m3,n3;//变化三个点的坐标
    private int w1,h1,w2,h2,w3,h3;//变化三个图片的宽高
    private int sX, sY;//第一个圆点的中心点位置； 整体底部居中
    private float CV;//线性变化的基础值
    private int index;//resList 最大访问索引
    private int state;//位置状态3种
    private boolean isHalf;//是否已移动到一半：第二张与第三张层次变化
    private boolean hasInited;//是否初始化完成

    public VirgoThreeImage2(Context context) {
        super(context);
        init();
    }

    public VirgoThreeImage2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoThreeImage2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        resList = new ArrayList<>();
        isHalf = false;
        hasInited = false;

        rCircle = 8;
        rMargin = 10;
        index = 0;
        state = 0;

        pcCur = new Paint();
        pcCur.setColor(Color.RED);
        pcCur.setAntiAlias(true);
        pcCur.setStyle(Paint.Style.FILL);

        pcOth = new Paint();
        pcOth.setColor(Color.LTGRAY);
        pcOth.setAntiAlias(true);
        pcOth.setStyle(Paint.Style.FILL);
//        pcOth.setStrokeWidth(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        initPosition();
        initAnimator();
    }

    private void initPosition() {
        if(mWidth>0){
            return;
        }
        Log.i(TAG, "initPosition: ");

        mWidth = getWidth();
        mHeight = getHeight();

        baseW = mWidth/3;
        baseH = mHeight*2/3;
        bigW = (int) (baseW*RATE_SIZE_W);
        bigH = (int) (baseH*RATE_SIZE_H);

        x1 = 0;
        y1 = (mHeight - baseH)/2;
        x2 = mWidth*2/3;
        y2 = y1;
        x3 = (mWidth - bigW)/2;
        y3 = (mHeight - bigH)/2;

        m1 = x1;
        n1 = y1;
        m2 = x2;
        n2 = y2;
        m3 = x3;
        n3 = y3;

        w1 = baseW;
        h1 = baseH;
        w2 = baseW;
        h2 = baseH;
        w3 = bigW;
        h3 = bigH;

        CV = x2-x1;
    }

    //线性旋转动画
    private void initAnimator() {
        //以图一为基准：右移 x1 -> x2;
        if(mAnimator==null){
            mAnimator = ValueAnimator.ofFloat(0, CV);
            mAnimator.setDuration(INTERVAL_ANIMATION);
            mAnimator.setInterpolator(new LinearInterpolator());//插值器设为线性
        }
    }

    private void initDrawable() {
        hasInited = false;
        new Thread(){
            @Override
            public void run() {
                super.run();
                drawables = new Drawable[IMAGE_SIZE];
                drawables[0] = getDrawableFromFile(resList.get(0).getAbsolutePath());
                drawables[1] = getDrawableFromFile(resList.get(2).getAbsolutePath());
                drawables[2] = getDrawableFromFile(resList.get(1).getAbsolutePath());
                handler.sendEmptyMessage(MSG_INIT_OK);
            }
        }.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(drawables==null || !hasInited){
            return;
        }

        drawImages(canvas);
        drawDots(canvas);
    }

    private void drawImages(Canvas canvas) {
        switch (state){
            case 0: //1  3  2
                //画第一张图
                drawables[0].setBounds(m1, n1, (w1+m1), (h1+n1));
                drawables[0].draw(canvas);

                if(isHalf){
                    //画第三张图
                    drawables[2].setBounds(m3, n3, (w3+m3), (h3+n3));
                    drawables[2].draw(canvas);
                    //画第二张图
                    drawables[1].setBounds(m2, n2, (w2+m2), (h2+n2));
                    drawables[1].draw(canvas);
                }else{
                    //画第二张图
                    drawables[1].setBounds(m2, n2, (w2+m2), (h2+n2));
                    drawables[1].draw(canvas);
                    //画第三张图
                    drawables[2].setBounds(m3, n3, (w3+m3), (h3+n3));
                    drawables[2].draw(canvas);
                }
                break;
            case 1://3  2  1
                //画第一张图
                drawables[2].setBounds(m3, n3, (w3+m3), (h3+n3));
                drawables[2].draw(canvas);
                if(isHalf){
                    //画第三张图
                    drawables[1].setBounds(m2, n2, (w2+m2), (h2+n2));
                    drawables[1].draw(canvas);
                    //画第二张图
                    drawables[0].setBounds(m1, n1, (w1+m1), (h1+n1));
                    drawables[0].draw(canvas);
                }else{
                    //画第二张图
                    drawables[0].setBounds(m1, n1, (w1+m1), (h1+n1));
                    drawables[0].draw(canvas);
                    //画第三张图
                    drawables[1].setBounds(m2, n2, (w2+m2), (h2+n2));
                    drawables[1].draw(canvas);
                }
                break;
            case 2://2  1  3
                //画第一张图
                drawables[1].setBounds(m2, n2, (w2+m2), (h2+n2));
                drawables[1].draw(canvas);
                if(isHalf){
                    //画第三张图
                    drawables[0].setBounds(m1, n1, (w1+m1), (h1+n1));
                    drawables[0].draw(canvas);
                    //画第二张图
                    drawables[2].setBounds(m3, n3, (w3+m3), (h3+n3));
                    drawables[2].draw(canvas);
                }else{
                    //画第二张图
                    drawables[2].setBounds(m3, n3, (w3+m3), (h3+n3));
                    drawables[2].draw(canvas);
                    //画第三张图
                    drawables[0].setBounds(m1, n1, (w1+m1), (h1+n1));
                    drawables[0].draw(canvas);
                }
                break;
        }
    }

    private void drawDots(Canvas canvas) {
        for(int i=0; i<resList.size(); i++){
            if((index==0 && i==(resList.size()-1)) || (index-1)==i){
                canvas.drawCircle(sX+i*(2*rCircle+rMargin), sY, rCircle, pcCur);
            }else {
                canvas.drawCircle(sX+i*(2*rCircle+rMargin), sY, rCircle, pcOth);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopTask();
        cancelAnim();
        super.onDetachedFromWindow();
    }

    private void cancelAnim() {
        if(mAnimator!=null){
            mAnimator.removeAllListeners();
            mAnimator.end();
            mAnimator = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private final Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            changeIamge();
            handler.postDelayed(updateTask, INTERVAL_IMAGE_CHANGE);
        }
    };

    private void changeIamge() {
        mAnimator.removeAllListeners();
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.i(TAG, "onAnimationStart: " + state);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i(TAG, "onAnimationEnd: 动画结束");
                isHalf = false;
                state++;
                if(state>=3){
                    state = state%3;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mAnimator.addUpdateListener(animation -> {
            float av = (float) animation.getAnimatedValue();
            if(!isHalf){
                if(av>CV/3){
                    Log.i(TAG, "动画执行一半: 更新资源");
                    isHalf = true;
                    //更新资源
                    updateDrawable();
                }
            }

            //s1:s2:s3 = 4 : 1+r : 3-r;
            int cY = (int) ((RATE_SIZE_H-1)*mHeight*av/(2*mWidth));//y轴变化值
            int cW = (int) ((RATE_SIZE_W-1)*av/2);//宽度变化值
            int cH = (int) ((RATE_SIZE_H-1)*av*mHeight/mWidth);//高度变化值
            switch (state){
                case 0: //1  3  2 -> 3  2  1
                    //x值变化
                    m1 = (int) (x1 + av);
                    m2 = (int) (x2 - (1+RATE_SIZE_W)*av/4);
                    m3 = (int) (x3 - (3-RATE_SIZE_W)*av/4);

                    //y值变化：n1 不变
                    n1 = y1;
                    n2 = y2 - cY;
                    n3 = y3 + cY;

                    //大小变化：bitmaps[0]不变
                    w1 = baseW;
                    h1 = baseH;
                    w2 = baseW + cW;
                    h2 = baseH + cH;
                    w3 = bigW - cW;
                    h3 = bigH - cH;
                    break;
                case 1://3  2  1 -> 2  1  3
                    m3 = (int) (x1 + av);
                    m1 = (int) (x2 - (1+RATE_SIZE_W)*av/4);
                    m2 = (int) (x3 - (3-RATE_SIZE_W)*av/4);

                    n3 = y1;
                    n1 = y2 - cY;
                    n2 = y3 + cY;

                    w3 = baseW;
                    h3 = baseH;
                    w1 = baseW + cW;
                    h1 = baseH + cH;
                    w2 = bigW - cW;
                    h2 = bigH - cH;
                    break;
                case 2://2  1  3 -> 1  3  2
                    m2 = (int) (x1 + av);
                    m3 = (int) (x2 - (1+RATE_SIZE_W)*av/4);
                    m1 = (int) (x3 - (3-RATE_SIZE_W)*av/4);

                    n2 = y1;
                    n3 = y2 - cY;
                    n1 = y3 + cY;

                    w2 = baseW;
                    h2 = baseH;
                    w3 = baseW + cW;
                    h3 = baseH + cH;
                    w1 = bigW - cW;
                    h1 = bigH - cH;
                    break;
            }

            invalidate();
        });
        mAnimator.start();
    }

    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MSG_INIT_OK:
                    updateView();
                    break;
            }
        }
    };

    private void updateView() {
        sX = (mWidth - (2*resList.size()*rCircle+rMargin*(resList.size()-1)))/2+rCircle;
        sY = mHeight - rCircle - rMargin;
        hasInited = true;
        invalidate();
    }

    private void startTask(){
        stopTask();

        handler.postDelayed(updateTask, INTERVAL_IMAGE_CHANGE);
    }

    private void stopTask(){
        handler.removeCallbacks(updateTask);
    }

    private void updateDrawable() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                switch (state){
                    case 0:
                        drawables[0] = getNextDrawable();
                        break;
                    case 1:
                        drawables[2] = getNextDrawable();
                        break;
                    case 2:
                        drawables[1] = getNextDrawable();
                        break;
                }
            }
        }.start();
    }

    private Drawable getNextDrawable(){
        if (resList.isEmpty()){
            return null;
        }
        index++;

        if(index>=resList.size() || index<0){
            index = 0;
        }

        return getDrawableFromFile(resList.get(index).getAbsolutePath());
    }

    private Drawable getDrawableFromFile(String filePath) {
        Log.i(TAG, "getDrawableFromFile: " + filePath);
        if (TextUtils.isEmpty(filePath)) {
            return null;
        } else {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            } else {
                FileInputStream inputStream = null;
                Drawable drawable = null;

                try {
                    inputStream = new FileInputStream(filePath);
                    Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
                    drawable = new BitmapDrawable(getResources(), bitmap);
//                    bitmap.recycle();
                } catch (IOException var5) {
                    var5.printStackTrace();
                }finally {
                    if(inputStream!=null){
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return drawable;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setResList(List<File> list){
        if(list==null || list.isEmpty()){
            return;
        }

        index = 2;
        resList.clear();
        resList.addAll(list);
        initDrawable();
    }

    public void startPlay(){
        startTask();
    }
}
