package com.harine.virgotest.component;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Created by Administrator on 2021/9/27.
 * Usage:
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VirgoThreeImage extends View {
    private static final String TAG = "VirgoThreeImage";
    private static final long INTERVAL_IMAGE_CHANGE = 10000L;//自动切图间隔
    private static final long INTERVAL_ANIMATION = 1000L;//动画时长
    private static final int MSG_INIT_OK = 1;

    private static final float RATE_SIZE_W = 1.6f;//大图较小图宽比例
    private static final float RATE_SIZE_H = 1.3f;//大图较小图高比例
    private static final int IMAGE_SIZE = 3;

    private List<File> resList;//资源缓存列表
    private Bitmap[] bitmaps;//操作的三张图
    private ValueAnimator mAnimator;//值变化动画

    private Paint pcCur;//当前显示图片对应点
    private Paint pcOth;//其他点

    private Paint pImg1;//圆角矩形1
    private Paint pImg2;//圆角矩形2
    private Paint pImg3;//圆角矩形3

    private int mWidth, mHeight;//控件宽高
    private float baseW, baseH;//正常图片宽高
    private float bigW, bigH;//大图图片宽高
    private float rCircle;//圆点半径
    private float rMargin;//圆点间隔
    private float x1,y1,x2,y2,x3,y3;//固定三个点的坐标
    private float m1,n1,m2,n2,m3,n3;//变化三个点的坐标
    private float w1,h1,w2,h2,w3,h3;//变化三个图片的宽高
    private float cX, cY;//第一个圆点的中心点位置； 整体底部居中
    private float rX, rY;//圆角半径
    private float CV;//线性变化的基础值

    private int index;//resList 最大访问索引
    private int state;//位置状态3种
    private boolean isHalf;//是否已移动到一半：第二张与第三张层次变化
    private boolean hasInited;//是否初始化完成
    private boolean isReverse;//逆方向切换
    private boolean isAnimation;//动画中？
    private float startX;//滑动时按下点
    private boolean hasSlip;//滑动时是否触发动画

    public VirgoThreeImage(Context context) {
        this(context, null);
    }

    public VirgoThreeImage(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoThreeImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        resList = new ArrayList<>();
        isHalf = false;
        hasInited = false;
        isReverse = false;
        isAnimation = false;

        rCircle = 8f;
        rMargin = 10f;
        index = 0;
        state = 0;
        rX = 10f;
        rY = 10f;

        pcCur = new Paint();
        pcCur.setColor(Color.RED);
        pcCur.setAntiAlias(true);
        pcCur.setStyle(Paint.Style.FILL);

        pcOth = new Paint();
        pcOth.setColor(Color.LTGRAY);
        pcOth.setAntiAlias(true);
        pcOth.setStyle(Paint.Style.FILL);

        pImg1 = new Paint();
        pImg1.setAntiAlias(true);

        pImg2 = new Paint();
        pImg2.setAntiAlias(true);

        pImg3 = new Paint();
        pImg3.setAntiAlias(true);
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

        baseW = mWidth/3f;
        baseH = mHeight*2/3f;
        bigW = baseW*RATE_SIZE_W;
        bigH = baseH*RATE_SIZE_H;

        x1 = 0;
        y1 = (mHeight - baseH)/2;
        x2 = mWidth*2/3f;
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
            Log.i(TAG, "initAnimator: ");
            mAnimator = ValueAnimator.ofFloat(0, CV);
            mAnimator.setDuration(INTERVAL_ANIMATION);
            mAnimator.setInterpolator(new LinearInterpolator());//插值器设为线性
        }
    }

    private void initBitmaps() {
        hasInited = false;
        new Thread(){
            @Override
            public void run() {
                super.run();
                bitmaps = new Bitmap[IMAGE_SIZE];
                bitmaps[0] = getBitmapFromFile(resList.get(0).getAbsolutePath());
                bitmaps[1] = getBitmapFromFile(resList.get(1).getAbsolutePath());
                bitmaps[2] = getBitmapFromFile(resList.get(2).getAbsolutePath());
                handler.sendEmptyMessage(MSG_INIT_OK);
            }
        }.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(bitmaps==null || !hasInited){
            return;
        }
        setShader();
        drawImages(canvas);
        drawDots(canvas);
    }

    private void setShader(){
        Matrix mMatrix = new Matrix();

        //不管Rect如果变化，BitmapShaderView的宽高始终是整个View的宽高
        //BitmapShader的绘制原理是从视图原点开始绘制第一个Bitmap,然后在按设置的模式先绘制Y轴，然后根据Y轴的绘制，绘制X轴这样去绘制超过Bitmap的部分。
        //按这样的原理绘制的图形此时并不会显示，只有当canvas调用draw绘制后，才显示canvas绘制的那部分
        BitmapShader mShader = new BitmapShader(bitmaps[0], Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //将图片缩放到绘制区大小
        mMatrix.setScale(w1/bitmaps[0].getWidth(), h1/bitmaps[0].getHeight());
        mShader.setLocalMatrix(mMatrix);
        pImg1.setShader(mShader);

        mShader = new BitmapShader(bitmaps[2], Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mMatrix.setScale(w2/bitmaps[2].getWidth(), h2/bitmaps[2].getHeight());
        mShader.setLocalMatrix(mMatrix);
        pImg2.setShader(mShader);

        mShader = new BitmapShader(bitmaps[1], Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mMatrix.setScale(w3/bitmaps[1].getWidth(), h3/bitmaps[1].getHeight());
        mShader.setLocalMatrix(mMatrix);
        pImg3.setShader(mShader);
    }

    private void drawImages(Canvas canvas) {
        switch (state){
            case 0: //1  3  2
                if(isReverse){//1  3  2 -> 2  1  3
                    //画第二张图
                    canvas.save();
                    canvas.translate(m2, n2);
                    canvas.drawRoundRect(0, 0, w2, h2, rX,rY, pImg2);
                    canvas.restore();

                    if(isHalf){
                        //画第三张图
                        canvas.save();
                        canvas.translate(m3, n3);
                        canvas.drawRoundRect(0, 0, w3, h3, rX,rY, pImg3);
                        canvas.restore();

                        //画第一张图
                        canvas.save();
                        //移动画布与绘制区起点重合
                        canvas.translate(m1, n1);
                        canvas.drawRoundRect(0, 0, w1, h1, rX,rY, pImg1);
                        canvas.restore();
                    }else{
                        //画第一张图
                        canvas.save();
                        //移动画布与绘制区起点重合
                        canvas.translate(m1, n1);
                        canvas.drawRoundRect(0, 0, w1, h1, rX,rY, pImg1);
                        canvas.restore();

                        //画第三张图
                        canvas.save();
                        canvas.translate(m3, n3);
                        canvas.drawRoundRect(0, 0, w3, h3, rX,rY, pImg3);
                        canvas.restore();
                    }
                }else{
                    //画第一张图
                    canvas.save();
                    //移动画布与绘制区起点重合
                    canvas.translate(m1, n1);
                    canvas.drawRoundRect(0, 0, w1, h1, rX,rY, pImg1);
                    canvas.restore();

                    if(isHalf){
                        //画第三张图
                        canvas.save();
                        canvas.translate(m3, n3);
                        canvas.drawRoundRect(0, 0, w3, h3, rX,rY, pImg3);
                        canvas.restore();

                        //画第二张图
                        canvas.save();
                        canvas.translate(m2, n2);
                        canvas.drawRoundRect(0, 0, w2, h2, rX,rY, pImg2);
                        canvas.restore();
                    }else{
                        //画第二张图
                        canvas.save();
                        canvas.translate(m2, n2);
                        canvas.drawRoundRect(0, 0, w2, h2, rX,rY, pImg2);
                        canvas.restore();
                        //画第三张图
                        canvas.save();
                        canvas.translate(m3, n3);
                        canvas.drawRoundRect(0, 0, w3, h3, rX,rY, pImg3);
                        canvas.restore();
                    }
                }
                break;
            case 1://3  2  1
                if(isReverse){ //3  2  1 -> 1  3  2
                    //画第二张图
                    canvas.save();
                    canvas.translate(m1, n1);
                    canvas.drawRoundRect(0, 0, w1, h1, rX,rY, pImg1);
                    canvas.restore();

                    if(isHalf){
                        //画第三张图
                        canvas.save();
                        canvas.translate(m2, n2);
                        canvas.drawRoundRect(0, 0, w2, h2, rX,rY, pImg2);
                        canvas.restore();
                        //画第一张图
                        canvas.save();
                        canvas.translate(m3, n3);
                        canvas.drawRoundRect(0, 0, w3, h3, rX,rY, pImg3);
                        canvas.restore();
                    }else{
                        //画第一张图
                        canvas.save();
                        canvas.translate(m3, n3);
                        canvas.drawRoundRect(0, 0, w3, h3, rX,rY, pImg3);
                        canvas.restore();
                        //画第三张图
                        canvas.save();
                        canvas.translate(m2, n2);
                        canvas.drawRoundRect(0, 0, w2, h2, rX,rY, pImg2);
                        canvas.restore();
                    }
                }else{
                    //画第一张图
                    canvas.save();
                    canvas.translate(m3, n3);
                    canvas.drawRoundRect(0, 0, w3, h3, rX,rY, pImg3);
                    canvas.restore();
                    if(isHalf){
                        //画第三张图
                        canvas.save();
                        canvas.translate(m2, n2);
                        canvas.drawRoundRect(0, 0, w2, h2, rX,rY, pImg2);
                        canvas.restore();
                        //画第二张图
                        canvas.save();
                        canvas.translate(m1, n1);
                        canvas.drawRoundRect(0, 0, w1, h1, rX,rY, pImg1);
                        canvas.restore();
                    }else{
                        //画第二张图
                        canvas.save();
                        canvas.translate(m1, n1);
                        canvas.drawRoundRect(0, 0, w1, h1, rX,rY, pImg1);
                        canvas.restore();
                        //画第三张图
                        canvas.save();
                        canvas.translate(m2, n2);
                        canvas.drawRoundRect(0, 0, w2, h2, rX,rY, pImg2);
                        canvas.restore();
                    }
                }
                break;
            case 2://2  1  3
                if(isReverse){
                    //画第二张图
                    canvas.save();
                    canvas.translate(m3, n3);
                    canvas.drawRoundRect(0, 0, w3, h3, rX,rY, pImg3);
                    canvas.restore();
                    if(isHalf){
                        //画第三张图
                        canvas.save();
                        canvas.translate(m1, n1);
                        canvas.drawRoundRect(0, 0, w1, h1, rX,rY, pImg1);
                        canvas.restore();

                        //画第一张图
                        canvas.save();
                        canvas.translate(m2, n2);
                        canvas.drawRoundRect(0, 0, w2, h2, rX,rY, pImg2);
                        canvas.restore();
                    }else{
                        //画第一张图
                        canvas.save();
                        canvas.translate(m2, n2);
                        canvas.drawRoundRect(0, 0, w2, h2, rX,rY, pImg2);
                        canvas.restore();
                        //画第三张图
                        canvas.save();
                        canvas.translate(m1, n1);
                        canvas.drawRoundRect(0, 0, w1, h1, rX,rY, pImg1);
                        canvas.restore();
                    }
                }else{
                    //画第一张图
                    canvas.save();
                    canvas.translate(m2, n2);
                    canvas.drawRoundRect(0, 0, w2, h2, rX,rY, pImg2);
                    canvas.restore();
                    if(isHalf){
                        //画第三张图
                        canvas.save();
                        canvas.translate(m1, n1);
                        canvas.drawRoundRect(0, 0, w1, h1, rX,rY, pImg1);
                        canvas.restore();
                        //画第二张图
                        canvas.save();
                        canvas.translate(m3, n3);
                        canvas.drawRoundRect(0, 0, w3, h3, rX,rY, pImg3);
                        canvas.restore();
                    }else{
                        //画第二张图
                        canvas.save();
                        canvas.translate(m3, n3);
                        canvas.drawRoundRect(0, 0, w3, h3, rX,rY, pImg3);
                        canvas.restore();
                        //画第三张图
                        canvas.save();
                        canvas.translate(m1, n1);
                        canvas.drawRoundRect(0, 0, w1, h1, rX,rY, pImg1);
                        canvas.restore();
                    }
                }
                break;
        }
    }

    private void drawDots(Canvas canvas) {
        for(int i=0; i<resList.size(); i++){
            if(isReverse){
                if((i==0 && index==resList.size()-1) || (index+1)==i){
                    canvas.drawCircle(cX +i*(2*rCircle+rMargin), cY, rCircle, pcCur);
                }else {
                    canvas.drawCircle(cX +i*(2*rCircle+rMargin), cY, rCircle, pcOth);
                }
            }else{
                if((index==0 && i==(resList.size()-1)) || (index-1)==i){
                    canvas.drawCircle(cX +i*(2*rCircle+rMargin), cY, rCircle, pcCur);
                }else {
                    canvas.drawCircle(cX +i*(2*rCircle+rMargin), cY, rCircle, pcOth);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                hasSlip = false;
                stopTask();
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.i(TAG, "scrollY: " + event.getX());
                if(!hasSlip){
                    //等下一次吧！
                    if(event.getX()-startX>100){
                        //上一张
                        hasSlip=true;
                        playLast();
                    }else if(startX-event.getX()>100){
                        //下一张
                        hasSlip=true;
                        playNext();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(hasSlip){
                    hasSlip = false;
                }else{
                    startTask();
                }
                break;
        }

        return true;
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
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i(TAG, "onAnimationEnd: 动画结束");
                if(isReverse){
                    isReverse = false;
                    index+=2;//跳回到预备点
                    if(index>=resList.size()){
                        index = index - resList.size();
                    }

                    state--;
                    if(state<0){
                        state = 2;
                    }
                }else{
                    state++;
                    if(state>=3){
                        state = state%3;
                    }
                }

                isHalf = false;
                isAnimation = false;
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
                    updateBitmap();
                }
            }

            //s1:s2:s3 = 4 : 1+r : 3-r;
            float cY =  (RATE_SIZE_H-1)*mHeight*av/(2*mWidth);//y轴变化值
            float cW = (RATE_SIZE_W-1)*av/2;//宽度变化值
            float cH = (RATE_SIZE_H-1)*av*mHeight/mWidth;//高度变化值

            switch (state){
                case 0:
                    if(isReverse){//1  3  2 -> 2  1  3
                        //x值变化
                        m1 = x1 + (3-RATE_SIZE_W)*av/4;
                        m2 = x2 - av;
                        m3 = x3 + (1+RATE_SIZE_W)*av/4;

                        //y值变化：n1 不变
                        n1 = y1 - cY;
                        n2 = y2;
                        n3 = y3 + cY;

                        //大小变化：bitmaps[0]不变
                        w1 = baseW + cW;
                        h1 = baseH + cH;
                        w2 = baseW;
                        h2 = baseH;
                    }else{//1  3  2 -> 3  2  1
                        //x值变化
                        m1 = x1 + av;
                        m2 = x2 - (1+RATE_SIZE_W)*av/4;
                        m3 = x3 - (3-RATE_SIZE_W)*av/4;

                        //y值变化：n1 不变
                        n1 = y1;
                        n2 = y2 - cY;
                        n3 = y3 + cY;

                        //大小变化：bitmaps[0]不变
                        w1 = baseW;
                        h1 = baseH;
                        w2 = baseW + cW;
                        h2 = baseH + cH;
                    }
                    w3 = bigW - cW;
                    h3 = bigH - cH;
                    break;
                case 1:
                    if(isReverse){ //3  2  1 -> 1  3  2
                        m3 = x1 + (3-RATE_SIZE_W)*av/4;
                        m1 = x2 - av;
                        m2 = x3 + (1+RATE_SIZE_W)*av/4;

                        //y值变化：n1 不变
                        n3 = y1 - cY;
                        n1 = y2;
                        n2 = y3 + cY;

                        //大小变化：bitmaps[0]不变
                        w3 = baseW + cW;
                        h3 = baseH + cH;
                        w1 = baseW;
                        h1 = baseH;
                    }else{//3  2  1 -> 2  1  3
                        m3 = x1 + av;
                        m1 = x2 - (1+RATE_SIZE_W)*av/4;
                        m2 = x3 - (3-RATE_SIZE_W)*av/4;

                        n3 = y1;
                        n1 = y2 - cY;
                        n2 = y3 + cY;

                        w3 = baseW;
                        h3 = baseH;
                        w1 = baseW + cW;
                        h1 = baseH + cH;
                    }
                    w2 = bigW - cW;
                    h2 = bigH - cH;
                    break;
                case 2:
                    if(isReverse){//2  1  3 -> 3  2  1
                        m2 = x1 + (3-RATE_SIZE_W)*av/4;
                        m3 = x2 - av;
                        m1 = x3 + (1+RATE_SIZE_W)*av/4;

                        //y值变化：n1 不变
                        n2 = y1 - cY;
                        n3 = y2;
                        n1 = y3 + cY;

                        //大小变化：bitmaps[0]不变
                        w2 = baseW + cW;
                        h2 = baseH + cH;
                        w3 = baseW;
                        h3 = baseH;
                    }else{//2  1  3 -> 1  3  2
                        m2 = x1 + av;
                        m3 = x2 - (1+RATE_SIZE_W)*av/4;
                        m1 = x3 - (3-RATE_SIZE_W)*av/4;

                        n2 = y1;
                        n3 = y2 - cY;
                        n1 = y3 + cY;

                        w2 = baseW;
                        h2 = baseH;
                        w3 = baseW + cW;
                        h3 = baseH + cH;
                    }
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
            if (msg.what == MSG_INIT_OK) {
                updateView();
            }
        }
    };

    private void updateView() {
        Log.i(TAG, "updateView: ");
        cX = (mWidth - (2*resList.size()*rCircle+rMargin*(resList.size()-1)))/2+rCircle;
        cY = mHeight - rCircle - rMargin;

        hasInited = true;
        invalidate();
    }

    private void startTask(){
        stopTask();

        handler.postDelayed(updateTask, INTERVAL_IMAGE_CHANGE);
    }

    private void startTaskNow() {
        stopTask();
        handler.post(updateTask);
    }

    private void stopTask(){
        handler.removeCallbacks(updateTask);
    }

    private void updateBitmap() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                if(isReverse){
                    switch (state){
                        case 0:
                            bitmaps[2] = getNextBitmap();
                            break;
                        case 1:
                            bitmaps[0] = getNextBitmap();
                            break;
                        case 2:
                            bitmaps[1] = getNextBitmap();
                            break;
                    }
                }else{
                    switch (state){
                        case 0:
                            bitmaps[0] = getNextBitmap();
                            break;
                        case 1:
                            bitmaps[1] = getNextBitmap();
                            break;
                        case 2:
                            bitmaps[2] = getNextBitmap();
                            break;
                    }
                }
            }
        }.start();
    }

    private Bitmap getNextBitmap(){
        if (resList.isEmpty()){
            return null;
        }

//        Log.i(TAG, "getNextBitmap: index: " + index + ",  size: " + resList.size());

        if(isReverse){
            if(index<0){
                index = resList.size()+index;
            }
        }else{
            index++;
            if(index>=resList.size()){
                index = 0;
            }
        }

        return getBitmapFromFile(resList.get(index).getAbsolutePath());
    }

    private Bitmap getBitmapFromFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        } else {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            } else {
                FileInputStream inputStream = null;
                Bitmap bitmap = null;

                try {
                    inputStream = new FileInputStream(filePath);
                    bitmap = BitmapFactory.decodeStream(inputStream);
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

                return bitmap;
            }
        }
    }

    //立即切换下一张
    private void playNextNow(){
        if(isAnimation){
            return;
        }
        isReverse = false;
        startTaskNow();
    }

    //立即切换上一张
    private void playLastNow(){
        if(isAnimation){
            return;
        }
        index-=3;
        isReverse = true;
        startTaskNow();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setResList(List<File> list){
        if(list==null || list.isEmpty()){
            return;
        }

        index = 2;
        resList.clear();
        resList.addAll(list);
        initBitmaps();
    }

    public void startPlay(){
        startTask();
    }

    public void playNext(){
        playNextNow();
    }

    public void playLast(){
        playLastNow();
    }
}
