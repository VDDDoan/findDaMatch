/*
    UI class for displaying the main menu and linking to all other UI activities
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        cloud = findViewById(R.id.img_menu_cloud);
        play = findViewById(R.id.btn_menu_game);
        play.setOnClickListener((View v)->{
            Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        });
        option = findViewById(R.id.btn_menu_option);
        option.setOnClickListener((View v)->{
            Intent intent = new Intent(MainMenuActivity.this, OptionsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        });
        score = findViewById(R.id.btn_menu_score);
        score.setOnClickListener((View v)->{
            Intent intent = new Intent(MainMenuActivity.this, HighScoreActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        });
        help = findViewById(R.id.btn_menu_help);
        help.setOnClickListener((View v)->{
            Intent intent = new Intent(MainMenuActivity.this, HelpActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        });

        ObjectAnimator animationX = ObjectAnimator.ofFloat(cloud, "x", cloud.getLeft(), 650f);
        animationX.setDuration(10000);
        animationX.setRepeatMode(ObjectAnimator.REVERSE);
        animationX.setRepeatCount(ObjectAnimator.INFINITE);
        animationX.start();
    }

}