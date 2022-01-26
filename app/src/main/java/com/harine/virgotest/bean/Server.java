package com.harine.virgotest.bean;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author nepalese on 2021/3/8 08:54
 * @usage 本地服务器
 */
public class Server{
    private static final String TAG = "Server";

    private static final int PORT = 9999;
    private ServerSocket serverSocket;
    private final List<Socket> clientList = new ArrayList<>();
    private final ExecutorService executorService;
    private ReceiveCallBack callBack;

    //建立一个服务器 Socket
    public Server() {
        executorService = Executors.newCachedThreadPool();
    }

    class CreateServer implements Runnable{
        @Override
        public void run() {
            Log.i(TAG, "Server: create");
            try {
                serverSocket = new ServerSocket(PORT);
                Log.i(TAG, ": server started...");
                Socket client;
                while (true){
                    client = serverSocket.accept();
                    clientList.add(client);
                    Log.i(TAG, "run: get client: " + client.getInetAddress());
                    executorService.execute(new ServerRun(client));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //每个客户端一个独立线程
    class ServerRun implements Runnable{
        private final Socket client;//客户端
        private BufferedReader reader;//输入流: 读取
        private PrintWriter writer;//输出流： 返回

        public ServerRun(Socket client) {
            this.client = client;

            try {
                writer = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8)), true);

                reader = new BufferedReader(new InputStreamReader(
                        client.getInputStream(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            //循环接收、读取 Client 端发送过来的信息
            while (true){
                try {
                    String recMsg;
                    if((recMsg =reader.readLine())!=null){
                        Log.i(TAG, "receive: " + recMsg);
                        callBack.onReceivr(TAG + ": "+ recMsg);

                        if(recMsg.equals("-1")){
                            //请求断连
                            Log.i(TAG, "客户端请求断开连接!");
                            writer.println("服务端断开连接!");
                            clientList.remove(client);
                            reader.close();
                            client.close();
                        }else{
                            String sendMsg = "Server: [copy] " + recMsg;
                            writer.println(sendMsg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    public void startServer(){
        executorService.execute(new CreateServer());
    }

    public void closeServer(){
        Log.i(TAG, "closeServer: ");
        clientList.clear();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCallBack(ReceiveCallBack callBack) {
        this.callBack = callBack;
    }
}
