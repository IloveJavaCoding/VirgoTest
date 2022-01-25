package com.harine.virgotest.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.nepalese.virgosdk.Util.BitmapUtil;
import com.nepalese.virgosdk.Util.FileUtil;

/**
 * @author nepalese on 2021/3/5 10:10
 * @usage
 */
public class TestUtil {
    private static final String TAG = "TestUtil";

    /**
     * 反转图片 :把每个像素点的每个rgb值都与255相减（alpha的值不改变）
     * @param bitmap
     * @return
     */
    public static Bitmap convertImage(Bitmap bitmap){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pixels = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        for(int i=0; i<pixels.length; i++){
            int a = (pixels[i] & 0xff000000)>>24;
            int r = (pixels[i] & 0x00ff0000)>>16;
            int g = (pixels[i] & 0x0000ff00)>>8;
            int b = pixels[i] & 0x000000ff;

            pixels[i] = a<<24 | (255-r)<<16 | (255-g) <<8 | (255-b);
        }

        bitmap = Bitmap.createBitmap(pixels, w, h, Bitmap.Config.ARGB_8888);
        return bitmap;
    }

    /**
     * 转灰度图:Gray Scale Image 或是Grey Scale Image，又称灰阶图。
     * 把白色与黑色之间按对数关系分为若干等级，称为灰度。
     * 灰度分为256阶。
     * 用灰度表示的图像称作灰度图。
     * 1.浮点算法：Gray=R0.3+G0.59+B*0.11
     * 2.整数方法：Gray=(R30+G59+B*11)/100
     * 3.移位方法：Gray =(R76+G151+B*28)>>8;
     * 4.平均值法：Gray=（R+G+B）/3;
     * 5.仅取绿色：Gray=G；
     * @param bitmap
     * @return
     */
    public static Bitmap grayImage(Bitmap bitmap){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pixels = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        for(int i=0; i<pixels.length; i++){
            int a = (pixels[i] & 0xff000000)>>24;
            int r = (pixels[i] & 0x00ff0000)>>16;
            int g = (pixels[i] & 0x0000ff00)>>8;
            int b = pixels[i] & 0x000000ff;

            int avg = (r+g+b)/3;
            pixels[i] = a<<24 | (avg)<<16 | (avg) <<8 | (avg);
        }

        bitmap = Bitmap.createBitmap(pixels, w, h, Bitmap.Config.ARGB_8888);
        return bitmap;
    }

    /**
     * 将图片转ASCII码字符
     * @param bitmap 源图
     * @return 码图
     */
    public static String Image2AsciiStr(Bitmap bitmap){
        //把字符分成15阶，即0-14
        String[] arr = {"M","N","H","Q","$","O","C","?","7",">","!",":","–",";","."};

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pixels = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        StringBuilder builder = new StringBuilder();

        //将每个像素转换相应的字符
        for(int i=0; i<pixels.length; i++){
            int r = (pixels[i] & 0x00ff0000)>>16;
            int g = (pixels[i] & 0x0000ff00)>>8;
            int b = pixels[i] & 0x000000ff;

            int avg = (r+g+b)/3;
            //0-255转换成0-14阶
            int step = (int) Math.floor(avg/18f);
            if(i>0 && i%w==0){
                //换行
                builder.append("\n");
            }
            builder.append(arr[step]);
        }

        return builder.toString();
    }

    /**
     * 将图片转ASCII码字符文件
     * @param bitmap 源图
     */
    public static void Image2AsciiFile(Bitmap bitmap, String path, String name){
        FileUtil.writeToFile(Image2AsciiStr(bitmap), path, name);
    }

    /**
     * 将图片转ASCII码图
     * @param bitmap 源图
     * @return 码图
     */
    public static Bitmap Image2Ascii(Bitmap bitmap){
        //把字符分成15阶，即0-14
        String[] arr = {"M","N","H","Q","$","O","C","?","7",">","!",":","–",";","."};
        //一个像素转为字符后缩放比例
        int scale =7;
        int maxPix = 170000;
        float textSize = 12;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        //缩放图片: 防溢出
        if(w*h>maxPix){
            int rate = (int) Math.round(Math.sqrt(w*h*1f/maxPix));
            Log.i(TAG, "缩放图片: " + rate);
            bitmap = BitmapUtil.scaleBitmap(bitmap, 1f/rate);

            w = bitmap.getWidth();
            h = bitmap.getHeight();
        }

        int[] pixels = new int[w * h];

        //获取像素点
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        //初始化
        StringBuilder builder = new StringBuilder();
        Bitmap outBitmap = Bitmap.createBitmap(w*scale, h*scale, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setTextSize(textSize);
        paint.setColor(Color.BLACK);

        //白底
        canvas.drawColor(Color.WHITE);

        //将每个像素转换相应的字符
        for(int i=0; i<pixels.length; i++){
            int r = (pixels[i] & 0x00ff0000)>>16;
            int g = (pixels[i] & 0x0000ff00)>>8;
            int b = pixels[i] & 0x000000ff;

            //灰度算法
            int avg = (r+g+b)/3;
            //0-255转换成0-14阶
            int step = (int) Math.floor(avg/18f);
            if(i>0 && i%w==0){
                //换行、绘制、重置
                builder.append("\n");
                canvas.drawText(builder.toString(), 0, (int)(i/w)*scale, paint);
                builder.delete(0, builder.capacity());
            }
            builder.append(arr[step]);
        }

        return outBitmap;
    }


    /**
     * 字符集转bitmap
     * @param text
     * @param context
     * @return
     */
    public static Bitmap textAsBitmap(String text, Context context) {
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.MONOSPACE);
        textPaint.setTextSize(12);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        StaticLayout layout = new StaticLayout(text, textPaint, width,
                Layout.Alignment.ALIGN_CENTER, 1f, 0.0f, true);

        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth() + 20, layout.getHeight() + 20, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(10, 10);
        canvas.drawColor(Color.WHITE);
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色

        layout.draw(canvas);
        return bitmap;
    }
}