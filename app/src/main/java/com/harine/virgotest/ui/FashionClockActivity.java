package com.harine.virgotest.ui;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.harine.virgotest.R;

public class FashionClockActivity extends AppCompatActivity {
    private static final String TAG = "FashionClockActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
//            setShowWhenLocked(true);//api 27后
//        }else{
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);//api 27前
//        }

        showWhenLock();
        setContentView(R.layout.activity_fashion_clock);
        Log.i(TAG, "onCreate: ");
        systemLock();
    }

    private void showWhenLock() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private void systemLock(){
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = km.newKeyguardLock("");
        keyguardLock.disableKeyguard();
    }
}