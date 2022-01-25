package com.harine.virgotest.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.harine.virgotest.bean.VirgoPoint;

import java.util.Arrays;

/**
 * @author nepalese on 2021/3/1 08:47
 * @usage
 */
public class VirgoTicToe extends View {
    private static final String TAG = "VirgoTicToe";
    private static final int minWigth = 90;
    private static final int mLineNum = 3;//等分数
    private static final int maxStep = 9;
    private static final int MSG_AI_GO = 1;

    private static final float mOffsetX = 0.3f;//标记偏移量
    private static final float mOffsetY = 0.7f;//标记偏移量

    public static final int WIN_O = 1;//O赢
    public static final int WIN_X = 2;//X赢
    public static final int WIN_DRAW = 3;//平局

    public static final int _O = 1;//O
    public static final int _X = 2;//X
    public static final int _BLANK = 0;//blank

    private ticToeCallback callback;
    private Paint paintLine;//背景线
    private Paint paintO;//player o
    private Paint paintX;//player x

    private int mWidth, mHeight;//宽高，相等
    private float mRect;//每格宽度
    private float mRoundR;//标点半径
    private int mSteps;//共走步数
    private boolean isOGo = true;//O走？（O先行）
    private boolean isFreeze = false;//游戏结束？
    private boolean isAIMode = true;//人机大战？

    private int bgColor;//背景颜色
    private final int[] tags = new int[9];//记录每格情况

    public VirgoTicToe(Context context) {
        this(context, null);
    }

    public VirgoTicToe(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoTicToe(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initTag();
        mSteps = 0;
        mRoundR = 25;
        bgColor = Color.argb(75,255,255,255);

        paintLine = new Paint();
        paintLine.setColor(Color.rgb(211,211,211));
        paintLine.setAntiAlias(true);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(3);

        paintO = new Paint();
        paintO.setColor(Color.GREEN);
        paintO.setAntiAlias(true);
        paintO.setStyle(Paint.Style.FILL);

        paintX = new Paint();
        paintX.setColor(Color.RED);
        paintX.setAntiAlias(true);
        paintX.setStyle(Paint.Style.FILL);
    }

    private void initTag() {
        Arrays.fill(tags, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = mWidth = getRealSize(widthMeasureSpec);
        mRect = mWidth/3f;
        //标记大小
        float mTextSize = mRect / 2f;
        paintO.setTextSize(mTextSize);
        paintX.setTextSize(mTextSize);
        setMeasuredDimension(mWidth, mHeight);
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
    public void draw(Canvas canvas) {
        //背景
        super.draw(canvas);
        canvas.drawColor(bgColor);

        //边框
        for (int i = 0; i < mLineNum; i++) {
            //行
            for(int j = 0; j < mLineNum; j++){
                //竖
                canvas.drawRoundRect(new RectF(mRect * j, mRect * i, mRect * (j+1), mRect * (i+1)), mRoundR, mRoundR, paintLine);
            }
        }

        //画标记
        for(int i=0; i<tags.length; i++){
            int tag = tags[i];
            if(tag>0){//非空
                VirgoPoint p = getPointFromTag(i);
                if(tag==_O){
                    //画O
                    canvas.drawText("O", (p.getX() + mOffsetX)*mRect, (p.getY() + mOffsetY)*mRect, paintO);
                }else{
                    //画X
                    canvas.drawText("X", (p.getX() + mOffsetX)*mRect, (p.getY() + mOffsetY)*mRect, paintX);
                }
            }
        }

        //判断输赢
        judgeResult();
    }

    //每格所对应的坐标
    private VirgoPoint getPointFromTag(int i) {
        VirgoPoint point = null;
        switch (i){
            case 0:
                point = new VirgoPoint(0,0);
                break;
            case 1:
                point = new VirgoPoint(1,0);
                break;
            case 2:
                point = new VirgoPoint(2,0);
                break;
            case 3:
                point = new VirgoPoint(0,1);
                break;
            case 4:
                point = new VirgoPoint(1,1);
                break;
            case 5:
                point = new VirgoPoint(2,1);
                break;
            case 6:
                point = new VirgoPoint(0,2);
                break;
            case 7:
                point = new VirgoPoint(1,2);
                break;
            case 8:
                point = new VirgoPoint(2,2);
                break;
            default:
                break;
        }
        return point;
    }

    //根据坐标定格
    private int getTagFromPoint(VirgoPoint point) {
        switch (point.getX()){
            case 0://第一列
                switch (point.getY()){
                    case 0:
                        return 0;
                    case 1:
                        return 3;
                    default:
                        return 6;
                }
            case 1://第二列
                switch (point.getY()){
                    case 0:
                        return 1;
                    case 1:
                        return 4;
                    default:
                        return 7;
                }
            default://第三列
                switch (point.getY()){
                    case 0:
                        return 2;
                    case 1:
                        return 5;
                    default:
                        return 8;
                }
        }
    }

    private void judgeResult() {
        if (mSteps > 4) {
            if(judgeLine(_O) || judgeColumn(_O) || judgeSlash(_O)){
                win(WIN_O);
                return;
            }

            if(judgeLine( _X) || judgeColumn( _X) || judgeSlash(_X)){
                win(WIN_X);
                return;
            }

            if (mSteps == maxStep && !isFreeze) {
                win(WIN_DRAW);
            }
        }
    }

    private void win(int a){
        isFreeze = true;
        callback.gameOver(a);
    }

    //横
    private boolean judgeLine(int a){
        if(a==_O){
           return ((tags[0]==_O && tags[1]==_O && tags[2]==_O)
                   ||(tags[3]==_O && tags[4]==_O && tags[5]==_O)
                   ||(tags[6]==_O && tags[7]==_O && tags[8]==_O));
        }else{
            return ((tags[0]==_X && tags[1]==_X && tags[2]==_X)
                    ||(tags[3]==_X && tags[4]==_X && tags[5]==_X)
                    ||(tags[6]==_X && tags[7]==_X && tags[8]==_X));
        }
    }

    //竖
    private boolean judgeColumn(int a){
        if(a==_O){
            return ((tags[0]==_O && tags[3]==_O && tags[6]==_O)
                    ||(tags[1]==_O && tags[4]==_O && tags[7]==_O)
                    ||(tags[2]==_O && tags[5]==_O && tags[8]==_O));
        }else{
            return ((tags[0]==_X && tags[3]==_X && tags[6]==_X)
                    ||(tags[2]==_X && tags[4]==_X && tags[7]==_X)
                    ||(tags[2]==_X && tags[5]==_X && tags[8]==_X));
        }
    }

    //斜
    private boolean judgeSlash(int a){
        if(a==_O){
            return ((tags[0]==_O && tags[4]==_O && tags[8]==_O)
                    ||(tags[2]==_O && tags[4]==_O && tags[6]==_O));
        }else{
            return ((tags[0]==_X && tags[4]==_X && tags[8]==_X)
                    ||(tags[2]==_X && tags[4]==_X && tags[6]==_X));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(!isFreeze){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    //生成新子
                    generatePoint(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    //落子无悔
                    invalidate();
                    //AI
                    handler.sendEmptyMessageDelayed(MSG_AI_GO, 100);
                    break;
            }
        }

        return true;
    }

    private void generatePoint(float x, float y) {
        if(x<0 || y<0 || x>mRect*mLineNum || y>mRect*mLineNum){
            return;
        }

        int pX = (int) ((x)/mRect);
        int pY = (int) ((y)/mRect);

        VirgoPoint p = new VirgoPoint(pX, pY);
        Log.i(TAG, "generatePoint: " + pX +" "+ pY);
        int position = getTagFromPoint(p);
        Log.i(TAG, "generatePoint: " + position);
        //判断是否已被占领
        if(tags[position]>0){
            return;
        }

        mSteps++;

        if(isOGo){
            isOGo = false;
            tags[position] = _O;
        }else{
            isOGo = true;
            tags[position] = _X;
        }

        //下一步
        if(!isAIMode){
            callback.nextPlay(isOGo);
        }
    }

    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==MSG_AI_GO){
                if(isAIMode && !isOGo && !isFreeze ){
                    AIgo();
                }
            }
        }
    };

    ///////////////////////////////////////////ai play///////////////////////////////////////////
    private void AIgo() {
        isOGo = true;

        //ai 第一步
        if(mSteps<2){
            //抢占中间点
            if(tags[4]==_BLANK){
                robotGo(4);
            }else{
                int random = getRandom(0,8,4);
                Log.i(TAG, "AIgo: " + random);
                robotGo(random);
            }
        }

        if(mSteps>2) {
            //to win first
            int r1 = howToWin(0, 1, 2);
            if (r1 > -1) {
                robotGo(r1);
            } else {
                int r2 = howToWin(3, 4, 5);
                if (r2 > -1) {
                    robotGo(r2);
                } else {
                    int r3 = howToWin(6, 7, 8);
                    if (r3 > -1) {
                        robotGo(r3);
                    } else {
                        int r4 = howToWin(0, 3, 6);
                        if (r4 > -1) {
                            robotGo(r4);
                        } else {
                            int r5 = howToWin(1, 4, 7);
                            if (r5 > -1) {
                                robotGo(r5);
                            } else {
                                int r6 = howToWin(2, 5, 8);
                                if (r6 > -1) {
                                    robotGo(r6);
                                } else {
                                    int r7 = howToWin(0, 4, 8);
                                    if (r7 > -1) {
                                        robotGo(r7);
                                    } else {
                                        int r8 = howToWin(2, 4, 6);
                                        if (r8 > -1) {
                                            robotGo(r8);
                                        } else {
                                            //cannot win, judge whether need to stop player
                                            int i1 = howToStop(0, 1, 2);
                                            if (i1> -1) {
                                                robotGo(i1);
                                            } else {
                                                int i2 = howToStop(3, 4, 5);
                                                if (i2 > -1) {
                                                    robotGo(i2);
                                                } else {
                                                    int i3 = howToStop(6, 7, 8);
                                                    if (i3 > -1) {
                                                        robotGo(i3);
                                                    } else {
                                                        int i4 = howToStop(0, 3, 6);
                                                        if (i4 > -1) {
                                                            robotGo(i4);
                                                        } else {
                                                            int i5 = howToStop(1, 4, 7);
                                                            if (i5 > -1) {
                                                                robotGo(i5);
                                                            } else {
                                                                int i6 = howToStop(2, 5, 8);
                                                                if (i6 > -1) {
                                                                    robotGo(i6);
                                                                } else {
                                                                    int i7 = howToStop(0, 4, 8);
                                                                    if (i7 > -1) {
                                                                        robotGo(i7);
                                                                    } else {
                                                                        int i8 = howToStop(2, 4, 6);
                                                                        if (i8 > -1) {
                                                                            robotGo(i8);
                                                                        } else {
                                                                            //no need to stop player
                                                                            howToGo();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        mSteps++;
        invalidate();
    }

    private void robotGo(int i) {
        tags[i] = _X;
    }

    //获取[a,b]内处c外随机数
    private int getRandom(int a, int b, int c){
        if(a>b)
            return -1;
        int d = (int)(Math.random()*(b-a)+a);
        if(d!=c){
            return d;
        }else{
            return getRandom(a,b,c);
        }
    }

    //如何连成线
    private int howToWin(int a, int b, int c){
        int taga = tags[a];
        int tagb = tags[b];
        int tagc = tags[c];

        int num = 0;
        int total = taga+tagb+tagc;

        if(taga==2){
            num++;
        }
        if(tagb==2){
            num++;
        }
        if(tagc==2){
            num++;
        }

        if(num==2 && total==4){
            if(taga==0){
                return a;
            }
            if(tagb==0){
                return b;
            }
            if(tagc==0){
                return c;
            }
        }
        return -1;
    }

    //O 是否将连成线
    private int howToStop(int a, int b, int c){//-1 -> no, [0,8] -> next step
        int taga = tags[a];
        int tagb = tags[b];
        int tagc = tags[c];

        int num = 0;
        int total = taga+tagb+tagc;
        if(taga==1){
            num++;
        }
        if(tagb==1){
            num++;
        }
        if(tagc==1){
            num++;
        }

        if(num==2 && total==2){
            if(taga==0){
                return a;
            }
            if(tagb==0){
                return b;
            }
            if(tagc==0){
                return c;
            }
        }
        return -1;
    }

    //目前无法成线，也不需阻拦对方
    private void howToGo(){
        int times = 0;
        while(true) {
            times++;
            int d = (int) (Math.random() * (8) );//[0-8]
            if (d == 0) {
                if (tags[d] == _BLANK) {
                    int t1_1 = getNumofThree(0, 1, 2);
                    int t1_2 = getNumofThree(0, 4, 8);
                    int t1_3 = getNumofThree(0, 3, 6);
                    if (t1_1 > 1 || t1_2 > 1 || t1_3 > 1) {
                        //----------------------
                    } else {
                        robotGo(d);
                        break;
                    }
                }
            } else if (d == 1) {
                if (tags[d] == _BLANK) {
                    int t2_1 = getNumofThree(0, 1, 2);
                    int t2_2 = getNumofThree(1, 4, 7);
                    if (t2_1 > 1 || t2_2 > 1) {
                        //----------------------
                    } else {
                        robotGo(d);
                        break;
                    }
                }
            } else if (d == 2) {
                if (tags[d] == _BLANK) {
                    int t3_1 = getNumofThree(0, 1, 2);
                    int t3_2 = getNumofThree(2, 5, 8);
                    int t3_3 = getNumofThree(2, 4, 6);
                    if (t3_1 > 1 || t3_2 > 1 || t3_3 > 1) {
                        //----------------------
                    } else {
                        robotGo(d);
                        break;
                    }
                }
            } else if (d == 3) {
                if (tags[d] == _BLANK) {
                    int t4_1 = getNumofThree(0, 3, 6);
                    int t4_2 = getNumofThree(3, 4, 5);
                    if (t4_1 > 1 || t4_2 > 1) {
                        //----------------------
                    } else {
                        robotGo(d);
                        break;
                    }
                }
            } else if (d == 4) {
                if (tags[d] == _BLANK) {
                    int t5_1 = getNumofThree(1, 4, 7);
                    int t5_2 = getNumofThree(3, 4, 5);
                    int t5_3 = getNumofThree(0, 4, 8);
                    int t5_4 = getNumofThree(2, 4, 6);
                    if (t5_1 > 1 || t5_2 > 1 || t5_3 > 1 || t5_4 > 1) {
                        //----------------------
                    } else {
                        robotGo(d);
                        break;
                    }
                }
            } else if (d == 5) {
                if (tags[d] == _BLANK) {
                    int t6_1 = getNumofThree(2, 5, 8);
                    int t6_2 = getNumofThree(3, 4, 5);
                    if (t6_1 > 1 || t6_2 > 1) {
                        //----------------------
                    } else {
                        robotGo(d);
                        break;
                    }
                }
            } else if (d == 6) {
                if (tags[d] == _BLANK) {
                    int t7_1 = getNumofThree(0, 3, 6);
                    int t7_2 = getNumofThree(6, 7, 8);
                    int t7_3 = getNumofThree(2, 4, 6);
                    if (t7_1 > 1 || t7_2 > 1 || t7_3 > 1) {
                        //----------------------
                    } else {
                        robotGo(d);
                        break;
                    }
                }
            } else if (d == 7) {
                if (tags[d] == _BLANK) {
                    int t8_1 = getNumofThree(1, 4, 7);
                    int t8_2 = getNumofThree(6, 7, 8);
                    if (t8_1 > 1 || t8_2 > 1) {
                        //----------------------
                    } else {
                        robotGo(d);
                        break;
                    }
                }
            } else if (d == 8) {
                if (tags[d] == _BLANK) {
                    int t9_1 = getNumofThree(0, 4, 8);
                    int t9_2 = getNumofThree(6, 7, 8);
                    int t9_3 = getNumofThree(2, 5, 8);
                    if (t9_1 > 1 || t9_2 > 1 || t9_3 > 1) {
                        //----------------------
                    } else {
                        robotGo(d);
                        break;
                    }
                }
            }

            if(times>9){
                goAnyway();
                break;
            }
        }
    }

    //按顺序，哪里空走哪
    private void goAnyway(){
        for(int i=0; i<tags.length; i++){
            if(tags[i]==_BLANK){
                robotGo(i);
                break;
            }
        }
    }

    private int getNumofThree(int a, int b, int c){
        int taga = tags[a];
        int tagb = tags[b];
        int tagc = tags[c];
        int num = 0;

        if(taga>0){
            num++;
        }
        if(tagb>0){
            num++;
        }
        if(tagc>0){
            num++;
        }
        return num;
    }

    public interface ticToeCallback{
        void gameOver(int a);

        void nextPlay(boolean isOGo);
    }

    ////////////////////////////////////////////////////////////////////
    public void setCallback(ticToeCallback callback) {
        this.callback = callback;
    }

    public void restartGame() {
        initTag();
        mSteps=0;
        isFreeze = false;
        isOGo = true;
        invalidate();
        if(!isAIMode){
            callback.nextPlay(isOGo);
        }
    }

    public void setAIMode(boolean AIMode) {
        isAIMode = AIMode;
    }
}