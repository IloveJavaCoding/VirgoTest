package com.harine.virgotest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.harine.virgotest.ui.FashionClockActivity;

/**
 * @author nepalese on 2021/4/25 10:45
 * @usage
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    private static final int MSG_SHOW_LOCK = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(TextUtils.isEmpty(action)) return;

        Log.i(TAG, "onReceive: " + action);
        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            handler.sendMessage(handler.obtainMessage(MSG_SHOW_LOCK, context));
        }
    }


    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SHOW_LOCK:
                    showLock((Context) msg.obj);
                    break;
            }
        }
    };

    private void showLock(Context context) {
        Intent startIntent = new Intent(context, FashionClockActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(startIntent);
    }
}
