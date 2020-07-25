package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.cmpt276.finddamatch.R;

public class FlickrDeckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_deck);

        ImageView add = findViewById(R.id.btn_addtoflickrdeck);
        add.setOnClickListener(v -> {
            Intent intent = new Intent(FlickrDeckActivity.this, PhotoGalleryActivity.class);
            startActivity(intent);
        });
    }
}