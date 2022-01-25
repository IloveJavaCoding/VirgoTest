package com.harine.virgotest.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.Nullable;

import com.harine.virgotest.R;
import com.nepalese.virgosdk.Util.BitmapUtil;
import com.nepalese.virgosdk.Util.FileUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nepalese on 2021/03/09 16:20
 * @usage 通过滚动画布实现歌词滚动
 */
public class VirgoLrcView extends View {
    private static final String TAG = "VirgoLrcView";
    private static final int MSG_TOUCH_BORDER = 1;//滑到底部或顶部
    private static final String DEFAULT_TEXT = "暂无歌词，快去下载吧！";

    private Context context;
    private LrcCallback callback;//滑动歌词时间线回调

    private Paint mainPaint;//当前歌词画笔
    private Paint secPaint;//其他歌词画笔
    private int viewWidth;//显示宽度
    private int viewHeight;//显示高度
    private float totalHeight;//歌词总高度

    private float textSizeMain;//选中字体大小
    private float textSizeSec;//其他字体大小
    private float textMainHeight;
    private float centerY;
    private int textColorMain;//选中字体颜色
    private int textColorSec;//其他字体颜色

    private int curLine = 0;//当前行数
    private float dividerHeight;//行间距
    private float padValue;//两边缩进值
    private long nextTime = 0;//下一行时间线

    private boolean isPlaying = false;
    private boolean isDown = false;//按压界面

    private Bitmap background;//背景图
    private OverScroller scroller;
    private final List<MyLrcLine> lineList = new LinkedList<>();

    public VirgoLrcView(Context context) {
        this(context, null);
    }

    public VirgoLrcView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoLrcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // 解析自定义属性
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.VirgoLrcView);
        textSizeMain = ta.getDimension(R.styleable.VirgoLrcView_vlrcTextSizeMain, 25.0f);
        textSizeSec = ta.getDimension(R.styleable.VirgoLrcView_vlrcTextSizeSec, 15.0f);
        textColorMain = ta.getColor(R.styleable.VirgoLrcView_vlrcTextColorMain, 0x66ccff);
        textColorSec = ta.getColor(R.styleable.VirgoLrcView_vlrcTextColorSec, 0x8a8a8a);

        dividerHeight = ta.getDimension(R.styleable.VirgoLrcView_vlrcDividerHeight, 15.0f);
        padValue = ta.getDimension(R.styleable.VirgoLrcView_vlrcpadValue, 25.0f);
        ta.recycle();
        // </end>

        // 初始化paint
        mainPaint = new Paint();
        mainPaint.setTextSize(textSizeMain);
        mainPaint.setColor(textColorMain);
        mainPaint.setAntiAlias(true);

        secPaint = new Paint();
        secPaint.setTextSize(textSizeSec);
        secPaint.setColor(textColorSec);
        secPaint.setAntiAlias(true);

        scroller = new OverScroller(context);

        Paint.FontMetrics fm = mainPaint.getFontMetrics();
        textMainHeight = (fm.descent - fm.ascent);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        viewWidth = getWidth();
        viewHeight = getHeight();
        //显示页内中心y轴坐标
        centerY = (viewHeight - textMainHeight) / 2.0f;

        calculateHeight();
        scaleBackground();
        super.onLayout(changed, left, top, right, bottom);
    }

    //计算歌词需要高度
    private void calculateHeight() {
        totalHeight = (textSizeSec+dividerHeight)*lineList.size();
    }

    private void scaleBackground(){
        if (background != null) {
            background = Bitmap.createScaledBitmap(background, viewWidth, viewHeight, true);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw background
        if (background != null) {
            canvas.drawBitmap(background, 0, 0, null);
        }else{
            canvas.drawColor(Color.TRANSPARENT);
        }

        //提示无歌词
        if (lineList.isEmpty()) {
            canvas.drawText(DEFAULT_TEXT, getStartX(DEFAULT_TEXT, mainPaint), centerY, mainPaint);
            return;
        }

        //画选择线
        if(isPlaying && isDown){
            float baseLine = centerY + getScrollY();
            canvas.drawLine(padValue, baseLine, viewWidth-padValue, baseLine, mainPaint);
        }

        for(int i=0; i<lineList.size(); i++){
            if(curLine==i){
                //选中行
                canvas.drawText(lineList.get(i).lrc, getStartX(lineList.get(i).lrc, mainPaint),
                        centerY + i * (textSizeSec+dividerHeight), mainPaint);
            }else{
                canvas.drawText(lineList.get(i).lrc, getStartX(lineList.get(i).lrc, secPaint),
                        centerY + i * (textSizeSec+dividerHeight), secPaint);
            }
        }

        super.onDraw(canvas);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    private float oldY, startY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float y;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isDown = true;
                oldY = event.getY();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "scrollY: " + getScrollY());
                if(getScrollY()>totalHeight || getScrollY()<(-viewHeight/3)){
                    handler.sendEmptyMessage(MSG_TOUCH_BORDER);
                    return true;
                }
                y = event.getY();
                if(Math.abs(y-startY) > 5){
                    scroller.startScroll(scroller.getFinalX(), scroller.getFinalY(), 0, (int) (startY-y));
                    invalidate();
                }

                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                isDown = false;
                if(isPlaying){
                    y = event.getY();
                    calculateCurLine(oldY-y);
                }
                break;
        }

        return true;
    }

    //计算滑动后当前居中的行
    private void calculateCurLine(float y) {
        float offLine = ((Math.abs(y)-textSizeSec) / (textSizeSec + dividerHeight)) * 1.0f;
        Log.i(TAG, "off line: " + offLine);
        if(y>0){
            //上划
            curLine = (int) (curLine + offLine + 1);
        }else{
            //下拉
            curLine = (int) (curLine - offLine - 1);
        }

        if(curLine>lineList.size()-1){
            curLine = lineList.size()-1;
        }else if(curLine<0){
            curLine = 0;
        }

        //跳到前面时需要
        if(y<0){
            nextTime = lineList.get(curLine+1).getTime();
        }

        //返回当前行对应的时间线
        if(curLine<lineList.size()-1){
            long time = lineList.get(curLine).getTime();
            callback.onRefresh(time);
        }
    }

    private int fourOutFiveIn(float a, boolean convert){
        int temp = (int) (a*10);
        int m = temp % 10;
        int out = temp/10;
        if(convert){
            if(m<5){
                return out+1;
            }else {
                return out;
            }
        }else{
            if(m>5){
                return out+1;
            }else {
                return out;
            }
        }
    }

    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == MSG_TOUCH_BORDER) {
                //回到当前行
                int curY = (int) (curLine * (textSizeSec + dividerHeight));
                scrollTo(0, curY);
                setScrollY(curY);
                scroller.abortAnimation();
                scroller = null;
                scroller = new OverScroller(context);
                invalidate();
            }
        }
    };

    //计算使文字居中
    private float getStartX(String str, Paint paint){
        return (viewWidth - paint.measureText(str)) / 2.0f;
    }

    private void reset(){
        lineList.clear();
        curLine = 0;
        nextTime = 0;
        if(scroller.isFinished()){
            scroller.abortAnimation();
        }
    }

    private void parseLrc(InputStreamReader inputStreamReader){
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line;
        try {
            while((line=reader.readLine())!=null){
                //deal with line
                parseLine(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long parseTime(String time) {
        // 00:01.10
        String[] min = time.split(":");
        String[] sec = min[1].split("\\.");

        long minInt = Long.parseLong(min[0].replaceAll("\\D+", "")
                .replaceAll("\r", "").replaceAll("\n", "").trim());
        long secInt = Long.parseLong(sec[0].replaceAll("\\D+", "")
                .replaceAll("\r", "").replaceAll("\n", "").trim());
        long milInt = Long.parseLong(sec[1].replaceAll("\\D+", "")
                .replaceAll("\r", "").replaceAll("\n", "").trim());

        return minInt * 60 * 1000 + secInt * 1000 + milInt;// * 10;
    }

    private void parseLine(String line) {
        Matcher matcher = Pattern.compile("\\[\\d.+].+").matcher(line);
        // 如果形如：[xxx]后面啥也没有的，则return空
        if (!matcher.matches()) {
            long time;
            String str;
            String con = line.replace("\\[", "").replace("\\]", "");
            Log.d(TAG, con);
            if(con.matches("^\\d.+")){//time
                time = parseTime(con);
                str = " ";
            }else{
                return;
            }
            lineList.add(new MyLrcLine(time, str));
            return;
        }

        //[00:23.24]让自己变得快乐
        line = line.replaceAll("\\[", "");
        String[] result = line.split("]");
        lineList.add(new MyLrcLine(parseTime(result[0]), result[1]));
    }

    /////////////////////////////////////////////api///////////////////////////////////
    public void setBackground(Bitmap background) {
        this.background = BitmapUtil.blurBitmap(background, 200);
    }

    public void loopMode(){
        curLine = 0;
        nextTime = 0;
        scrollTo(0,0);
        scroller.abortAnimation();
        scroller = null;
        scroller = new OverScroller(context);
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void setLrc(String lrc) {
        reset();
        if (TextUtils.isEmpty(lrc)) { return;}
        parseLrc(new InputStreamReader(new ByteArrayInputStream(lrc.getBytes())));
    }

    public void setLrcFile(String path){
        File file = new File(path);
        if(file.exists()){
            reset();
            String format;
            if(FileUtil.isUtf8(file)){
                format = "UTF-8";
            }else{
                format = "GBK";
            }

            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            InputStreamReader inputStreamReader = null;//'utf-8' 'GBK'
            try {
                inputStreamReader = new InputStreamReader(inputStream, format);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            parseLrc(inputStreamReader);
        }
    }

    public void seekTo(long time){
        if(time<lineList.get(curLine).getTime()){//往回跳
            //
        }else if(time<nextTime){
            return;
        }

        for(int i=0; i<lineList.size(); i++){
            if(i<lineList.size()-1){
                if(time>=lineList.get(i).getTime() && time<lineList.get(i+1).getTime()){
                    int temp = i - curLine;
                    curLine = i;
                    nextTime = lineList.get(i+1).getTime();
                    scroller.startScroll(scroller.getFinalX(), scroller.getFinalY(), 0,  (int)(textSizeSec+dividerHeight)*temp);
                    invalidate();
                    break;
                }
            }else{//last line
                int temp = i - curLine;
                curLine = i;
                nextTime = lineList.get(i).getTime() + 60000;
                scroller.startScroll(scroller.getFinalX(), scroller.getFinalY(), 0,  (int)(textSizeSec+dividerHeight)*temp);
                invalidate();
            }
        }
    }

    public void setCallback(LrcCallback callback) {
        this.callback = callback;
    }

    //////////////////////////////////////////////////////////////////////////////////////
    public interface LrcCallback{
        void onRefresh(long time);
    }
    static class MyLrcLine {
        private long time;
        private String lrc;

        public MyLrcLine(long time, String lrc) {
            this.time = time;
            this.lrc = lrc;
        }

        public MyLrcLine() {
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getLrc() {
            return lrc;
        }

        public void setLrc(String lrc) {
            this.lrc = lrc;
        }
    }
}