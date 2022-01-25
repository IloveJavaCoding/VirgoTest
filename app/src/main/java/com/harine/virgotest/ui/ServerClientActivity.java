package com.harine.virgotest.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.harine.virgotest.R;
import com.harine.virgotest.bean.Client;
import com.harine.virgotest.bean.ReceiveCallBack;
import com.harine.virgotest.bean.Server;
import com.nepalese.virgosdk.Util.HardwareUtil;

public class ServerClientActivity extends AppCompatActivity implements ReceiveCallBack {
    private static final String TAG = "ServerClientActivity";

    private Context context;
    private Server server;
    private Client client;

    private TextView tvIp, tvStatus, tvShow;
    private SwitchCompat switchCompat;
    private EditText etIp, etMsg;
    private StringBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_client);

        init();
        setData();
        setListener();
    }

    private void init() {
        context = getApplicationContext();

        tvIp = findViewById(R.id.tvIp);
        tvStatus = findViewById(R.id.tvStatus);
        tvShow = findViewById(R.id.tvShow);

        switchCompat = findViewById(R.id.switvhServer);
        etIp = findViewById(R.id.etIp);
        etMsg = findViewById(R.id.etContent);

        builder = new StringBuilder();
    }

    private void setData() {
        tvIp.setText(HardwareUtil.getIpAddress(context));

        server = new Server();
        server.setCallBack(this);
    }

    private void setListener() {
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                server.startServer();
            }else{
                server.closeServer();
            }
        });
    }

    public void onCreate(View view){
        String ip = etIp.getText().toString().trim();
        if(TextUtils.isEmpty(ip)){
            return;
        }
        client = new Client(ip);
        client.setCallBack(this);
    }

    public void onConnect(View view){
       if(client!=null){
           client.connect();
           tvStatus.setText("connected");
       }
    }

    public void onDisconnect(View view){
        if(client!=null){
            client.disconnect();
            tvStatus.setText("disconnected");
        }
    }

    public void onSend(View view){
        if(client!=null){
            client.sendMessage(etMsg.getText().toString().trim());
        }
    }

    @Override
    public void onReceivr(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.append(msg).append("\n");
                tvShow.setText(builder.toString());
            }
        });
    }
}