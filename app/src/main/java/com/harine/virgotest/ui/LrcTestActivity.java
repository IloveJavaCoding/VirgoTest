package com.harine.virgotest.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.harine.virgotest.R;
import com.harine.virgotest.component.VirgoLrcView;
import com.nepalese.virgosdk.Util.FileUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LrcTestActivity extends AppCompatActivity implements VirgoLrcView.LrcCallback {
    private static final String TAG = "LrcTestActivity";

    private VirgoLrcView lrcView;
    private int curTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrc_test);

        init();
        setData();
        setListener();
    }

    private void init() {
        lrcView = findViewById(R.id.lrcView);
        lrcView.setCallback(this);
    }

    private void setData() {
        lrcView.setLrc(FileUtil.readResource2String(getApplicationContext(),
                R.raw.shaonian, "utf-8"));
        lrcView.seekTo(0);
    }

    private void setListener() {
    }

    public void onStartPlaying(View view){
        startTask();
    }

    public void onStopPlaying(View view){
        stopTask();
    }

    @Override
    public void onRefresh(long time) {
        Log.i(TAG, "onRefresh: " + time);
        curTime = (int) (time/1000);
        lrcView.seekTo(curTime*1000);
    }

    private final Runnable timeTisk = new Runnable() {
        @Override
        public void run() {
            curTime ++;
            lrcView.seekTo(curTime*1000);
            handler.postDelayed(timeTisk, 1000);
        }
    };

    private void startTask(){
        stopTask();
        handler.post(timeTisk);
        lrcView.setPlaying(true);
    }

    private void stopTask(){
        handler.removeCallbacks(timeTisk);
        lrcView.setPlaying(false);
    }

    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTask();
    }
}