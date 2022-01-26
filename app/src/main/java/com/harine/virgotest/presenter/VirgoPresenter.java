package com.harine.virgotest.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.harine.virgotest.Constants;
import com.nepalese.virgosdk.Util.JsonUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nepalese on 2021/1/27 14:20
 * @usage
 */
public class VirgoPresenter {
    private static final String TAG = "VirgoPresenter";

    private Context context;
    private final String mHost;
    private httpCallBack callBack;

    public VirgoPresenter(Context context){
        this.context = context;
        mHost = getServerIp();
    }

    private String getServerIp() {
        return Constants.SERVER_HEAD + Constants.DEFAULT_SERVER_IP
                + ":" + Constants.DEFAULT_SERVER_PORT + Constants.SERVER_API;
    }

    public void setCallBack(httpCallBack callBack) {
        this.callBack = callBack;
    }
    /////////////////////////////////////TAG////////////////////////////////////////////
    public static final String TAG_WEATHER = "weather";
    public static final String TAG_UPLOAD_FILE = "upload_file";

    private final String API_UPLOAD_FILE = "UploadFile";
    ////////////////////////////////////API/////////////////////////////////////////////
    public void getWeather(String lng, String lat) {
        //获取天气
        String url = Constants.WEATHER_API_URL + lat + "," + lng;
        BaseConnectation(TAG_WEATHER, url);
    }

    public void uoloadImage(File file){
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", "admin");

        FileConnection(API_UPLOAD_FILE, TAG_UPLOAD_FILE, params, file);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private static final String BOUNDARY =  "&&&&&";
    private static final String HEAD = "--";

    public void FileConnection(String api, final String tag, Map<String, Object> params, File file) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                InputStream inputStream = null;

                //每格分割线内的内容算为一个fileItem(服务器)
                StringBuilder sb = new StringBuilder();

                // 普通的表单数据
                if (params != null) {
                    for (String key : params.keySet()) {
                        sb.append(HEAD + BOUNDARY + "\r\n");
                        sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append("\r\n");
                        sb.append("\r\n");
                        sb.append(params.get(key)).append("\r\n");
                    }
                }

                //上传文件的头
                sb.append(HEAD + BOUNDARY + "\r\n");
                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                        .append(file.getName())
                        .append("\"")
                        .append("\r\n");
                //sb.append("Content-Type: image/jpeg" + "\r\n");// 如果服务器端有文件类型的校验，必须明确指定ContentType
                sb.append("\r\n");

                byte[] headerInfo = sb.toString().getBytes(StandardCharsets.UTF_8);
                byte[] endInfo = ("\r\n" + HEAD + BOUNDARY + "--\r\n").getBytes(StandardCharsets.UTF_8);

                String url = mHost + api;
                boolean isConnected = false;
                try {
                    conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setConnectTimeout(3000); // 设置超时时间
                    conn.setReadTimeout(3000);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST"); // 设置获取信息方式
                    conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                    conn.setRequestProperty("Content-Length", String.valueOf(headerInfo.length + file.length() + endInfo.length));

                    //将文件头，文件内容，文件尾写入到http链接的输出流内
                    OutputStream outputStream = conn.getOutputStream();
                    InputStream stream = new FileInputStream(file);
                    // 写入头部 （包含了普通的参数，以及文件的标示等）
                    outputStream.write(headerInfo);
                    // 写入文件
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = stream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);
                    }
                    // 写入尾部
                    outputStream.write(endInfo);
                    stream.close();
                    outputStream.close();

                    if (conn.getResponseCode() == 200) {
                        inputStream = conn.getInputStream();
                        String json = parseStream2Str(inputStream);
                        //state 用来判断状态
                        boolean state = (boolean) JsonUtil.getResponeValue(json, "status");
                        if(state){
                            callBack.onSuccess(tag, json);
                        }else{
                            callBack.onFail(tag, (String) JsonUtil.getResponeValue(json, "message"));
                        }
                    }else{
                        callBack.onFail(tag, conn.getResponseMessage());
                    }
                    isConnected = true;
                }catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 意外退出时进行连接关闭保护
                    if (conn != null) {
                        conn.disconnect();
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(!isConnected){
                    callBack.onFail(tag, "404");
                }
            }
        };

        new Thread(runnable).start();
    }

    private void BaseConnectation(final String tag, String url) {
        Runnable runnable = () -> {
            HttpURLConnection conn = null;
            InputStream inputStream = null;


            boolean isConnected = false;
            try {
                Log.i(TAG, "BaseConnectation: " + url);
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(3000); // 设置超时时间
                conn.setReadTimeout(3000);
                conn.setDoInput(true);
                conn.setRequestMethod("GET"); // 设置获取信息方式
                conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式

                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                    String json = parseStream2Str(inputStream);
                    //state 用来判断状态
//                    String state = (String) JsonUtil.getResponeValue(json, "status");
                    if(!TextUtils.isEmpty(json)){
                        callBack.onSuccess(tag, json);
                    }else{
                        callBack.onFail(tag, "");
                    }
                }else{
                    callBack.onFail(tag, conn.getResponseMessage());
                }
                isConnected = true;
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 意外退出时进行连接关闭保护
                if (conn != null) {
                    conn.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(!isConnected){
                callBack.onFail(tag, "404");
            }
        };

        new Thread(runnable).start();
    }

    //将输入流转化为 String 型
    private String parseStream2Str(InputStream inStream){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        while (true) {
            int len;
            try {
                if ((len = inStream.read(buffer)) == -1) break;
                outputStream.write(buffer, 0, len);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = outputStream.toByteArray();

        return new String(data, StandardCharsets.UTF_8);
    }

    public interface httpCallBack{
        void onSuccess(String tag, String json);
        void onFail(String tag, String error);
    }
}
