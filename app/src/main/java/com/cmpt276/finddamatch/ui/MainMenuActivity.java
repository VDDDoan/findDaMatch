/*
    UI class for displaying the main menu and linking to all other UI activities
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.cmpt276.finddamatch.R;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }
}