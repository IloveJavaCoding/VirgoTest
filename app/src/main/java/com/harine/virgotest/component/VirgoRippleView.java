package com.harine.virgotest.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.harine.virgotest.R;
import com.harine.virgotest.bean.VirgoDoubleCircle;
import com.harine.virgotest.ui.ComponentActivity;
import com.nepalese.virgosdk.Util.MatchUtil;
import com.nepalese.virgosdk.Util.MathUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nepalese on 2021/03/08 17:11
 * @usage 仿网易云水波纹效果
 */
public class VirgoRippleView extends View {
    private static final String TAG = "VirgoRippleView";
    private static final int START_ALPHA = 225;

    private static final int STATE_PREPARE = 0;
    private static final int STATE_PAUSE = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_DISTORY = 3;

    private Paint bigPaint;//大圆
    private Paint smallPaint;//小圆

    private int width;//宽高相等
    private float cX;//中心坐标
    private float cY;//中心坐标

    private int bigColor;//大圆颜色
    private int smallColor;//小圆颜色
    private float strokeWidth;//大圆画笔粗细
    private float baseBigRadius;//大圆起始半径
    private int baseSmallRadius;//小圆起始半径
    private int flashInterval;//刷新间隔

    private float speed;//扩散速度
    private float space;//两个圆间隔
    private int alphaSpeed;//透明度变化
    private int rotateSpeed; //旋转速度
    private int curState;//当前状态
    private boolean isStop = false;//停止动画？

    private final List<VirgoDoubleCircle> circleList = new ArrayList<>();
    private final List<VirgoDoubleCircle> clearList = new ArrayList<>();

    public VirgoRippleView(Context context) {
        this(context, null);
    }

    public VirgoRippleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoRippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.VirgoRippleView);
        bigColor = ta.getColor(R.styleable.VirgoRippleView_rvBigColor, Color.GRAY);
        smallColor = ta.getColor(R.styleable.VirgoRippleView_rvSmallColor, Color.LTGRAY);

        strokeWidth = ta.getDimension(R.styleable.VirgoRippleView_rvStrokeWidth, 5f);
        baseBigRadius = ta.getDimension(R.styleable.VirgoRippleView_rvBigRadius, 120f);
        baseSmallRadius = ta.getDimensionPixelSize(R.styleable.VirgoRippleView_rvSmallRadius, 10);

        flashInterval = ta.getInt(R.styleable.VirgoRippleView_rvFlashInterval, 120);
        speed = ta.getFloat(R.styleable.VirgoRippleView_rvSpeed, 10f);
        space =  ta.getFloat(R.styleable.VirgoRippleView_rvSpace, 60f);
        rotateSpeed = ta.getInt(R.styleable.VirgoRippleView_rvRotateSpeed, 3);
        alphaSpeed = ta.getInt(R.styleable.VirgoRippleView_rvAlphaSpeed, 35);
        ta.recycle();

        bigPaint = new Paint();
        bigPaint.setColor(bigColor);
        bigPaint.setAntiAlias(true);
        bigPaint.setStyle(Paint.Style.STROKE);
        bigPaint.setStrokeWidth(strokeWidth);

        smallPaint = new Paint();
        smallPaint.setColor(smallColor);
        smallPaint.setAntiAlias(true);
        smallPaint.setStyle(Paint.Style.FILL);

        initCircle();
    }

    //初始化圆
    private void initCircle() {
        circleList.clear();
//        circleList.add(getBaseCircle(0));
        curState = STATE_PREPARE;
    }

    //生成最基本的圆
    private VirgoDoubleCircle getBaseCircle(int degree){
        return new VirgoDoubleCircle(baseBigRadius, baseSmallRadius, degree);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getRealSize(widthMeasureSpec);

        cY = cX = width / 2f;
        setMeasuredDimension(width, width);
    }

    public int getRealSize(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.UNSPECIFIED) {
            //自己计算
            result = (int) (baseBigRadius * 4);
        } else {
            result = size;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(VirgoDoubleCircle circle: circleList){
            //修改透明度
            int num = (int) ((circle.getrBig() - baseBigRadius)/space + 1);
            int newAlpha = START_ALPHA - num*alphaSpeed;

            if(newAlpha<10){
                newAlpha = 10;
            }
            //画大圆
            bigPaint.setAlpha(newAlpha);
            canvas.drawCircle(cX, cY, circle.getrBig(), bigPaint);

            //旋转画布
            canvas.save();
            canvas.rotate(circle.getDegree(), cX, cY);

            //画小圆
            smallPaint.setAlpha(newAlpha);
            canvas.drawCircle(cX + circle.getrBig(), cY, circle.getrSmall(), smallPaint);
            canvas.restore();
        }
    }

    private float offset = 0;//已增大量
    private int interruptNum = 3;//每x个间隔一个
    private final Runnable extendTask = new Runnable() {
        @Override
        public void run() {
            clearList.clear();

            if(isStop){
                //当所有圆都消失后停止动画
                if(circleList.isEmpty()){
                    //数据初始化
                    offset = 0;
                    interruptNum = 3;
                    curState = STATE_PAUSE;
                    return;
                }
            }

            offset += speed;
            for(VirgoDoubleCircle circle: circleList){
                //更新
                circle.setrBig(circle.getrBig() + speed);
                circle.setDegree(circle.getDegree() + rotateSpeed);

                //到边缘即消失
                if(circle.getrBig()>width/2f){
                    clearList.add(circle);
                }
            }

            circleList.removeAll(clearList);

            if(!isStop){
                //增加新的
                if(offset>=space){
                    offset = 0;
                    if(interruptNum<1){
                        interruptNum = MathUtil.getRandomInt(2,5);
                    }else{
                        circleList.add(getBaseCircle(MathUtil.getRandomInt(0,360)));
                        interruptNum--;
                    }
                }
            }

            invalidate();
            handler.postDelayed(extendTask, flashInterval);
        }
    };

    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTask();
        curState = STATE_DISTORY;
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == GONE || visibility == INVISIBLE) {
            if(curState==STATE_PLAYING){
                stopAnimator();
            }
        } else {
            if(curState==STATE_PAUSE){
                startAnimotor();
            }
        }
    }

    private void startTask(){
        stopTask();
        handler.post(extendTask);
        curState = STATE_PLAYING;
    }

    private void stopTask(){
        handler.removeCallbacks(extendTask);
    }

    //================================api=================================
    public void startAnimotor(){
        isStop = false;
        if(circleList.isEmpty()){
            circleList.add(getBaseCircle(0));
        }

        startTask();
    }

    public void stopAnimator(){
        //停止新的生成
        isStop = true;
    }

    public void setBigColor(int bigColor) {
        this.bigColor = bigColor;
        bigPaint.setColor(bigColor);
    }

    public void setSmallColor(int smallColor) {
        this.smallColor = smallColor;
        smallPaint.setColor(smallColor);
    }
}