package com.harine.virgotest.component;

import android.content.Context;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.harine.virgotest.Constants;
import com.harine.virgotest.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nepalese on 2021/3/30 08:50
 * @usage 基于两张imageview的图片播放控件
 */
public class VirgoImageViewAnim extends RelativeLayout {
    private static final String TAG = "ImageViewAnim";

    private static final int MSG_NEXT_CYCLE = 1;//下一张循环
    private static final int MSG_NEXT_IN_ABOVE = 11;//下一张图片进入
    private static final int MSG_NEXT_IN_BACK = 12;//下一张图片进入
    private static final int MSG_HIDE_BACK = 13;//下一张图片进入
    private static final int MSG_HIDE_ABOVE = 14;//下一张图片进入
    private static final long INTERVAL = 15 * 1000L;
    private static final int ANIMATION_DELAY = 200;

    private Context mContext;
    private RectF mRectF;
    private DrawableTransitionOptions transitionOptions;
    private RequestOptions requestOptions;

    private ImageView imgAbove, imgBack;
    private Animation inAnim; // 进入动画
    private Animation outAnim; // 出去动画
    private List<File> itemList;

    private int animType;//动画
    private int cyIndex;//循环播放索引
    private boolean isAbove = false;//记录目前使用的图片容器

    public VirgoImageViewAnim(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public VirgoImageViewAnim(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoImageViewAnim(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        initUI();
        initData();
    }

    private void initUI() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootview = inflater.inflate(R.layout.layout_alarm_image_view, this, true);
        imgAbove = rootview.findViewById(R.id.imgAbove);
        imgBack = rootview.findViewById(R.id.imgBack);
    }

    private void initData() {
        transitionOptions = new DrawableTransitionOptions().dontTransition();
        requestOptions = new RequestOptions().skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        itemList = new ArrayList<>();

        cyIndex = 0;
    }

    private void setLayout() {
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = (int) mRectF.left;
        lp.topMargin = (int) mRectF.top;
        lp.width = (int) mRectF.right;
        lp.height = (int) mRectF.bottom;

        this.setLayoutParams(lp);
    }

    private void playImage() {
        loadImage(getCycleItem());
        handler.sendEmptyMessageDelayed(MSG_NEXT_CYCLE, INTERVAL);
    }

    private void loadImage(String path) {
        Log.i(TAG, "loadImage: " + path);
        if (!isAbove) {
            //上层
            isAbove = true;
            loadAbove(path);
        } else {
            //下层
            isAbove = false;
            loadBack(path);
        }
    }

    private void loadBack(String path) {
        imgAbove.startAnimation(outAnim);
        imgAbove.setAlpha(1f);
        displayImg(path, imgBack);
        if (animType > 1) {//左右进出
            handler.sendEmptyMessageDelayed(MSG_NEXT_IN_BACK, ANIMATION_DELAY);
        } else {
            handler.sendEmptyMessageDelayed(MSG_NEXT_IN_BACK, outAnim.getDuration() - 50);
        }
    }

    private void loadAbove(String path) {
        imgBack.startAnimation(outAnim);
        imgBack.setAlpha(1f);
        displayImg(path, imgAbove);
        if (animType > 1) {//左右进出
            handler.sendEmptyMessageDelayed(MSG_NEXT_IN_ABOVE, ANIMATION_DELAY);
        } else {
            handler.sendEmptyMessageDelayed(MSG_NEXT_IN_ABOVE, outAnim.getDuration() - 50);
        }
    }

    private String getCycleItem() {
        if (cyIndex >= itemList.size()) {
            cyIndex = 0;
        }

        File file = itemList.get(cyIndex);
        cyIndex++;
        if (file.exists()) {
            return file.getAbsolutePath();
        } else {
            return getCycleItem();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeMessage();
    }

    //////////////////////////////////////////handler///////////////////////////////////////////////
    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_NEXT_CYCLE:
                    playImage();
                    break;
                case MSG_NEXT_IN_ABOVE:
                    playAbove();
                    break;
                case MSG_NEXT_IN_BACK:
                    playBack();
                    break;
                case MSG_HIDE_BACK:
                    hideBack();
                    break;
                case MSG_HIDE_ABOVE:
                    hideAbove();
                    break;
            }
        }
    };

    private void playAbove() {
//        imgAbove.bringToFront();
        imgAbove.setAlpha(1f);
        imgAbove.startAnimation(inAnim);
        imgAbove.setVisibility(VISIBLE);

        if (animType > 1) {//左右进出
            handler.sendEmptyMessageDelayed(MSG_HIDE_BACK, outAnim.getDuration() - ANIMATION_DELAY);
        } else {
            hideBack();
        }
    }

    private void playBack() {
//        imgBack.bringToFront();
        imgBack.setAlpha(1f);
        imgBack.startAnimation(inAnim);
        imgBack.setVisibility(VISIBLE);

        if (animType > 1) {//左右进出
            handler.sendEmptyMessageDelayed(MSG_HIDE_ABOVE, outAnim.getDuration() - ANIMATION_DELAY);
        } else {
            hideAbove();
        }
    }

    private void hideBack() {
        imgBack.setVisibility(INVISIBLE);
    }

    private void hideAbove() {
        imgAbove.setVisibility(INVISIBLE);
    }

    public void displayImg(String file, ImageView iv) {
        if (iv == null || iv.getContext() == null || file == null) return;
        if (file.endsWith(".gif")) {
            try {
                Glide.with(iv.getContext())
                        .asGif()
                        .load(file)
                        .apply(requestOptions)
                        .transition(transitionOptions)
                        .into(iv);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            try {
                Glide.with(iv.getContext())
                        .load(file)
                        .apply(requestOptions)
                        .transition(transitionOptions)
                        .into(iv);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setImageList(List<File> imags) {
        itemList.clear();
        itemList.addAll(imags);
        cyIndex = 0;
    }

    public void setAnimation(int type) {
        this.animType = type;
        switch (type) {
            case Constants.ANIM_TYPE_FADE://淡入淡出
                setAnim(R.anim.anim_fade_in, R.anim.anim_fade_out);
                break;
            case Constants.ANIM_TYPE_SCALE://中心缩放
                setAnim(R.anim.tiv_anim_scale_center_in, R.anim.tiv_anim_scale_center_out);
                break;
            case Constants.ANIM_TYPE_RL://右进左出
                setAnim(R.anim.tiv_anim_transfor_right_in, R.anim.tiv_anim_transfor_left_out);
                break;
            case Constants.ANIM_TYPE_LR://左进右出
                setAnim(R.anim.tiv_anim_transfor_left_in, R.anim.tiv_anim_transfor_right_out);
                break;

        }
    }

    public void setmRectF(RectF mRectF) {
        this.mRectF = mRectF;
        setLayout();
    }

    public void startPlay() {
        removeMessage();
        playImage();
    }

    private void setAnim(@AnimRes int resIn, @AnimRes int resOut) {
        inAnim = AnimationUtils.loadAnimation(mContext, resIn);
        outAnim = AnimationUtils.loadAnimation(mContext, resOut);
    }

    private void removeMessage() {
        handler.removeMessages(MSG_NEXT_CYCLE);
    }
}
