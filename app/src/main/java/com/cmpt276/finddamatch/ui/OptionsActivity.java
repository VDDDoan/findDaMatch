/*
    UI class for changing the game settings
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.ConstraintHorizontalLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.Options;

public class OptionsActivity extends AppCompatActivity {
    private Button imgChange;
    private Button returnBtn;
    private Options option;
    private LinearLayout layout;

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
                option = option.getInstance();
                int currentImgSet = option.getImageSetIndex();
                switch (currentImgSet){
                    case 0:
                        option.setImageSetIndex(1);
                        break;
                    case 1:
                        option.setImageSetIndex(0);
                }
                Toast.makeText(OptionsActivity.this,"Imgdex: "+ option.getImageSetIndex(),
                        Toast.LENGTH_SHORT).show();

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