package com.harine.virgotest.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.harine.virgotest.R;
import com.harine.virgotest.component.clock.VirgoDigitalClockView;
import com.harine.virgotest.util.TempUtil;

public class DisplayControlActivity extends AppCompatActivity {
    private static final String TAG = "DisplayControlActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_control);

        //状态栏，导航栏消失
        TempUtil.setSNHide(this);

        //
//        TempUtil.setSNTrans(this);

//        TempUtil.setLayout(this);
//        hideBottomUIMenu();

//        TempUtil.setStatusHide(this);
    }

    //状态栏变黑，导航栏没变化
    private void setFull(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    //效果跟TempUtil.setSNHide(this); 一样
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
//        }
//    }
}