package com.cmpt276.finddamatch.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cmpt276.finddamatch.R;

public class PhotoGalleryActivity extends AppCompatActivity implements DialogFlickrSearch.SearchDialogListener {
    private static String searchWord;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, PhotoGalleryActivity.class);
        return intent;
    }

    public static String getWords(){
        return searchWord;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_photo_gallery);

        ImageView search = findViewById(R.id.search_Button);
        search.setColorFilter(Color.argb(255, 255, 255, 255));
        search.setOnClickListener(v->{
            showDialog();
        });
        showDialog();
    }
    private saveInterface listener;

    public interface saveInterface {
        void saveImagesToDeck();
    }

    @Override
    public void onFinishSearchDialog(String inputText, boolean newSearch) {
        searchWord = inputText;
        Toast.makeText(this,searchWord, Toast.LENGTH_LONG).show();
        if (newSearch){
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.fragment_container);

            if (fragment == null) {
                fragment = PhotoGalleryFragment.newInstance();
                fm.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            } else {
                fragment = PhotoGalleryFragment.newInstance();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
            listener = (saveInterface) fragment;
        }

        ImageView done = findViewById(R.id.btn_save_images);
        done.setOnClickListener(v -> {
            listener.saveImagesToDeck();
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showDialog(){
        DialogFlickrSearch dialog = new DialogFlickrSearch();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(),"dialog");
    }
}
