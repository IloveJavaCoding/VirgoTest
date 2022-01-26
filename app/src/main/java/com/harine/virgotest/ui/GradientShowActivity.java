package com.harine.virgotest.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.harine.virgotest.R;
import com.harine.virgotest.bean.WeatherInfo;
import com.harine.virgotest.component.VirgoGradientView;
import com.harine.virgotest.component.VirgoWeatherView;
import com.harine.virgotest.component.clock.VirgoDigitalClockView;
import com.harine.virgotest.presenter.VirgoPresenter;
import com.harine.virgotest.util.GsonUtil;
import com.harine.virgotest.util.TempUtil;
import com.nepalese.virgosdk.Util.JsonUtil;

public class GradientShowActivity extends AppCompatActivity implements VirgoPresenter.httpCallBack{
    private static final String TAG = "GradientShowActivity";
    private static final int MSG_WEATHER = 1;

    private VirgoGradientView gradientView;
    private VirgoWeatherView weatherView;
    private VirgoPresenter presenter;

    private VirgoDigitalClockView clockView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradient_show);

        TempUtil.setFullScreen(this);
        init();
        setData();
    }

    private void init(){
        presenter = new VirgoPresenter(this);
        presenter.setCallBack(this);


        gradientView = findViewById(R.id.gradientView);
        weatherView = findViewById(R.id.weatherView);
        clockView = findViewById(R.id.digitClock);
    }

    private void setData() {
        int[] colors = new int[4];
        colors[0] = Color.parseColor("#FFDDBB");
        colors[1] = Color.parseColor("#FFC0CD");
        colors[2] = Color.parseColor("#B5BBFF");
        colors[3] = Color.parseColor("#AADCFF");
        gradientView.setmColors(colors);
        
        getWeather();

        clockView.setDigitColor(Color.GREEN);
    }

    private void getWeather() {
        presenter.getWeather("118.004", "24.5555");
    }


    @Override
    public void onSuccess(String tag, String json) {
        if (VirgoPresenter.TAG_WEATHER.equals(tag)){
            Log.i(TAG, "onSuccess: " + json);
            handler.sendMessage(handler.obtainMessage(MSG_WEATHER, json));
        }
    }

    @Override
    public void onFail(String tag, String error) {
        Log.i(TAG, "onFail: " + error);
    }

    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_WEATHER:
                    String response = (String) msg.obj;
                    WeatherInfo weatherInfo = (WeatherInfo) GsonUtil.getObject(response,WeatherInfo.class);
                    weatherView.setWeatherInfo(weatherInfo);
                    break;
            }
        }
    };
}