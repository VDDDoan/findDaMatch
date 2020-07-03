/*
    UI class for displaying the main menu and linking to all other UI activities
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.cmpt276.finddamatch.R;

public class MainMenuActivity extends AppCompatActivity {
    private ImageView cloud;
    private Button play;
    private Button option;
    private Button score;
    private Button help;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }
}