package com.harine.virgotest.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.harine.virgotest.R;
import com.harine.virgotest.VideoDecoder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

public class SocketPlayActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "SocketPlayActivity";

    private static final String IP = "224.0.0.8";//"225.0.0.1"
    private static final int PORT = 10001;

    private VideoDecoder videoDecoder;
    private MulticastSocket socket;
    private InetAddress address;

    private SurfaceView surfaceView;
    private MediaCodec mediaCodec;
    private MediaFormat mediaFormat;
    private Surface surface;

    private final String MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_MPEG4;
    private final int FRAME_RATE = 25;
    private final int BYTE_SIZE = 1024*50;//48128
    private final int BIT_RATE = 1024*4700;//
    private final int FRAME_INTERVAL = 1;
    private final int mWidth = 720;
    private final int mHeight = 576;
    private final int dequeueTimeOut = 100;

    private boolean isOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_play);

        init();
        setData();
        setListener();
    }

    private void init() {
        surfaceView = findViewById(R.id.surfaceView);
        videoDecoder = new VideoDecoder();

        initSocket();
    }

    private void setData() {
        onReceive();
    }

    private void initSocket(){
        try {
            socket = new MulticastSocket(PORT);
            address = InetAddress.getByName(IP);
            socket.joinGroup(address);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setListener() {
        surfaceView.getHolder().addCallback(this);
    }

    /////////////////////////////////////////////////////////
    private void startPlay(){
        mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_MPEG2, mWidth, mHeight);
        //图像格式
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);//MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar
        //码率
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        //设置帧率
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        //关键帧
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, FRAME_INTERVAL);

        try {
            mediaCodec = MediaCodec.createDecoderByType(MIME_TYPE);//MediaFormat.MIMETYPE_VIDEO_AVC
        } catch (IOException e) {
            e.printStackTrace();
        }
        //format  如果为解码器，此处表示输入数据的格式；如果为编码器，此处表示输出数据的格式。
        //surface 指定一个surface，可用作decode的输出渲染。
        //crypto  如果需要给媒体数据加密，此处指定一个crypto类.
        //flags   如果正在配置的对象是用作编码器，此处加上CONFIGURE_FLAG_ENCODE 标签。
        mediaCodec.configure(mediaFormat, surface, null, 0);
        mediaCodec.start();
    }

    private void onReceive(){
        new Thread() {
            @Override
            public void run() {
                super.run();
                receiveData();
            }
        }.start();
    }

    private void receiveData() {//75-80ms  48128byte
        DatagramPacket packet;

        try {
            byte[] rev = new byte[BYTE_SIZE];
            packet = new DatagramPacket(rev, rev.length, address, PORT);
            socket.receive(packet);
            if(isOk){
                videoDecoder.decodeFrame(packet.getData(), 1);

//                onFrame(packet.getData(),packet.getOffset(), packet.getLength());
            }

            String data = new String(packet.getData()).trim();
            Log.d(TAG, "receiveData: " + data);

            handler.sendEmptyMessage(1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onFrame(byte[] buf, int offset, int length) {
        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(buf, offset, length);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, 0, 0);
        }else{
            return;
        }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, dequeueTimeOut);
        while (outputBufferIndex >= 0) {
            mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    onReceive();
                    break;
            }
        }
    };

    private void release(){
        try {
            socket.leaveGroup(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.close();
        socket = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        release();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        this.surface = holder.getSurface();
//        startPlay();
        videoDecoder.createDecoder(mWidth, mHeight, MIME_TYPE, surface);
        isOk = true;
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        this.surface = null;
    }
}