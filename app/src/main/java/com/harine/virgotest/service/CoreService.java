package com.harine.virgotest.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.harine.virgotest.ui.FashionClockActivity;

/**
 * @author nepalese on 2021/4/25 11:04
 * @usage
 */
public class CoreService extends Service {
    private static final String TAG = "CoreService";
    private static final int MSG_SCREEN_ON = 1;
    private static final int MSG_SCREEN_OFF = 2;
    private static final int MSG_UN_LOCKED = 3;

    private boolean isRegister = false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        registerReceiver();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
//        unRegistertReceiver();
        super.onDestroy();
    }

    ////////////////////////////////////屏幕开关监听///////////////////////////////
    private void registerReceiver(){
        if(!isRegister){
            Log.i(TAG, "registerReceiver: ");
            isRegister = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            registerReceiver(receiver, filter);
        }
    }

    private void unRegistertReceiver(){
        Log.i(TAG, "unRegistertReceiver: ");
        if(isRegister){
            isRegister = false;
            unregisterReceiver(receiver);
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(TextUtils.isEmpty(action)) return;

            Log.i(TAG, "onReceive: " + action);
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                handler.sendEmptyMessage(MSG_SCREEN_ON);
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                handler.sendEmptyMessage(MSG_SCREEN_OFF);
            }else if(Intent.ACTION_USER_PRESENT.equals(action)){
                handler.sendEmptyMessage(MSG_UN_LOCKED);
            }
        }
    };

    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SCREEN_ON:
//                    closeLock();
                    break;
                case MSG_SCREEN_OFF:
//                    selfCall();
//                    showLock();
                    break;
                case MSG_UN_LOCKED:
                    Log.i(TAG, "解锁: ");
                    break;
            }
        }
    };

    private void closeLock() {
    }

    private void showLock() {
        Log.i(TAG, "showLock: ");
        Intent intent = new Intent(getApplicationContext(), FashionClockActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
//        ComponentName cn = new ComponentName("com.harine.virgotest", "com.harine.virgotest.ui.FashionClockActivity");
//        intent.setComponent(cn);
        startActivity(intent);
    }

    private void selfCall(){
        startService(new Intent(this, CoreService.class));
    }
}
