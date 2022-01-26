package com.harine.virgotest.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.harine.virgotest.R;
import com.harine.virgotest.util.TempUtil;
import com.harine.virgotest.util.TestUtil;
import com.nepalese.virgosdk.Util.BitmapUtil;
import com.nepalese.virgosdk.Util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;

public class ImageMagicActivity extends AppCompatActivity {
    private static final String TAG = "ImageMagicActivity";
    private static final int REQUESTCODE_IMG = 1;

    private Context context;
    private ImageView imageView;
    private Bitmap bitmap;
    private String path;
    private boolean lock = false;//防多次处理
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_magic);

        TempUtil.setStatusLight(this);
        init();
        setData();
    }

    private void init() {
        context = getApplicationContext();
        path = FileUtil.getRootPath() + File.separator + "Download";//this.getExternalFilesDir("").getAbsolutePath();

        imageView = findViewById(R.id.imgMagic);
    }

    private void setData() {
        //反转图
//        bitmap = TestUtil.convertImage(BitmapUtil.getBitmapFromRes(this, R.mipmap.img_test));
        //灰度图
//        bitmap = TestUtil.grayImage(BitmapUtil.getBitmapFromRes(this, R.mipmap.img_test));
        //ASCII图
//        bitmap = TestUtil.Image2Ascii(BitmapUtil.getBitmapFromRes(this, R.mipmap.img_test));
        bitmap = BitmapUtil.drawable2Bitmap(imageView.getDrawable());//默认
    }

    public void onProcess(View view){
        if(lock){
            return;
        }
        if(bitmap==null){
            return;
        }
        bitmap = TestUtil.Image2Ascii(bitmap);
        imageView.setImageBitmap(bitmap);
        lock = true;
    }

    public void onSave(View view){
        long time = System.currentTimeMillis();
        BitmapUtil.saveBitmap2File(bitmap, path, time+"_.png");
        Toast.makeText(context,  "save success!", Toast.LENGTH_SHORT).show();
    }

    public void onInport(View view){
        pickImageFile();
    }

    //选择图片
    private void pickImageFile() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUESTCODE_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==REQUESTCODE_IMG){
                assert data != null;
                Uri uri = data.getData();
                ContentResolver contentResolver = context.getContentResolver();
                try {
                    assert uri != null;
                    bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri));
                    imageView.setImageBitmap(bitmap);
                    lock = false;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}