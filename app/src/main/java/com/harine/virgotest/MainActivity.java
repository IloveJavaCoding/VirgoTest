package com.harine.virgotest;

import android.os.Bundle;
import android.view.View;

import com.harine.virgotest.data.DBHelper;
import com.harine.virgotest.ui.AlarmProgramActivity;
import com.harine.virgotest.ui.BlurImageActivity;
import com.harine.virgotest.ui.ComponentActivity;
import com.harine.virgotest.ui.DisplayControlActivity;
import com.harine.virgotest.ui.FashionClockActivity;
import com.harine.virgotest.ui.FileTestActivity;
import com.harine.virgotest.ui.GradientShowActivity;
import com.harine.virgotest.ui.ImageAnimationActivity;
import com.harine.virgotest.ui.ImageMagicActivity;
import com.harine.virgotest.ui.LrcTestActivity;
import com.harine.virgotest.ui.MultiSocketActivity;
import com.harine.virgotest.ui.NetListener;
import com.harine.virgotest.ui.PermissionTestActivity;
import com.harine.virgotest.ui.ScrollTextActivity;
import com.harine.virgotest.ui.ServerClientActivity;
import com.harine.virgotest.ui.SocketPlayActivity;
import com.harine.virgotest.ui.Text2SpeechActivity;
import com.harine.virgotest.ui.VideoActivity;
import com.harine.virgotest.ui.WeatherDemoActivity;
import com.harine.virgotest.util.TempUtil;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity  {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        TempUtil.setSNHide(this);
        TempUtil.setStatusLight(this);
    }

    public void onComponent(View view){
        TempUtil.jumpto(this, ComponentActivity.class);
    }

    public void onSocket(View view){
        TempUtil.jumpto(this, MultiSocketActivity.class);
    }

    public void onVideo(View view){
        TempUtil.jumpto(this, VideoActivity.class);
    }

    public void onSocketPlay(View view){
        TempUtil.jumpto(this, SocketPlayActivity.class);
    }

    public void onImageMagic(View view){
        TempUtil.jumpto(this, ImageMagicActivity.class);
    }

    public void onCommunication(View view){ TempUtil.jumpto(this, ServerClientActivity.class); }

    public void onScreen(View view){ TempUtil.jumpto(this, DisplayControlActivity.class); }

    public void onLrc(View view){ TempUtil.jumpto(this, LrcTestActivity.class); }

    public void onFileTest(View view){ TempUtil.jumpto(this, FileTestActivity.class); }

    public void onGradient(View view){ TempUtil.jumpto(this, GradientShowActivity.class); }

    public void onProgram(View view){ TempUtil.jumpto(this, AlarmProgramActivity.class); }

    public void onClock(View view){ TempUtil.jumpto(this, FashionClockActivity.class); }

    public void onImageAnim(View view){ TempUtil.jumpto(this, ImageAnimationActivity.class); }

    public void onWeatherDemo(View view){ TempUtil.jumpto(this, WeatherDemoActivity.class); }

    public void onPermission(View view){ TempUtil.jumpto(this, PermissionTestActivity.class); }

    public void onText2Speak(View view){
        TempUtil.jumpto(this, Text2SpeechActivity.class);
    }

    public void onScrollTest(View view){
        TempUtil.jumpto(this, ScrollTextActivity.class);
    }

    public void onImageBlur(View view){ TempUtil.jumpto(this, BlurImageActivity.class); }

    public void onNetListen(View view){
        TempUtil.jumpto(this, NetListener.class);
    }

    public void onClearAll(View view){
        DBHelper.getInstance(this).clearAllTable();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 1.non-critical screen decorations (such as the status bar) will be hidden;
     * View.SYSTEM_UI_FLAG_FULLSCREEN //关闭状态栏但空间保留 == WindowManager.LayoutParams.FLAG_FULLSCREEN*
     * View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏底部导航栏且占用其空间
     * View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //单个使用无效果
     * View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION ////单个使用无效果
     *
     * View.SYSTEM_UI_FLAG_IMMERSIVE //沉浸式 始终隐藏，触摸屏幕时也不出现
     */
}