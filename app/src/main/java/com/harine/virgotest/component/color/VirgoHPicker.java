package com.harine.virgotest.component.color;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.harine.virgotest.R;

/**
 * @author nepalese on 2021/5/11 17:49
 * @usage
 */
public class VirgoHPicker extends RelativeLayout {
    private static final String TAG = "VirgoHPicker";

    private Context context;
    private VirgoSelectRect selectRect;

    public VirgoHPicker(Context context) {
        this(context, null);
    }

    public VirgoHPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoHPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_color_h, this, true);
        init();
    }

    private void init() {
        selectRect = findViewById(R.id.hRect);
        selectRect.setmMaxProgress(360);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setmH(float mH) {
        selectRect.setProgress(mH);
    }

    public void setCallback(VirgoSelectRect.RectCallback callback) {
        selectRect.setCallback(callback);
    }
}
