package com.harine.virgotest.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

import com.harine.virgotest.R;
import com.harine.virgotest.component.ScheTextView;

public class ScrollTextActivity extends AppCompatActivity {

    private ScheTextView textView;
    private EditText editText;
    private boolean hasStart = false;
    private boolean hasPause = false;
    private String PLAY_TEXT = "Leading文档说的很含糊，其实是上一行字符的descent到下一行的ascent之间的距离";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_text);

        editText = findViewById(R.id.txInput);
        textView = findViewById(R.id.scheTV);
    }

    public void onSetText(View view){
        PLAY_TEXT = editText.getText().toString().trim();
        if(hasStart){
            textView.startPlay(PLAY_TEXT);
        }
    }

    public void onPlayOrStop(View view){
        if(hasStart){
            if(hasPause){
                hasPause = false;
                textView.continuePlay();
            }else{
                hasPause = true;
                textView.pausePlay();
            }
        }else{
            if (TextUtils.isEmpty(PLAY_TEXT)) {
                editText.setError("请输入有效文本！");
            }else{
                hasStart = true;
                textView.startPlay(PLAY_TEXT);
            }
        }
    }
}