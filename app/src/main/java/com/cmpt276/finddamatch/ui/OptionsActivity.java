/*
    UI class for changing the game settings
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cmpt276.finddamatch.R;

public class OptionsActivity extends AppCompatActivity {
    private Button imgChange;
    private Button clrHiScore;
    private Button returnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        imgChange = findViewById(R.id.changeImgBtn);
        clrHiScore = findViewById(R.id.clearHiScore);
        returnBtn = findViewById(R.id.returnBtn);

        imgChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Does nothing rn
            }
        });

        clrHiScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Does nothing rn
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}