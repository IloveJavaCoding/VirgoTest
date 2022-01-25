package com.harine.virgotest.util;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.WindowManager;

/**
 * @author nepalese on 2021/3/2 09:39
 * @usage
 */
public class TempUtil {
    public static int getRandomInt(int a, int b) {
        return (int)(Math.random() * (double)(b - a)) + a;
    }

    public static void jumpto(Context context, Class c){
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }
    ///////////////////
    public static void hideActionBar(Activity activity) {
        // Hide UI
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public static void setFullScreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void setLayout(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏StatusBar
    }

    public static void hideStatus(Activity activity){
        View decorView = activity.getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
//        ActionBar actionBar = getActionBar();
//        actionBar.hide();
    }

    //让内容显示在状态栏

    public static void setStatusLight(Activity activity){
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public static void setStatusHide(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    public static void setStatusHideLight(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    public static void setNavigationHide(Activity activity){
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    //沉浸式: 状态栏、导航栏透明
    public static void setSNTrans(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
//        activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }

    //沉浸式: 无状态栏、导航栏
    public static void setSNHide(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(option);
//        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
//        activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }
}
