package com.harine.virgotest.ui;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.harine.virgotest.R;
import com.harine.virgotest.component.VirgoThreeImage;
import com.harine.virgotest.component.VirgoThreeImage2;
import com.nepalese.virgosdk.Util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class ImageAnimationActivity extends AppCompatActivity {
    private static final String TAG = "ImageAnimationActivity";

    private VirgoThreeImage image3;
    private List<File> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_animation);

        setData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            play();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void play() {
//        VirgoThreeImage2 image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);

//        image2.setResList(itemList);
        image3.setResList(itemList);

//        image2.startPlay();
        image3.startPlay();
    }

    private void setData() {
        File file = new File(FileUtil.getRootPath()+ "/Pictures/test");
        if(file.exists()){
           itemList = Arrays.asList(Objects.requireNonNull(file.listFiles()));
        }else{
            Log.i(TAG, "地址不存在！");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onLast(View view){
        image3.playLast();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onNext(View view){
        image3.playNext();
    }

//    private void addImage() {
//        if (layout == null)
//            return;
//
//        VirgoImageViewAnim view = new VirgoImageViewAnim(getApplicationContext());
//        layout.addView(view);
//
//        view.setmRectF(new RectF(0, 0,
//                ScreenUtil.getScreenWidth(getApplicationContext()),
//                ScreenUtil.getScreenHeight(getApplicationContext())));
//        view.setAnimation(Constants.ANIM_TYPE_RL);
//        view.setImageList(itemList);
//        view.startPlay();
//    }

//    private void addImage3() {
//        if (layout == null)
//            return;
//
//        VirgoThreeImage2 view = new VirgoThreeImage2(getApplicationContext());
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//        layout.addView(view, layoutParams);
//
//        view.setResList(itemList);
//        view.startPlay();
//    }
//
//    private void addImage4() {
//        if (layout == null)
//            return;
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            VirgoThreeImage view = new VirgoThreeImage(getApplicationContext());
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//            layout.addView(view, layoutParams);
//
//            view.setResList(itemList);
//            view.startPlay();
//        }
//    }
}