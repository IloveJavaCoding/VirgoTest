package com.harine.virgotest.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.harine.virgotest.R;
import com.harine.virgotest.component.VirgoRippleView;
import com.harine.virgotest.component.VirgoTicToe;

import androidx.appcompat.app.AppCompatActivity;

public class ComponentActivity extends AppCompatActivity implements VirgoTicToe.ticToeCallback{

    private VirgoTicToe ticToe;
    private TextView tvNotice;
    private Switch aSwitch;

    private VirgoRippleView rippleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component);

        init();
        setData();
        setListener();
    }

    private void init() {
        ticToe = findViewById(R.id.tictoe);
        tvNotice = findViewById(R.id.tvNotice);
        aSwitch = findViewById(R.id.switchAI);

        rippleView = findViewById(R.id.rippleView);
    }

    private void setData() {
        ticToe.setCallback(this);
    }

    private void setListener() {
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> ticToe.setAIMode(isChecked));
    }

    public void onRestart(View view){
        ticToe.restartGame();
    }

    public void onStart(View view){
        rippleView.startAnimotor();
    }

    public void onStop(View view){
        rippleView.stopAnimator();
    }

    @Override
    public void gameOver(int a) {
        switch (a){
            case VirgoTicToe.WIN_O:
                tvNotice.setText("O 赢");
                break;
            case VirgoTicToe.WIN_X:
                tvNotice.setText("X 赢");
                break;
            case VirgoTicToe.WIN_DRAW:
                tvNotice.setText("平局");
                break;
        }
    }

    @Override
    public void nextPlay(boolean isOGo) {
        if(isOGo){
            tvNotice.setText("O 走");
        }else{
            tvNotice.setText("X 走");
        }
    }
}