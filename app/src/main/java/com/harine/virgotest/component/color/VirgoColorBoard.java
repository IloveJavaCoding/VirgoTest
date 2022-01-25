package com.harine.virgotest.component.color;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.harine.virgotest.R;

/**
 * @author nepalese on 2021/5/11 08:43
 * @usage 弹框式颜色画板
 */
public class VirgoColorBoard extends Dialog {
    private static final String TAG = "VirgoColorBoard";
    private static final int MSG_UPDATE_UI= 1;
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 255;

    private Context context;
    private ColorCallback callback;//结果回调

    private VirgoSVPicker svPicker;//饱和度、亮度控制器
    private VirgoHPicker hPicker;//色相控制器
    private VirgoAPicker aPicker;//透明度控制器

    private View preview;//选中颜色预览
    private EditText etA, etR, etG, etB, etColor;

    private int mColor;//传入的颜色
    private int dialogWidth = -1;//默认为屏幕宽度
    private int dialogHeight = 800;//默认弹框高度
    private float dialogAlpha = 1f;//默认弹框透明度
    private final float[] hsv = new float[3];

    public VirgoColorBoard(@NonNull Context context) {
        super(context, R.style.VirgoColorBoard);
        this.init(context);
    }

    public VirgoColorBoard(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.mColor = Color.RED;//默认

        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        View view = mLayoutInflater.inflate(R.layout.layout_color_board, null);

        svPicker = view.findViewById(R.id.svPicker);
        hPicker = view.findViewById(R.id.hPicker);
        aPicker = view.findViewById(R.id.aPicker);

        preview = view.findViewById(R.id.colorPreview);
        etA = view.findViewById(R.id.etA);
        etR = view.findViewById(R.id.etR);
        etG = view.findViewById(R.id.etG);
        etB = view.findViewById(R.id.etB);
        etColor = view.findViewById(R.id.etColor);

        this.setContentView(view);
        this.setListener();
    }

    private void setLayout() {
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        if(dialogWidth>0){
            lp.width = dialogWidth; // 宽度
        }
        lp.height = dialogHeight; // 高度
        lp.alpha = dialogAlpha; // 透明度

        dialogWindow.setAttributes(lp);
    }

    private void initData() {
        setLayout();
        initColor();
    }

    private void initColor(){
        Color.colorToHSV(mColor, hsv);

        svPicker.setmH(hsv[0]);
        svPicker.setSV(hsv[1], hsv[2]);
        hPicker.setmH(hsv[0]);
        aPicker.setmH(hsv[0]);
        aPicker.setA(Color.alpha(mColor));

        preview.setBackgroundColor(mColor);
        etColor.setText(Integer.toHexString(mColor));
        extraARGB(mColor);
    }

    private void setListener() {
        svPicker.setCallback(svCallBack);
        hPicker.setCallback(hCallBack);
        aPicker.setCallback(aCallBack);

        addTextListener();
        etColor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))){
                    //收起键盘
                    ((InputMethodManager)etColor.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(etColor.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    //要执行任务（这里显示输入的内容）
                    String input = etColor.getText().toString();
                    if(input.length()==6 || input.length()==8){
                        for(Character c: input.toCharArray()){
                            if(c > 'f'){
                                return true;
                            }
                        }

                        mColor = Color.parseColor("#" + input);
                        initColor();
                        callback.onPick(mColor);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //防止edittext settext 与 onTextChanged 发生冲突，在赋值前取消监听，之后重新监听；
    private void addTextListener(){
        etA.addTextChangedListener(watcherA);
        etR.addTextChangedListener(watcherR);
        etG.addTextChangedListener(watcherG);
        etB.addTextChangedListener(watcherB);
    }

    private void removeTextListener() {
        etA.removeTextChangedListener(watcherA);
        etR.removeTextChangedListener(watcherR);
        etG.removeTextChangedListener(watcherG);
        etB.removeTextChangedListener(watcherB);
    }

    //当a/r/g/b输入值大于255或小于0时，强制重置为255或0；
    private void resetInputVlue(EditText et, TextWatcher watcher, int value) {
        et.removeTextChangedListener(watcher);
        if(value>MAX_VALUE){
            et.setText(String.valueOf(MAX_VALUE));
        }else{
            et.setText(String.valueOf(MIN_VALUE));
        }
        et.addTextChangedListener(watcher);
    }

    //手动调整r/g/b值后，更新需要更改的值
    private void updateWithInput() {
        Color.colorToHSV(mColor, hsv);

        svPicker.setmH(hsv[0]);
        svPicker.setSV(hsv[1], hsv[2]);
        hPicker.setmH(hsv[0]);
        aPicker.setmH(hsv[0]);

        updateSelectColor();
    }

    //更新选中的颜色
    private void updateSelectColor(){
        callback.onPick(mColor);
        preview.setBackgroundColor(mColor);
        etColor.setText(Integer.toHexString(mColor));
    }

    private final TextWatcher watcherA = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(TextUtils.isEmpty(s)) return;
            int alph = Integer.parseInt(s.toString());
            if(alph<=MAX_VALUE && alph>=MIN_VALUE){
                aPicker.setA(alph);
                mColor =(alph<<24) | (mColor & 0x00ff0000) | (mColor & 0x0000ff00) | (mColor & 0x000000ff);
                updateSelectColor();
            }else{
                resetInputVlue(etA, this, alph);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private final TextWatcher watcherR = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(TextUtils.isEmpty(s)) return;
            int r = Integer.parseInt(s.toString());
            if(r<=MAX_VALUE && r>=MIN_VALUE){
                mColor =(mColor & 0xff000000) | (r & 0x00ff0000) | (mColor & 0x0000ff00) | (mColor & 0x000000ff);
                updateWithInput();
            }else{
                resetInputVlue(etR, this, r);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private final TextWatcher watcherG = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(TextUtils.isEmpty(s)) return;
            int g = Integer.parseInt(s.toString());
            if(g<=MAX_VALUE && g>=MIN_VALUE){
                mColor =(mColor & 0xff000000) | (mColor & 0x00ff0000) | (g & 0x0000ff00) | (mColor & 0x000000ff);
                updateWithInput();
            }else{
                resetInputVlue(etG, this, g);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private final TextWatcher watcherB = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(TextUtils.isEmpty(s)) return;
            int b = Integer.parseInt(s.toString());
            if(b<=MAX_VALUE && b>=MIN_VALUE){
                mColor =(mColor & 0xff000000) | (mColor & 0x00ff0000) | (mColor & 0x0000ff00) | (b & 0x000000ff);
                updateWithInput();
            }else{
                resetInputVlue(etB, this, b);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //饱和度、亮度回调
    private final VirgoSelectCy.PointCallback svCallBack = new VirgoSelectCy.PointCallback() {
        @Override
        public void onUpdateSV(float s, float v) {
            hsv[1] = s;
            hsv[2] = v;
            mColor = Color.HSVToColor(hsv);
            handler.sendEmptyMessage(MSG_UPDATE_UI);
        }
    };

    //色相回调
    private final VirgoSelectRect.RectCallback hCallBack = new VirgoSelectRect.RectCallback() {
        @Override
        public void onProgress(float progress) {
            hsv[0] = progress;
            svPicker.setmH(progress);
            aPicker.setmH(progress);
            mColor = Color.HSVToColor(hsv);
            handler.sendEmptyMessage(MSG_UPDATE_UI);
        }
    };

    //透明度回调
    private final VirgoSelectRect.RectCallback aCallBack = new VirgoSelectRect.RectCallback() {
        @Override
        public void onProgress(float progress) {
            int alph = (int) (255 - progress);
            mColor =(alph<<24) | (mColor & 0x00ff0000) | (mColor & 0x0000ff00) | (mColor & 0x000000ff);
            handler.sendEmptyMessage(MSG_UPDATE_UI);
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==MSG_UPDATE_UI){
                updateUI();
            }
        }
    };

    //选择变化后相应数据改变
    private void updateUI() {
        updateSelectColor();
        extraARGB(mColor);
    }

    //提取某一颜色的a/r/g/b和hex值
    private void extraARGB(int color) {
        removeTextListener();
        int a = color >>24 & 0xff;
        int r = color >> 16 & 0xff;
        int g = color >> 8 & 0xff;
        int b = color & 0xff;

        etA.setText(String.valueOf(a));
        etR.setText(String.valueOf(r));
        etG.setText(String.valueOf(g));
        etB.setText(String.valueOf(b));
        addTextListener();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void show() {
        super.show();
        initData();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    //结果回调
    public interface ColorCallback{
        void onPick(@ColorInt int color);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void setCallback(ColorCallback callback) {
        this.callback = callback;
    }

    /**
     * 颜色赋值
     * @param mColor
     */
    public void setmColor(@ColorInt int mColor) {
        this.mColor = mColor;
    }


    /**
     * 设置弹框宽度
     * @param dialogWidth
     */
    public void setDialogWidth(int dialogWidth) {
        this.dialogWidth = dialogWidth;
    }

    /**
     * 设置弹窗高度
     * @param dialogHeight
     */
    public void setDialogHeight(int dialogHeight) {
        this.dialogHeight = dialogHeight;
    }

    /**
     * 设置弹窗透明度
     * @param dialogAlpha
     */
    public void setDialogAlpha(float dialogAlpha) {
        this.dialogAlpha = dialogAlpha;
    }
}
