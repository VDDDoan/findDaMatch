/*
    UI class for displaying the high scores
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.HighScore;
import com.cmpt276.finddamatch.model.HighScoreManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class HighScoreActivity extends AppCompatActivity {
    private ListView listView;
    private String[] scoreText = new String[5];
    // high score :     private long time;
    //                  private String nickname;
    //                  private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        populateListView();
    }


    //set list
    private void populateListView(){
        for(int i = 0; i < HighScoreManager.getNumHighScores(); i++) {
            scoreText[i] = HighScoreManager.getInstance().getHighScores().get(i).toString();
        }
        System.out.println(scoreText);
        //Build Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.da_item,
                scoreText);


        //Configure the list view
        listView = findViewById(R.id.list_score_scores);
        listView.setAdapter(adapter);

    }


    // record record (time,usrName,date) save it in manager and .txt file
    public void GameRecord(long time, String usrName) {
        Calendar c = Calendar.getInstance();
        // require date from calendar, and add this towards high_score Manager
        String date = DateFormat.getDateInstance().format(c.getTime());
        HighScore newScore = new HighScore(time,usrName,date);
        HighScoreManager.getInstance().forcedHighScore(newScore);

        String filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord.txt";//record the path of file
        FileOutputStream fos;
        FileInputStream fis;
        PrintWriter pw = null;
        BufferedReader br = null;
        //if the director path not exist, then build it
        File file = new File(getExternalCacheDir().getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            File dir = new File(filename);
            if (!dir.exists()) dir.createNewFile();//if record file not be created
                //if record file has been created, check the # of record, we need only 5 records
            else {
                LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
                lnr.skip(Long.MAX_VALUE);
                int i = lnr.getLineNumber();
                lnr.close();
                //write back to list
                if (i >= 5) {
                    fis = new FileInputStream(filename);
                    br = new BufferedReader(new InputStreamReader(fis));
                    i = 0;
                    String str = null;
                    ArrayList<String> list = new ArrayList<String>();
                    while ((str = br.readLine()) != null) {
                        if (i == 0) {
                            i++; continue;
                        }
                        i++;
                        list.add(str);
                    }
                    BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
                    for (String a : list) {
                        bw.write(a);
                        bw.newLine();
                    }
                    bw.close();
                    fis.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //record the new record
        try {
            fos = new FileOutputStream(filename, true);
            pw = new PrintWriter(fos);
            pw.println(usrName +  ' '  + time +  ' ' + date);
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            assert pw != null;
            pw.close();
        }
    }

    //Record view
    public void RecordView(View view) throws IOException {
        String filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord.txt";//path of file
        File file = new File(filename);
        Scanner inputStream = null;
        FileInputStream fis = null;
        BufferedReader br = null;
        String str;
        String message = "                          High Record                    \n";
        //if file not exist, No record! Then create default records
        if (!file.exists()) {
            File dir = new File(filename);
            dir.createNewFile();
            GameRecord(180,"Mr.Panda");
            GameRecord(180,"Mr.James");
            GameRecord(180,"Mr.David");
            GameRecord(180,"Mr.Vinesh");
            GameRecord(180,"Mr.Brain");
        }

    }

    public void reset(View view) throws IOException {
        String filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord.txt";//path of file
        File dir = new File(filename);
        dir.createNewFile();
        GameRecord(180,"Mr.Panda");
        GameRecord(180,"Mr.James");
        GameRecord(180,"Mr.David");
        GameRecord(180,"Mr.Vinesh");
        GameRecord(180,"Mr.Brain");
        populateListView();
    }

    public void back(View view) {
        this.finish();
    }

}