package com.harine.virgotest.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.harine.virgotest.R;
import com.harine.virgotest.util.TimeUtil;

public class NetListener extends AppCompatActivity {

    private final StringBuilder log = new StringBuilder();
    private TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_listener);
        tvLog = findViewById(R.id.tvLog);

        initReceiver();
    }

    /**
     * 注册网络监听的广播
     */
    private void initReceiver() {
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        timeFilter.addAction("android.net.ethernet.STATE_CHANGE");//有线网络
        timeFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGED");
        timeFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        timeFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(netReceiver, timeFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (netReceiver != null) {
            unregisterReceiver(netReceiver);
            netReceiver = null;
        }
    }

    private BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            addLog("收到通知：" + action);
            switch (action){
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isAvailable()) {
                        int type2 = networkInfo.getType();
                        addLog("网络信息：" + networkInfo.toString());
                        switch (type2) {
                            case 0://移动 网络    2G 3G 4G 都是一样的 实测 mix2s 联通卡
                                addLog("移动!");
                                break;
                            case 1: //wifi网络
                                addLog("wifi!");
                                break;
                            case 9:  //网线连接
                                addLog("有线!");
                                break;
                        }
                    } else {// 无网络
                        addLog("无网络!");
                    }
                    break;
                case "android.net.ethernet.STATE_CHANGE":

                    break;
                case "android.net.ethernet.ETHERNET_STATE_CHANGED":

                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:

                    break;
                case WifiManager.WIFI_STATE_CHANGED_ACTION:

                    break;
            }
            tvLog.setText(log.toString());
        }
    };

    private void addLog(String str) {
        log.append(TimeUtil.getCurDate(TimeUtil.DATE_FORMAT_TIME)).append(": ").append(str).append("\n");
    }
}