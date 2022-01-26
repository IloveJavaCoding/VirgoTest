package com.harine.virgotest.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.harine.virgotest.R;
import com.harine.virgotest.component.color.VirgoColorBoard;
import com.harine.virgotest.util.TempUtil;

public class PermissionTestActivity extends AppCompatActivity implements VirgoColorBoard.ColorCallback {
    private static final String TAG = "PermissionTestActivity";

    private VirgoColorBoard colorBoard;
    private View imageView;
    private int color = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_test);
        TempUtil.setFullScreen(this);

        init();
    }

    private void init() {
        colorBoard = new VirgoColorBoard(this);
        colorBoard.setCallback(this);

        imageView = findViewById(R.id.imgColor);
    }

    public void onPicker(View view){
        if(color!=-1){
            colorBoard.setmColor(color);
        }
        colorBoard.show();
    }

    @Override
    public void onPick(int color) {
        this.color = color;
        imageView.setBackgroundColor(color);
    }

    /**
     * 检验是否有悬浮权限，并跳转到设置页
     */
    private void checkAlertPermission(Context context, int requsetCode) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(context)) {
                    //若未授权则请求权限
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, requsetCode);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}