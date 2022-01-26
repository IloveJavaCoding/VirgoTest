package com.harine.virgotest.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.harine.virgotest.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MultiSocketActivity extends AppCompatActivity {
    private static final String TAG = "SocketActivity";

    private static final String IP = "225.0.0.1";
    private static final int PORT = 18000;
    private static final int RECEIVE_SIZE = 1024*48;//48128

    private EditText input;
    private TextView receive;
    private MulticastSocket socket;
    private InetAddress address;

    private StringBuilder builder;
    private String mPath;
    private boolean lock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);

        init();
        setData();
    }

    private void init() {
        input = findViewById(R.id.etInput);
        receive = findViewById(R.id.tvReceiver);

        builder = new StringBuilder();
        mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "video.mp4";
    }

    private void setData() {
        try {
            socket = new MulticastSocket(PORT);
            address = InetAddress.getByName(IP);
            socket.setTimeToLive(2);
            socket.joinGroup(address);
        }catch (Exception e){
            e.printStackTrace();
        }

        onReceive();
    }

    public void onFile(View view){
        new Thread() {
            @Override
            public void run() {
                super.run();
                sendFile(mPath);
            }
        }.start();
    }

    public void onClear(View view){
        builder.delete(0, builder.capacity());
        receive.setText("");
    }

    public void onSend(View view){
        new Thread() {
            @Override
            public void run() {
                super.run();
                sendData();
            }
        }.start();
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

    private void sendData() {
        DatagramPacket packet;

        try {
            String msg = input.getText().toString().trim();
            Log.i(TAG, "sendData: " + msg);
            byte[] buff = msg.getBytes();
            packet = new DatagramPacket(buff, buff.length, address, PORT);
            socket.send(packet);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendFile(String path) {
        DatagramPacket packet;
        byte[] buff = readFile2Bytes(path);

        if(buff!=null){
            try {
                Log.i(TAG, "sendFile: " + buff.length);
                if(buff.length>RECEIVE_SIZE){
                    int seg = buff.length/RECEIVE_SIZE;
                    int index = 0;
                    do {
                        if (!lock) {
                            Log.i(TAG, "send segment: " + index);
                            byte[] segment = new byte[RECEIVE_SIZE];
                            System.arraycopy(buff, index * RECEIVE_SIZE, segment, 0, RECEIVE_SIZE);
                            packet = new DatagramPacket(segment, segment.length, address, PORT);
                            socket.send(packet);
                            lock = true;
                            index++;

                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    lock = false;
                                }
                            }.start();
                        }

                    } while (index < seg);

                    Log.i(TAG, "sendFile: over");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Log.e(TAG, "sendFile: no this file");
        }
    }

    private void receiveData() {
        DatagramPacket packet;

        try {
            byte[] rev = new byte[RECEIVE_SIZE];
            packet = new DatagramPacket(rev, rev.length, address, PORT);
            socket.receive(packet);

            writeByte2File(packet.getData());

            String data = new String(packet.getData()).trim();
            builder.append(System.currentTimeMillis()).append(": ").append(data);
            builder.append("\n");
            handler.sendEmptyMessage(1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private byte[] readFile2Bytes(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        } else {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                long size = inputStream.getChannel().size();
                if (size <= 0L) {
                    return null;
                } else {
                    byte[] bytes = new byte[inputStream.available()];
                    inputStream.read(bytes);
                    return bytes;
                }
            } catch (IOException var6) {
                var6.printStackTrace();
                return null;
            }
        }
    }

    public void writeByte2File(byte[] bytes) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "Movies/"
                + System.currentTimeMillis() + "_.ts";
        BufferedOutputStream outputStream = null;
        try {
            File file = new File(path);
            Log.i(TAG, "writeByte2File: save");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            outputStream = new BufferedOutputStream(fileOutputStream);
            outputStream.write(bytes);
        } catch (Exception var13) {
            var13.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException var12) {
                    var12.printStackTrace();
                }
            }
        }
    }

    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    receive.setText(builder.toString());
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
}