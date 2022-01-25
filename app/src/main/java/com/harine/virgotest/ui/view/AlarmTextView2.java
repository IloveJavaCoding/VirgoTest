package com.harine.virgotest.ui.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.harine.virgotest.Constants;
import com.harine.virgotest.data.DBHelper;
import com.harine.virgotest.data.bean.Program;
import com.harine.virgotest.data.bean.TextItem;
import com.harine.virgotest.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author nepalese on 2021/4/1 09:53
 * @usage
 */
public class AlarmTextView2 extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "AlarmTextView";
    private static final int MSG_NEXT_CYCLE = 1;//下一循环

    private Context mContext;
    private DBHelper dbHelper;

    // surface Handle onto a raw buffer that is being managed by the screen compositor.
    private SurfaceHolder surfaceHolder;   //providing access and control over this SurfaceView's underlying surface.

    private Paint paint = null;
    private boolean stopScroll = false;     // stop scroll
    private boolean pauseScroll = false;    // pause scroll

    private int speed = 4;                  // scroll-speed
    private String text = "";               // scroll text
    private float textSize = 25f;           // default text size

    private int textColor = Color.WHITE;
    private int textBackColor = Color.BLACK;
    private int needScrollTimes = Integer.MAX_VALUE;      //scroll times

    private int viewWidth = 0;
    private int viewHeight = 0;
    private float textWidth = 0f;
    private float textX = 0f;
    private float textY = 0f;
    private float viewWidth_plus_textLength = 0.0f;

    private ScheduledExecutorService scheduledExecutorService;

    boolean isSetNewText = false;
    boolean isScrollForever = true;

    private Program mProgram;//当前节目
    private int cyIndex;//循环播放索引
    private List<TextItem> itemList;
    private List<String> timeList;
    private boolean isRegister = false;

    /**
     * constructs 1
     * @param context you should know
     */
    public AlarmTextView2(Context context) {
        super(context);
        init(context);
    }

    /**
     * constructs 2
     * @param context CONTEXT
     * @param attrs   ATTRS
     */
    public AlarmTextView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        this.mContext = context;
        dbHelper = DBHelper.getInstance(mContext);
        registerReceiver();

        paint = new Paint();
        paint.setColor(textColor);
        paint.setTextSize(textSize);

        itemList = new ArrayList<>();
        timeList = new ArrayList<>();

        setZOrderOnTop(true);  //Control whether the surface view's surface is placed on top of its window.
        setFocusable(true);
        setTextBackgroundColor(textBackColor);
    }

    private void getTextItems(){
        itemList.clear();
        itemList.addAll(dbHelper.getTextItemPid(mProgram.getPId()));
    }

    //所有开始,结束时间
    private void getTimeList() {
        List<String> list = new ArrayList<>();
        for (TextItem item : itemList) {
            list.add(TimeUtil.formatTime(item.getStartTime()));
            list.add(TimeUtil.formatTime(item.getEndTime()));
        }
        timeList.clear();
        timeList.addAll(list);
    }

    private void setAlarm(){
        if(timeList.isEmpty()){
            Log.i(TAG, "setAlarm: empty");
            return;
        }

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Constants.ACTION_UPDATE_TIME_LIST_TEXT);
        long now = TimeUtil.getCurTimeTime();
        for(String time: timeList){
            long alarmTime = TimeUtil.string2LongTime(time, TimeUtil.DATE_FORMAT_TIME);

            if (alarmTime - now <= 0) {
                //已过或当前素材，不需要定时
                continue;
            }

            alarmTime = TimeUtil.getCurTime() + alarmTime - now;

            Log.i(TAG, "setAlarm: " + alarmTime);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
                    Constants.ALARM_REQUEST_CODE_TEXT, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            if(alarmManager!=null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
                }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
                }else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
                }
            }

            return;
        }
    }

    private void loadText(TextItem curItem) {
        if(curItem==null){
            //当前节目无素材播放
            playDefaultText();
        }else{

            //TODO 文本属性
//            setTextColor();
//            setTextSize();
//            setBackgroundColor();
//            setSpeed();
            setText(curItem.getContent());

            if(isCyclePlay()){
                handler.sendEmptyMessageDelayed(MSG_NEXT_CYCLE, curItem.getDuration()*1000L);
                cyIndex++;
            }
        }
    }

    private void playDefaultText() {
        Log.i(TAG, "playText: 垫片");
        //获取垫片节目内所有文本素材：拼接，无限循环
        List<TextItem> list = dbHelper.getTextItemDP();
        if(list.isEmpty()){
            //无垫片
            setText("");
        }else{
            StringBuilder builder = new StringBuilder();
            boolean isFirst = true;
            for(TextItem item: list){
                if(!isFirst){
                    builder.append("  ");
                }
                builder.append(item.getContent());
                isFirst = false;
            }
            setText(builder.toString());
        }
    }

    private TextItem getCurItem() {
        long now = TimeUtil.getCurTimeTime();
        for(TextItem item: itemList){
            if(now>=TimeUtil.string2LongTime(item.getStartTime(), TimeUtil.DATE_FORMAT_TIME) &&
                    now <TimeUtil.string2LongTime(item.getEndTime(), TimeUtil.DATE_FORMAT_TIME)){
                Log.i(TAG, "getCurItem: " + item.getResId());
                return item;
            }
        }
        return null;
    }

    private TextItem getCycleItem(){
        if(cyIndex>=itemList.size()){
            cyIndex = 0;
        }
        return itemList.get(cyIndex);
    }

    private boolean isCyclePlay(){
        return mProgram.getPType() == Constants.PROGRAM_TYPE_CYCLE || mProgram.getPType() == Constants.PROGRAM_TYPE_LOCATION;
    }

    ////////////////////////////////////////////api/////////////////////////////////////////////////
    public void setmProgram(Program mProgram) {
        this.mProgram = mProgram;
        if(mProgram.getPType()==Constants.PROGRAM_TYPE_DIANPIAN){
            return;
        }

        getTextItems();
        if(isCyclePlay()){
            //循环播放
            Log.i(TAG, "setProgram: 循环播放");
            timeList.clear();
            cyIndex = 0;
        }else{
            getTimeList();
        }
    }

    public void startPlay(){
        if(mProgram.getPType()==Constants.PROGRAM_TYPE_DIANPIAN){
            cancelAlarm();
            playDefaultText();
            return;
        }

        if(isCyclePlay()){
            loadText(getCycleItem());
        }else{
            setAlarm();
            loadText(getCurItem());
        }
    }

    ///////////////////////////////////////////定时广播/////////////////////////////////////////////
    private void registerReceiver() {
        if (!isRegister) {
            isRegister = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.ACTION_UPDATE_TIME_LIST_TEXT);
            mContext.registerReceiver(updateItem, filter);
        }
    }

    public void unRegisterReceiver() {
        if (isRegister) {
            isRegister = false;
            mContext.unregisterReceiver(updateItem);
        }
    }

    private final BroadcastReceiver updateItem = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: update text");
            String action = intent.getAction();
            if (Constants.ACTION_UPDATE_TIME_LIST_TEXT.equals(action)) {
                startPlay();
            }
        }
    };

    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(mContext,
                Constants.ALARM_REQUEST_CODE_TEXT,
                new Intent(Constants.ACTION_UPDATE_TIME_LIST_TEXT),
                PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_NEXT_CYCLE:
                    loadText(getCycleItem());
                    break;
            }
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////

//    /**
//     * measure text height width
//     * @param widthMeasureSpec  widthMeasureSpec
//     * @param heightMeasureSpec heightMeasureSpec
//     */
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        // TODO Auto-generated method stub
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        int mHeight = getFontHeight(textSize);      //实际的视图高
//        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
//        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
//
//        // when layout width or height is wrap_content ,should init ScrollTextView Width/Height
//        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
//            setMeasuredDimension(viewWidth, mHeight);
//            viewHeight = mHeight;
//        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
//            setMeasuredDimension(viewWidth, viewHeight);
//        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
//            setMeasuredDimension(viewWidth, mHeight);
//            viewHeight = mHeight;
//        }
//    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (surfaceHolder == null) {
            surfaceHolder = getHolder();
            surfaceHolder.removeCallback(this);
            surfaceHolder.addCallback(this);
            getHolder().setFormat(PixelFormat.TRANSLUCENT);
        }

        viewWidth = getWidth();
        viewHeight = getHeight();
    }

    /**
     * surfaceChanged
     * @param arg0 arg0
     * @param arg1 arg1
     * @param arg2 arg1
     * @param arg3 arg1
     */
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.d(TAG, "arg0:" + arg0.toString() + "  arg1:" + arg1 + "  arg2:" + arg2 + "  arg3:" + arg3);
    }

    /**
     * surfaceCreated,init a new scroll thread.
     * lockCanvas
     * Draw something
     * unlockCanvasAndPost
     * @param holder holder
     */

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        stopScroll = false;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new AlarmTextView2.ScrollTextThread(), 100, 100, TimeUnit.MILLISECONDS);
        Log.d(TAG, "ScrollTextTextView is created");
    }

    /**
     * surfaceDestroyed
     * @param arg0 SurfaceHolder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        stopScroll = true;
        scheduledExecutorService.shutdownNow();
        Log.d(TAG, "ScrollTextTextView is destroyed");
    }

    @Override
    protected void onDetachedFromWindow() {
        unRegisterReceiver();
        cancelAlarm();
        if (surfaceHolder != null) {
            surfaceHolder.removeCallback(this);
        }
        super.onDetachedFromWindow();
    }

    /**
     * text height
     * @param fontSize fontSize
     * @return fontSize`s height
     */
    private int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    /////////////////////////////////////////////////api///////////////////////////////////////////
    /**
     * set background color
     * @param color textBackColor
     */
    public void setTextBackgroundColor(int color){
        this.setBackgroundColor(color);
    }

    /**
     * get speed
     * @return speed
     */
    public int getSpeed() {
        return speed;
    }


    /**
     * set scroll times
     * @param times scroll times
     */
    public void setTimes(int times) {
        if (times <= 0) {
            throw new IllegalArgumentException("times was invalid integer, it must between > 0");
        } else {
            needScrollTimes = times;
            isScrollForever = false;
        }
    }

    /**
     * set scroll text size SP
     * @param textSizeTem scroll times
     */
    public void setTextSize(float textSizeTem) {
        if (textSize < 20) {
            throw new IllegalArgumentException("textSize must  > 20");
        } else if (textSize > 900) {
            throw new IllegalArgumentException("textSize must  < 900");
        } else {

            this.textSize=sp2px(getContext(), textSizeTem);
            //重新设置Size
            paint.setTextSize(textSize);
            //试图区域也要改变
            measureVarious();

            //实际的视图高,thanks to WG
            int mHeight = getFontHeight(textSizeTem);
            ViewGroup.LayoutParams lp = this.getLayoutParams();
            lp.width = viewWidth;
            lp.height = dip2px(this.getContext(), mHeight);
            this.setLayoutParams(lp);

            isSetNewText = true;
        }
    }

    /**
     * set scroll text
     * @param newText scroll text
     */
    public void setText(String newText) {
        isSetNewText = true;
        stopScroll = false;

        if (newText.equals("")) {
            setPauseScroll(true);
            this.text = newText;
            measureVarious();
            return;
        }

        setPauseScroll(false);
        float temp = paint.measureText(newText);
        Log.d(TAG, "setText: " + temp);
        if(temp<1920){
            StringBuilder builder = new StringBuilder(newText);
            do {
                builder.append("  ").append(newText);
                temp = paint.measureText(builder.toString());
            } while ((temp < 1920));

            newText = builder.toString();
        }
        this.text = newText + "  " +  newText;// " ● "
        measureVarious();
    }


    /**
     * Set the text color
     * @param color A color value in the form 0xAARRGGBB.
     */
    public void setTextColor(@ColorInt int color) {
        textColor = color;
        paint.setColor(textColor);
    }

    /**
     * set scroll speed
     * @param speed SCROLL SPEED [4,14] ///// 0?
     */
    public void setSpeed(int speed) {
        if (speed > 14 || speed < 4) {
            throw new IllegalArgumentException("Speed was invalid integer, it must between 4 and 14");
        } else {
            this.speed = speed;
        }
    }

    /**
     * scroll text forever
     * @param scrollForever scroll forever or not
     */
    public void setScrollForever(boolean scrollForever) {
        isScrollForever = scrollForever;
    }

    public void setPauseScroll(boolean pauseScroll) {
        this.pauseScroll = pauseScroll;
    }

    /**
     * dp to px
     * @param context c
     * @param dpValue dp
     * @return
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sp to px
     * @param context c
     * @param spValue sp
     * @return
     */
    private int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public  int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * Draw text
     * @param X X
     * @param Y Y
     */
    private synchronized void draw(float X, float Y) {
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawText(text, X, Y, paint);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != View.VISIBLE) {
            setPauseScroll(true);
        } else {
            setPauseScroll(false);
        }
    }

    /**
     * measure text
     */
    private void measureVarious() {
        textWidth = paint.measureText(text);
        viewWidth_plus_textLength = textWidth;//viewWidth + textWidth //单个循环宽度
        textX = - (viewWidth/2f);//viewWidth - viewWidth / 5 //初始位置

        //baseline measure !
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        textY = viewHeight / 2f + distance;
    }


    /**
     * Scroll thread
     */
    class ScrollTextThread implements Runnable {
        @Override
        public void run() {
            measureVarious();
            while (!stopScroll) {
                if (pauseScroll) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.toString());
                    }
                    continue;
                }

                draw(-textX, textY);//viewWidth - textX
                textX += speed;

                if (textX > viewWidth_plus_textLength/2) {
                    textX = 0;
                    --needScrollTimes;
                }

                if (needScrollTimes <= 0 && isScrollForever) {
                    stopScroll = true;
                }
            }
        }
    }
}
