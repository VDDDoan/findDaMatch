package com.cmpt276.finddamatch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.finddamatch.R;

public class PhotoGalleryActivity extends AppCompatActivity implements DialogFlickrSearch.SearchDialogListener {
    private String searchWord;
    private ImageView search;

    public static Intent newIntent(Context packageContext){
        Intent intent = new Intent(packageContext, PhotoGalleryActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flickr_container);
        search = findViewById(R.id.search_Button);
        search.setOnClickListener(v->{
            showDialog();
        });
        showDialog();
        Toast.makeText(this,searchWord, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFinishSearchDialog(String inputText) {
        searchWord = inputText;
    }

    private void showDialog(){
        DialogFlickrSearch dialog = new DialogFlickrSearch();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(),"dialog");
    }
}
