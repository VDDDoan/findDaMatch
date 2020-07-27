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
import com.cmpt276.finddamatch.model.HighScore;
import com.cmpt276.finddamatch.model.HighScoreManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class MainMenuActivity extends AppCompatActivity {
    private int locker = 0;
    private ImageView cloud;
    private Button play;
    private Button option;
    private Button score;
    private Button help;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        if(locker == 0) {
            try {
                ManagerUpdate();
                locker++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
         */
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

    private void ManagerUpdate() throws IOException {
        String filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord.txt";//path of file
        File file = new File(filename);
        Scanner inputStream = null;
        FileInputStream fis = null;
        BufferedReader br = null;
        String str;
        try {
            //load file and pop up
            inputStream = new Scanner(new FileInputStream(filename));
            int i = 1;
            fis = new FileInputStream(filename);
            br = new BufferedReader(new InputStreamReader(fis));
            while ((str = br.readLine()) != null) {
                String[] record = str.split(" ");
                List<String> recordlist = Arrays.asList(record);
                System.out.println(str);
                HighScore newScore = new HighScore(Long.parseLong(recordlist.get(1)), recordlist.get(0), recordlist.get(2) + recordlist.get(3) + recordlist.get(4));
                HighScoreManager.getInstance().forcedHighScore(newScore);
                i++;
            }
            HighScoreManager.getInstance().mangerSort();
            fis.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
    }
    @Override
    public void onBackPressed(){
        this.finishAffinity();
    }
}