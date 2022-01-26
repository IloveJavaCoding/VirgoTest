package com.harine.virgotest.ui;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.harine.virgotest.R;
import com.nepalese.virgosdk.Util.FileUtil;

import java.io.File;
import java.util.Locale;

//需要文件读取权限
public class Text2SpeechActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static final String TAG = "Text2SpeechActivity";

    private Button bSpeak, bSave;
    private TextView tvPitch, tvSpeed;
    private SeekBar sbPitch, sbSpeed;
    private EditText etInput;
    private TextToSpeech tts;

    private float mPitch, mSpeed;
    private final String uttId = "speech_1";
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text2_speech);

        init();
        setData();
        setListener();
    }

    private void init() {
        tts = new TextToSpeech(this, this);
        path = FileUtil.getRootPath() + "/Download/ttSpeach";
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

        etInput = findViewById(R.id.enterYouText);
        tvPitch = findViewById(R.id.tvPitch);
        tvSpeed = findViewById(R.id.tvSpeed);

        sbPitch = findViewById(R.id.sbPitch);
        sbSpeed = findViewById(R.id.sbSpeed);
        bSpeak = findViewById(R.id.buttonSpeak);
        bSave = findViewById(R.id.buttonSave);
    }

    private void setData() {
        mPitch = 1f;
        mSpeed = 1f;
    }

    private void setListener() {
        bSpeak.setOnClickListener(view -> speakOut());

        bSave.setOnClickListener(v -> savetLocal());

        //音调
        // Speech pitch. {@code 1.0} is the normal pitch,
        // lower values lower the tone of the synthesized voice,
        // greater values increase it.
        sbPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPitch.setText("( " + progress/10f + " )");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //0-2
                mPitch = seekBar.getProgress()/10f;
            }
        });

        //语速
        //Speech rate. {@code 1.0} is the normal speech rate,
        //lower values slow down the s peech ({@code 0.5} is half the normal speech rate),
        //greater values accelerate it ({@code 2.0} is twice the normal speech rate).
        sbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSpeed.setText("( " + progress/10f + " )");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSpeed = seekBar.getProgress()/10f;
            }
        });
    }

    private void speakOut() {
        //具体到每一个id上的监听 可有可无
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            //开始播放时调用
            @Override
            public void onStart(String utteranceId) {
                final String keyword = utteranceId;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(getApplicationContext(), "Started：" + keyword, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //播放完成时调用
            @Override
            public void onDone(String utteranceId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(getApplicationContext(), "Done ", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //播放错误时调用
            @Override
            public void onError(String utteranceId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        tts.setPitch(mPitch);
        tts.setSpeechRate(mSpeed);

        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, uttId);//can be null

        String text = etInput.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, uttId);
    }

    private void savetLocal() {
        tts.setPitch(mPitch);
        tts.setSpeechRate(mSpeed);

        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, uttId);//can be null

        String text = etInput.getText().toString();
        File file = new File(path + File.separator + System.currentTimeMillis() + "_.mp3");
        tts.synthesizeToFile(text, params, file, uttId);
    }


    //注销
    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.CHINA);//设置语言（中英都能说？）

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getApplicationContext(), "Language not supported", Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "onInit: 初始化成功");
                bSpeak.setEnabled(true);
                bSave.setEnabled(true);
            }
        } else {
            Log.i(TAG, "onInit: " + status);
            Toast.makeText(getApplicationContext(), "Init failed", Toast.LENGTH_SHORT).show();
        }
    }
}