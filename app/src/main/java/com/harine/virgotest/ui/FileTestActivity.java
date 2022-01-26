package com.harine.virgotest.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.harine.ui.view.StationView_RM;
import com.harine.ui.view.StationView_S1;
import com.harine.virgotest.R;
import com.harine.virgotest.presenter.VirgoPresenter;
import com.nepalese.virgosdk.Util.FileUtil;

import java.io.File;

public class FileTestActivity extends AppCompatActivity implements VirgoPresenter.httpCallBack{
    private static final String TAG = "FileTestActivity";

    private VirgoPresenter presenter;
    private StationView_S1 stationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_test);

        init();
        setData();
        setListener();
    }

    private void init() {
        presenter = new VirgoPresenter(getApplicationContext());
        presenter.setCallBack(this);

        stationView = findViewById(R.id.stationView);
        stationView.startInitial();
    }

    private void setData() {

    }

    private void setListener() {

    }

    public void onOpenFile(View view){
//        String path = FileUtil.getRootPath() + "/Download";
//        File file = new File(path);
//        if(file.exists()){
//            Log.i(TAG, "onOpenFile: " + path);
//            for(File f: Objects.requireNonNull(file.listFiles())){
//                Log.i(TAG, "list: " + f.getPath());
//            }
//        }

        String path = getApplicationContext().getExternalFilesDir(null).getAbsolutePath();
        Log.i(TAG, "onOpenFile: " + path);
    }

    public void onUploadFile(View view){
        File file = new File(FileUtil.getRootPath()+File.separator+"image.jpg");
        if(file.exists()){
            Log.i(TAG, "onUploadFile: ");
            presenter.uoloadImage(file);
        }

//        String path = FileUtil.getRootPath() + "/Download";
//        File file = new File(path);
//        if(file.exists()){
//            Log.i(TAG, "onOpenFile: " + path);
//            for(File f: Objects.requireNonNull(file.listFiles())){
//                Log.i(TAG, "list: " + f.getPath());
//            }
//        }
    }

    @Override
    public void onSuccess(String tag, String json) {
        Log.i(TAG, "onSuccess: " + json);
    }

    @Override
    public void onFail(String tag, String error) {
        Log.i(TAG, "onFail: " + error);
    }
}