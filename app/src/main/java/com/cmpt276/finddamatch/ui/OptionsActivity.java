/*
    UI class for changing the game settings
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.Options;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Button btnImageSet0 = findViewById(R.id.btn_imageSet0);
        Button btnImageSet1 = findViewById(R.id.btn_imageSet1);

        if (Options.getInstance().getImageSetIndex() == 0) {
            btnImageSet0.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCharcoalLite)));
        } else {
            btnImageSet1.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCharcoalLite)));
        }


        btnImageSet0.setOnClickListener(v -> {
            Options.getInstance().setImageSetIndex(0);
            btnImageSet0.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCharcoalLite)));
            btnImageSet1.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCharcoal)));
        });

        btnImageSet1.setOnClickListener(v -> {
            Options.getInstance().setImageSetIndex(1);
            btnImageSet0.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCharcoal)));
            btnImageSet1.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCharcoalLite)));
        });

        ImageView returnBtn = findViewById(R.id.btn_options_back);
        returnBtn.setOnClickListener(v -> finish());

    }


}