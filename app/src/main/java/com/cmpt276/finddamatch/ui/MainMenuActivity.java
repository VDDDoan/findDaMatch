/*
    UI class for displaying the main menu and linking to all other UI activities
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
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
        cloud = findViewById(R.id.cloud);
        play = findViewById(R.id.newGame);
        play.setOnClickListener((View v)->{
            Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);
            startActivity(intent);
        });
        option = findViewById(R.id.options);
        option.setOnClickListener((View v)->{
            Intent intent = new Intent(MainMenuActivity.this, OptionsActivity.class);
            startActivity(intent);
        });
        score = findViewById(R.id.highScore);
        score.setOnClickListener((View v)->{
            Intent intent = new Intent(MainMenuActivity.this, HighScoreActivity.class);
            startActivity(intent);
        });
        help = findViewById(R.id.help);
        help.setOnClickListener((View v)->{
            Intent intent = new Intent(MainMenuActivity.this, HelpActivity.class);
            startActivity(intent);
        });

        ObjectAnimator animationX = ObjectAnimator.ofFloat(cloud, "x", cloud.getLeft(), 650f);
        animationX.setDuration(10000);
        animationX.setRepeatMode(ObjectAnimator.REVERSE);
        animationX.setRepeatCount(ObjectAnimator.INFINITE);
        animationX.start();
    }

}