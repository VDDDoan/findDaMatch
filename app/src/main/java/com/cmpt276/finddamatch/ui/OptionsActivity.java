/*
    UI class for changing the game settings
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.Options;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        updateScreen();
        onClickListener(R.id.btn_imageSet0,R.id.btn_imageSet1, 0);
        onClickListener(R.id.btn_imageSet1,R.id.btn_imageSet0, 1);

        Button flickr = findViewById(R.id.btn_flickr_imageSet);
        flickr.setOnClickListener(v -> {
            Intent intent = new Intent(OptionsActivity.this, FlickrDeckActivity.class);
            startActivity(intent);
        });


        ImageView returnBtn = findViewById(R.id.btn_options_back);
        returnBtn.setOnClickListener(v -> finish());

    }

    private void onClickListener(int btnImgSet, int btnOtherImgSet, int selectedIndex) {
        Button clickedButton = findViewById(btnImgSet);
        Button otherButton = findViewById(btnOtherImgSet);

        clickedButton.setOnClickListener(v ->{
            Options.getInstance().setImageSetIndex(selectedIndex);
            clickedButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCharcoalLite)));
            otherButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCharcoal)));
        });


    }

    private void updateScreen() {
        Button btnImgSet0 = findViewById(R.id.btn_imageSet0);
        Button btnImgSet1 = findViewById(R.id.btn_imageSet1);

        int selectedImgIndex = Options.getInstance().getImageSetIndex();
        switch (selectedImgIndex){
            case 0:
                btnImgSet0.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCharcoalLite)));
                break;
            case 1:
                btnImgSet1.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCharcoalLite)));
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_top);
    }
}