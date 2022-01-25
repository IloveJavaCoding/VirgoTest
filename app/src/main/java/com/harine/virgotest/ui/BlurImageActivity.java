package com.harine.virgotest.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.harine.virgotest.R;
import com.nepalese.virgosdk.Util.BitmapUtil;

import androidx.appcompat.app.AppCompatActivity;

public class BlurImageActivity extends AppCompatActivity {
    private static final String TAG = "BlurImageActivity";

    private Button bBlur;
    private ImageView imageView1;
    private ImageView imageView2;
    private TextView tvRate, tvRadius, tvR, tvG, tvB;
    private SeekBar sbRate, sbRadius, sbR, sbG, sbB;

    private float r = 0.4f;
    private float g = 0.4f;
    private float b = 0.4f;
    private float rate = 0.3f;//截取比例
    private int radius = 18;//模糊半径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur_image);

        init();
        setData();
    }

    private void init() {
        imageView2 = findViewById(R.id.imgOriBg);
        imageView1 = findViewById(R.id.imgBlurBg);
        bBlur = findViewById(R.id.bBlur);

        tvRate = findViewById(R.id.tvRate);
        tvRadius = findViewById(R.id.tvRadius);
        sbRate = findViewById(R.id.sbRate);
        sbRadius = findViewById(R.id.sbRadius);

        tvR = findViewById(R.id.tvR);
        tvG = findViewById(R.id.tvG);
        tvB = findViewById(R.id.tvB);
        sbR = findViewById(R.id.sbR);
        sbG = findViewById(R.id.sbG);
        sbB = findViewById(R.id.sbB);
    }

    private void setData() {
        imageView1.setImageResource(R.mipmap.img_wolf);

        bBlur.setOnClickListener(v -> imageView2.setImageBitmap(
                matchBlurBg(getApplicationContext(),
                        BitmapUtil.getBitmapFromRes(getApplicationContext(), R.mipmap.img_wolf),
                        imageView2.getWidth(),
                        imageView2.getHeight(),
                        rate, radius)));

        sbRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rate = progress/10f;
                tvRate.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = progress;
                tvRadius.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                r = progress/10f;
                tvR.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                g = progress/10f;
                tvG.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                b = progress/10f;
                tvB.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 截取图片并模糊
     * @param context 上下文
     * @param bitmap 图片源
     * @param aimW 容器宽
     * @param aimH 容器高
     * @param rate 截取比例(0 - 1] 0.3f;
     * @param radius 高斯模糊半径 半径越大，越模糊 0-25: 18
     * @return b
     */
    private Bitmap matchBlurBg(Context context, Bitmap bitmap, int aimW, int aimH, float rate, int radius){
        //1. 截取图片
        //2. 放大
        //3. 模糊
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.i(TAG, "matchBlurBg: img w: " + width + " h: " + height);

        if(rate<=0 || rate>1){
            rate = 0.3f;
        }

        int cutW = (int) (aimW*rate);
        int cutH = (int) (aimH*rate);

        Matrix matrix = new Matrix();
        if(width>=cutW && height>=cutH){
            //足够剪切
            Log.i(TAG, ": 足够剪切");
            bitmap = Bitmap.createBitmap(bitmap, (width-cutW)/2, (height-cutH)/2, cutW, cutH, null, true);
            width = cutW;
            height = cutH;
            matrix.postScale(1/rate, 1/rate);
        }else{
            //不够剪辑，直接放大
            Log.i(TAG, ": 不够剪辑，直接放大");
            matrix.postScale(aimW*1f/width, aimH*1f/height);
        }

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);//缩放到容器大小

        //改变亮度
        Bitmap temp = Bitmap.createBitmap(aimW, aimH, Bitmap.Config.ARGB_8888);
        ColorMatrix cMatrix = new ColorMatrix();
//        cMatrix.setSaturation(0.2f); // 设置饱和度 0-1
//        cMatrix.setRotate(); // 设置色调 0,1,2分别代表像素点颜色矩阵中的Red，Green,Blue分量
        cMatrix.setScale(r, g, b, 0.8f);  // 设置亮度 某种颜色分量的缩放 当亮度为0时图片呈黑色 0.4f, 0.4f, 0.4f, 0.8f

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        bitmap.recycle();

        //高斯模糊
        Bitmap inputBitmap = Bitmap.createScaledBitmap(temp, aimW, aimH, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        blurScript.setRadius(radius);
        blurScript.setInput(tmpIn);
        blurScript.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        temp.recycle();
        inputBitmap.recycle();
        return outputBitmap;
    }
}