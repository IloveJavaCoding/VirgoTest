package com.harine.virgotest.component.color;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.harine.virgotest.R;

/**
 * @author nepalese on 2021/5/11 14:33
 * @usage
 */
public class VirgoSVPicker extends RelativeLayout {
    private static final String TAG = "VirgoSVPicker";

    private Context context;
    private VirgoColorSVView svView;
    private VirgoSelectCy selectCy;

    public VirgoSVPicker(Context context) {
        this(context, null);
    }

    public VirgoSVPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoSVPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_color_sv, this, true);
        init();
    }

    private void init() {
        svView = findViewById(R.id.svView);
        selectCy = findViewById(R.id.svCircle);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //设置色相
    public void setmH(float mH) {
        svView.setmColorH(mH);
    }

    //设置圆形选择器的位置
    public void setSV(float s, float v){
        Log.i(TAG, "setSV: s " +s + ", v " +v);
        selectCy.setSV(s, v);
    }

    public void setCallback(VirgoSelectCy.PointCallback callback) {
        selectCy.setCallback(callback);
    }
}
