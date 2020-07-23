package com.cmpt276.finddamatch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
        Toast.makeText(this,searchWord, Toast.LENGTH_LONG).show();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.flickr_photo_recycler_view);

        if (fragment == null) {
            fragment = PhotoGalleryFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.flickr_photo_recycler_view, fragment)
                    .commit();
        }
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
