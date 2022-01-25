package com.harine.virgotest.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.harine.virgotest.R;

import java.io.IOException;

public class VideoActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private static final String TAG = "VideoActivity";

    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;
//    private final String url = "udp://224.0.0.4:10001";
    private final String url = "http://117.25.163.26:9990/cdmsa/2021/09/01/00ddd3326fdde5bf.mp4";
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        init();
    }

    private void init() {
        this.mMediaPlayer = new MediaPlayer();

        mSurfaceView = findViewById(R.id.surfaceView);
        mSurfaceView.getHolder().addCallback(this);

        input = findViewById(R.id.etInputVideo);
    }

    private void doPlayerStart(String url) {
        if (!TextUtils.isEmpty(url)) {
            try {
                this.mMediaPlayer.reset();
                this.mMediaPlayer.setDataSource(url);
                this.mMediaPlayer.setOnPreparedListener(paramAnonymousMediaPlayer -> {
                    Log.i(TAG, "onPrepared");
                    mMediaPlayer.start();
                });
                this.mMediaPlayer.prepareAsync();
            } catch (IOException paramMessage) {
                paramMessage.printStackTrace(   );
            }
            return;
        }
        Log.e(TAG, "url is null");
    }

    public void onPlay(View view){
        String temp = input.getText().toString().trim();
        if(TextUtils.isEmpty(temp)){
            temp = url;
        }

        doPlayerStart(temp);
    }

    protected void onDestroy() {
        super.onDestroy();
        MediaPlayer localMediaPlayer = this.mMediaPlayer;
        if (localMediaPlayer != null) {
            localMediaPlayer.release();
        }
    }

    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3) {

    }

    public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
        this.mMediaPlayer.setDisplay(paramSurfaceHolder);
//        doPlayerStart(url);
    }

    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {

    }
}