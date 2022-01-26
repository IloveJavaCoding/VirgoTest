package com.harine.virgotest.component.color;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.harine.virgotest.R;

/**
 * @author nepalese on 2021/5/12 13:50
 * @usage
 */
public class VirgoAPicker extends RelativeLayout {
    private static final String TAG = "VirgoAPicker";

    private Context context;
    private VirgoColorAView aView;
    private VirgoSelectRect selectRect;

    public VirgoAPicker(Context context) {
        this(context, null);
    }

    public VirgoAPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoAPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_color_a, this, true);
        init();
    }

    private void init() {
        aView = findViewById(R.id.aView);
        selectRect = findViewById(R.id.aRect);
        selectRect.setmMaxProgress(255);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //设置色相
    public void setmH(float mH) {
        aView.setmColorH(mH);
    }

    public void setA(float a) {
        selectRect.setProgress(255-a);
    }

    public void setCallback(VirgoSelectRect.RectCallback callback) {
        selectRect.setCallback(callback);
    }
}
