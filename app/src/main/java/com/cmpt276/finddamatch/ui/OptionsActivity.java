/*
    UI class for changing the game settings
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cmpt276.finddamatch.R;

public class OptionsActivity extends AppCompatActivity {
    private Button imgChange;
    private Button returnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        imgChange = findViewById(R.id.changeImgBtn);
        returnBtn = findViewById(R.id.returnBtn);

        imgChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Change integer array set somehow
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