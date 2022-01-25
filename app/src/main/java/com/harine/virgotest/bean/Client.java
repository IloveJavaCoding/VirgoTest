package com.harine.virgotest.bean;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author nepalese on 2021/3/8 09:24
 * @usage 客户端
 */
public class Client {
    private static final String TAG = "Client";

    private static final String DEFAULT_IP = "192.168.2.138";
    private static final int PORT = 9999;
    private static final int TIMEOUT = 60000;

    private final ExecutorService executorService;
    private PrintWriter writer;
    private BufferedReader reader;
    private ReceiveCallBack callBack;
    private final String ip;

    public Client(String ip) {
        if(ip==null){
            this.ip = DEFAULT_IP;
        }else{
            this.ip = ip;
        }

        executorService = Executors.newCachedThreadPool();
    }

    class connectService implements Runnable{
        @Override
        public void run() {
            try {
                Log.i(TAG, "create client");
                Socket socket = new Socket(ip, PORT);
                socket.setSoTimeout(TIMEOUT);

                writer = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)), true);

                reader = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(), StandardCharsets.UTF_8));

                receiveMessage();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void receiveMessage() {
            while (true) {
                try {
                    String recMsg;
                    if ((recMsg = reader.readLine()) != null) {
                        callBack.onReceivr(TAG + ": "+ recMsg);
                        Log.i(TAG, "receiveMessage: " + recMsg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class sendService implements Runnable{
        private final String sendMeg;

        public sendService(String sendMeg) {
            this.sendMeg = sendMeg;
        }

        @Override
        public void run() {
            Log.i(TAG, "send " + sendMeg);
            writer.println(sendMeg);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////
    public void connect(){
        executorService.execute(new connectService());
    }

    public void disconnect(){
        sendMessage("-1");
    }

    public void sendMessage(String msg){
        executorService.execute(new sendService(msg));
    }


    public void setCallBack(ReceiveCallBack callBack) {
        this.callBack = callBack;
    }
}