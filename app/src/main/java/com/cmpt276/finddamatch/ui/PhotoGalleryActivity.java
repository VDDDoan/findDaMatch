package com.cmpt276.finddamatch.ui;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class PhotoGalleryActivity extends AppCompatActivity {

    public static Intent newIntent(Context packageContext){
        Intent intent = new Intent(packageContext, PhotoGalleryActivity.class);
        return intent;
    }
}
