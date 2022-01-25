package com.harine.virgotest.component.clock;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.harine.virgotest.R;

import java.util.Calendar;

/**
 * @author nepalese on 2021/3/19 13:59
 * @usage
 */
public class VirgoDigitalClockView extends LinearLayout {
    private static final String TAG = "VirgoDigitalClockView";
    private static final long INTERVAL_UPDATE = 3*1000L;

    private VirgoElecDigitalView digitalH1, digitalH2;
    private VirgoElecDigitalView digitalM1, digitalM2;
    private VirgoElectDotView digitalDot;
    private LinearLayout layoutAll;

    public VirgoDigitalClockView(Context context) {
        this(context, null);
    }

    public VirgoDigitalClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoDigitalClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_digit_clock, this, true);
        init();
    }

    private void init() {
        layoutAll = findViewById(R.id.layoutAll);
        digitalH1 = findViewById(R.id.digitH1);
        digitalH2 = findViewById(R.id.digitH2);
        digitalM1 = findViewById(R.id.digitM1);
        digitalM2 = findViewById(R.id.digitM2);
        digitalDot = findViewById(R.id.dot);

        startTask();
    }

    private void getTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        digitalH1.setmNum(hour/10);
        digitalH2.setmNum(hour%10);
        digitalM1.setmNum(min/10);
        digitalM2.setmNum(min%10);
    }

    private void startTask(){
        stopTask();
        handler.post(updateTask);
    }

    private void stopTask() {
        handler.removeCallbacks(updateTask);
    }

    private final Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            getTime();
            handler.postDelayed(updateTask, INTERVAL_UPDATE);
        }
    };

    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTask();
    }

    public void setDigitColor(int digitColor) {
        digitalH1.setmColor(digitColor);
        digitalH2.setmColor(digitColor);
        digitalM1.setmColor(digitColor);
        digitalM2.setmColor(digitColor);
        digitalDot.setmColor(digitColor);
    }

    public void setBgColor(int bgColor) {
        layoutAll.setBackgroundColor(bgColor);
        digitalH1.setmBgColor(bgColor);
        digitalH2.setmBgColor(bgColor);
        digitalM1.setmBgColor(bgColor);
        digitalM2.setmBgColor(bgColor);
        digitalDot.setmBgColor(bgColor);
    }

    public void setTime(int hour, int min){
        digitalH1.setmNum(hour/10);
        digitalH2.setmNum(hour%10);
        digitalM1.setmNum(min/10);
        digitalM2.setmNum(min%10);
    }
}