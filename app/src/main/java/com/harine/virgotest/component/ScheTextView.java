package com.harine.virgotest.component;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by Administrator on 2021/8/11.
 * Usage: 排期文本素材播放器
 */

public class ScheTextView extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG = "ScheTextView";
    public static final int MAX_SPEED = 50;
    public static final int MIN_SPEED = 0;
    
    private Paint paint = null;// 画笔
    private Rect rect;
    private SurfaceHolder holder;
    private Runnable textRun;
    private Thread mScheduledThread; //滚动线程

    private final LinkedList<MarqueeBean> txtCacheList = new LinkedList<>(); // 按每屏长的文字，缓存到列表
    private String oldStr = "";//缓存文字，作为比对使用
    private String newStr = "";//缓存文字，作为比对使用

    private int textColor = Color.BLACK; // 文字颜色
    private int bgColor = Color.WHITE; // 背景颜色

    private int mWidth, mHeight;//布局宽度
    private boolean needReload = false;
    private float speed = 2; // 滚动速度
    private float textSize = 25f; // 字号
    private float simpleHeight; //单文字高度
    private float totalLength = 0.0f;// 显示总长度

    private float mLoc_X_1 = 0;//第一屏的x坐标
    private float mLoc_Y_1 = 0;//第一屏的y坐标
    private float offsetDis = 0;//偏移量,以速度为基准

    private int mIndex_1 = 0;//第一屏的文字角标
    private int mIndex_2 = 1;//第二屏的文字角标
    private int mIndex_3 = 2;//第三屏的文字角标

    private boolean isRolling = false;//是否在滚动
    private boolean isInterrupted = true;//是否停止线程循环
    private static boolean needScroll = false;//是否需要滚动，单屏文本不滚动

    /////////////////////////////////////////////////////////
    public ScheTextView(Context context) {
        super(context);
        init();
    }

    public ScheTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(textColor);
        paint.setTextSize(textSize);

        setSpeed(speed);
        setText("");
    }


    public void startPlay(String str) {
        Log.d(TAG, "startPlay ");
        setText(str);
    }

    public void pausePlay() {
        Log.d(TAG, "pausePlay ");
        stopRolling();
    }

    public void continuePlay() {
        Log.d(TAG, "continuePlay ");
        //继续播放
        startRolling();
    }

    public void release() {
        if (holder != null) {
            holder.removeCallback(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        release();
        super.onDetachedFromWindow();
    }

    //////////////////////////////////////handler/////////////////////////////////////////////////
    private static final int MSG_ADD_TEXT = 10;
    private static final int MSG_SET_TEXT = 11;

    ////////////////////////////////////////文本处理////////////////////////////////////////////////
    // 获取字体宽
    private float getFontWith(String txt) {
        return paint.measureText(txt);
    }

    private float getContentWidth(String black) {
        if (black == null || black.length() == 0) {
            return 0;
        }
        if (rect == null) {
            rect = new Rect();
        }
        paint.getTextBounds(black, 0, black.length(), rect);
        return rect.width();
    }

    private float getBlackWidth() {
        String text1 = "en en";
        String text2 = "enen";
        return getContentWidth(text1) - getContentWidth(text2);
    }

    /**
     * 重置参数
     */
    private void reset() {
        if (txtCacheList.size() <= 0) return;

        mIndex_1 = 0;//第一屏的文字角标
        simpleHeight = FormatTextTask.getFontHeight(textSize);

        //fixme 这边先不考虑内边距
        // 水平滚动
        totalLength = mWidth;//getWidth();

        //定高
        mLoc_Y_1 = mHeight/ 2f + simpleHeight / 3;//getHeight()

        paint.setTextAlign(Paint.Align.LEFT);
        mLoc_X_1 = mWidth;//getWidth();//第一屏的坐标  / 2f ： 改为从最右点开始

        //不少于两屏
        mIndex_2 = txtCacheList.size() > 1 ? 1 : 0;//第二屏的文字角标
        mIndex_3 = mIndex_2 + 1 < txtCacheList.size() ? mIndex_2 + 1 : 0;//第三屏的文字角标
    }

    /**
     * 滚动任务
     */
    private final Runnable mScheduledRun = new Runnable() {
        @Override
        public void run() {
            while (!isInterrupted) {
                synchronized (txtCacheList) {
                    if (txtCacheList.size() <= 0 ||/*speed <= 0 ||*/ getVisibility() != View.VISIBLE) {
                        //小于一屏或者滚动速度为0，那么中断滚动
                        Log.i(TAG, "run: 小于一屏或者滚动速度为0，那么中断滚动");
                        stopRolling();
                        break;
                    }else if(!needScroll){//txtCacheList.size() <= 1
                        //仅一屏
                        try {
                            Thread.sleep(40);
                            onDrawUI2();
                        } catch (Throwable ignored) {
                        }
                        break;
                    }
                }

                if (!isRolling) {
                    isRolling = true;
                }
                try {
                    Thread.sleep(40);
                    onDrawUI();
                } catch (Throwable ignored) {
                }
            }
            isRolling = false;
        }
    };

    /// 绘制文字
    public void onDrawUI() {
        if (txtCacheList.size() > 0 && holder != null) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(bgColor);

            //水平滚动，往左
            int size = txtCacheList.size();
            if (txtCacheList.size() > 0) {
                mLoc_X_1 = mLoc_X_1 - offsetDis;

                //  类似传送带方式的移动
                MarqueeBean bean1 = txtCacheList.get(mIndex_1 % size);
                String str1 = bean1.getMsg();
                MarqueeBean bean2 = txtCacheList.get(mIndex_2 % size);
                String str2 = bean2.getMsg();
                MarqueeBean bean3 = txtCacheList.get(mIndex_3 % size);
                String str3 = bean3.getMsg();

                float mX_2 = bean1.getLen() + mLoc_X_1;
                float mX_3 = bean2.getLen() + mX_2;
                canvas.drawText(str1, mLoc_X_1, mLoc_Y_1, paint);
                canvas.drawText(str2, mX_2, mLoc_Y_1, paint);
                canvas.drawText(str3, mX_3, mLoc_Y_1, paint);

                if (mX_2 < 0) {
                    // 变化游标
                    mIndex_1 = mIndex_2;
                    mIndex_2 = mIndex_3;
                    mIndex_3 = (mIndex_2 + 1) % txtCacheList.size();
                    // 变化坐标
                    mLoc_X_1 = mX_2;
                }
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

    //仅一屏，居中显示
    private void onDrawUI2() {
        if (txtCacheList.size() > 0 && holder != null) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(bgColor);

            MarqueeBean bean = txtCacheList.get(0);
            String str = bean.getMsg();

            float startX = (mWidth - getFontWith(str))/2f;
            canvas.drawText(str, startX, mLoc_Y_1, paint);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * 意图事件处理
     */
    private final Handler mEventHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_ADD_TEXT) {
                Bundle data = msg.getData();
                if (data != null) {
                    ArrayList<MarqueeBean> list = data.getParcelableArrayList("data");
                    Log.d(TAG, "收到数据 == " + (list == null ? null : list.size()));
                    if (list != null) {
                        txtCacheList.addAll(list);
                        reset();
                        startRolling();
                    }
                }
            } else if (msg.what == MSG_SET_TEXT) {
                try {
                    if (holder != null) {
                        //初始化背景色
                        Canvas canvas = holder.lockCanvas();
                        if (canvas != null) {
                            canvas.drawColor(bgColor);
                            holder.unlockCanvasAndPost(canvas);
                        }
                    }
                } catch (Throwable ignored) {}

                reset();

                if (txtCacheList.size() == 0 && !TextUtils.isEmpty(oldStr)) {
                    //先停止滚动，然后才能设置文字
                    if (totalLength <= 0) {
                        totalLength = mWidth;//getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
                    }
                    Log.v(TAG, "onlayout 总长 = " + totalLength + " 字 = " + oldStr.length());
                    new FormatTextTask(mEventHandler, totalLength, textSize).execute(oldStr);
                }
            }
            return false;
        }
    });

    private static int dp2px(Resources res, float dpValue) {
        if (res == null) return -1;
        final float scale = res.getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //////////////////////////////////////over ride////////////////////////////////////////////////
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();

        if(needReload){
            needReload=false;
            postTextRun(newStr);
        }

        if (holder == null) {
            holder = getHolder();
            holder.removeCallback(this);
            holder.addCallback(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startRolling();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopRolling();
    }

    ///////////////////////////////////// out public method //////////////////////////////////////
    /**
     * 设置文字
     * @param s 文字
     */
    public void setText(final String s) {
        //先停止滚动，然后才能设置文字
        stopRolling();
        txtCacheList.clear();
        if (textRun != null) {
            removeCallbacks(textRun);
        }
        if (TextUtils.isEmpty(s)) {
            oldStr = null;
            return;
        }
        if(mWidth<=0){
            needReload = true;
            newStr = s;
            return;
        }
        postTextRun(s);
    }

    private void postTextRun(final String s) {
        post(textRun = new Runnable() {
            @Override
            public void run() {
                //取消自动补齐
                oldStr = s;
                mEventHandler.removeMessages(MSG_SET_TEXT);
                mEventHandler.sendEmptyMessageDelayed(MSG_SET_TEXT, 500);
            }
        });
    }

    /**
     * 设置字体大小
     * @param textSize 文字大小
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        paint.setTextSize(textSize);
    }

    /**
     * 设置文字颜色
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        paint.setColor(textColor);
    }

    /**
     * 设置背景颜色
     * @param color 背景颜色
     */
    @Override
    public void setBackgroundColor(int color) {
//        super.setBackgroundColor(color);
        this.bgColor = color;
    }

    /**
     * 设置滚动方向
     * @param orizontal 垂直{@link LinearLayout#VERTICAL}或者水平{@link LinearLayout#HORIZONTAL}
     */
    public void setOrizontal(int orizontal) {
//        if (this.orizontal != orizontal) {
//            stopRolling();
//            this.orizontal = orizontal;
//            oldStr = null;
//            reset();
//        }
    }

    /**
     * 设置滚动速度
     * @param speed
     */
    public void setSpeed(float speed) {
        if (speed > MAX_SPEED || speed < MIN_SPEED) {
            throw new IllegalArgumentException(
                    String.format(Locale.getDefault(),
                            "Speed was invalid integer, it must between %d and %d", MIN_SPEED, MAX_SPEED));
        } else {
            this.speed = speed;
            offsetDis = speed * 2;
        }
    }

    /**
     * 开始滚动
     */
    private void startRolling() {
        try {
            if (txtCacheList.size() < 1) {
                //如果文字不够一屏，不移动
                return;
            }
            if (getVisibility() != View.VISIBLE) {
                //如果不显示，就不滚动
                return;
            }
            Log.d(TAG, "startRolling -------- ");
            if (mScheduledThread == null) {
                mScheduledThread = new Thread(mScheduledRun, "schedule");
                isInterrupted = false;
                mScheduledThread.start();
            }
        } catch (Throwable e) {
            Log.e(TAG, "start rolling error", e);
        }
    }

    /**
     * 停止滚动
     */
    public void stopRolling() {
        try {
            Log.d(TAG, "stopRolling -------- ");
            if (mScheduledThread != null) {
                isInterrupted = true;
                mScheduledThread.interrupt();
                mScheduledThread = null;
            }
        } catch (Throwable e) {
            Log.e(TAG, "stop rolling error", e);
        }
    }

    /**
     * 销毁滚动
     */
    public void destroyRolling() {
        Log.d(TAG, "destroyRolling -------- ");
        try {
            startRolling();
            txtCacheList.clear();
        } catch (Throwable e) {
            Log.e(TAG, "destroy rolling error", e);
        }
    }

    //////////////////////////////////////////////////////////////////
    /**
     * 格式化文字任务
     */
    static class FormatTextTask extends AsyncTask<String, Void, ArrayList<MarqueeBean>> {
        //控件对应一屏的长度，如果是水平滚动，那么就是一屏宽度，如果是垂直滚动，就是一屏高度,必须有确切的宽或高
        private final float contentLength;
        private final float textSize;//字体大小
        private final Handler mEventHandler;

        public FormatTextTask(Handler mEventHandler, float contentLength, float textSize) {
            this.mEventHandler = mEventHandler;
            this.contentLength = contentLength;
            this.textSize = textSize;
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param strings The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected ArrayList<MarqueeBean> doInBackground(String... strings) {
            if (strings == null || strings.length <= 0) {
                return null;
            }
//            Log.v(TAG, "滚动方向的长度 = " + contentLength);
            if (contentLength <= 0) {
                //必须有确切的宽或高
                return null;
            }
            String str = strings[0]; // 需要格式的文字
            if (str == null || str.length() == 0) {
                return null;
            }
            String formatStr;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    formatStr = Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY).toString();
                } else {
                    formatStr = Html.fromHtml(str).toString();
                }
            } catch (Throwable e) {
                Log.e(TAG, "字符无法转编码", e);
                formatStr = str;
            }

            ArrayList<MarqueeBean> list = new ArrayList<>();
            TextPaint paint = new TextPaint();
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(textSize);

//            float textWidth = getTextWidth(paint, formatStr);
            float textWidth = Layout.getDesiredWidth(formatStr, paint);
            Log.w(TAG, "文本总宽： " + textWidth);
            if(textWidth<contentLength){
                //如果总文本长度小于显示宽度，无需滚动，无需分屏
                needScroll = false;
                MarqueeBean bean = new MarqueeBean(formatStr, textWidth);
                list.add(bean);
            }else{
                needScroll = true;
                int start = 0, len = 100;
                do {
                    int end = (start + len);
                    if (end > formatStr.length()) {
                        end = formatStr.length();
                    }
                    String cacheStr = formatStr.substring(start, end);
                    float len1 = Layout.getDesiredWidth(cacheStr, 0, cacheStr.length(), paint);
                    MarqueeBean bean = new MarqueeBean(cacheStr, len1);
                    list.add(bean);
                    start = end;
                } while (start < formatStr.length());
            }

            Log.d(TAG, "是否需要分屏： " + needScroll +", 拆分的字符： " + list.size());
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<MarqueeBean> list) {
            if (mEventHandler != null) {
                Message message = mEventHandler.obtainMessage(MSG_ADD_TEXT);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("data", list);
                message.setData(bundle);
                mEventHandler.sendMessage(message);
            }
        }

        public static float getTextWidth(Paint mPaint, String str) {
            float iSum = 0;
            if (str != null && !str.equals("")) {
                int len = str.length();
                float[] widths = new float[len];
                mPaint.getTextWidths(str, widths);
                for (int i = 0; i < len; i++) {
                    iSum += Math.ceil(widths[i]);
                }
            }
            return iSum;
        }

        //获取字体宽度
        private static float getFontWidth(float fontSize, String black) {
            if (black == null || black.length() == 0) {
                return 0;
            }
            Rect rect = new Rect();
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(fontSize);
            paint.getTextBounds(black, 0, black.length(), rect);
            return rect.width();
        }

        // 获取字体高度
        private static float getFontHeight(float fontSize) {
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(fontSize);
            Paint.FontMetrics fm = paint.getFontMetrics();
            return (float) Math.ceil(fm.descent - fm.ascent);
        }
    }

    static class MarqueeBean implements Parcelable {
        private String msg;
        private float len;

        public MarqueeBean(String msg, float len) {
            this.msg = msg;
            this.len = len;
        }

        protected MarqueeBean(Parcel in) {
            msg = in.readString();
            len = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(msg);
            dest.writeFloat(len);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<MarqueeBean> CREATOR = new Creator<MarqueeBean>() {
            @Override
            public MarqueeBean createFromParcel(Parcel in) {
                return new MarqueeBean(in);
            }

            @Override
            public MarqueeBean[] newArray(int size) {
                return new MarqueeBean[size];
            }
        };

        public String getMsg() {
            return msg;
        }

        public float getLen() {
            return len;
        }

        @Override
        public String toString() {
            return "{ " +
                    " \"clzName\": \"MarqueeBean\"" +
                    ", \"msg\": \"" + msg + '\"' +
                    ", \"len\": " + len +
                    '}';
        }
    }
}
