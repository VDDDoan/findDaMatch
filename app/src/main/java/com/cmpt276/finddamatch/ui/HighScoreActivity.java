/*
    UI class for displaying the high scores
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.HighScore;
import com.cmpt276.finddamatch.model.HighScoreManager;
import com.cmpt276.finddamatch.model.Options;

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
    private static int rollBackSign = 2;
    private Button orderBtn;

    // high score :     private long time;
    //                  private String nickname;
    //                  private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        orderBtn = findViewById(R.id.btn_orderHS);
        switch(rollBackSign){
            case 2:
                orderBtn.setText(R.string.order_2);
                break;
            case 3:
                orderBtn.setText(R.string.order_3);
                break;
            case 5:
                orderBtn.setText(R.string.order_5);
                break;
        }
        orderBtn.setOnClickListener(v -> {
            switch (rollBackSign) {
                case 2:
                    rollBackSign = 3;
                    orderBtn.setText(R.string.order_3);
                    try {
                        innerManagerUpdate(3);
                        populateListView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    rollBackSign = 5;
                    orderBtn.setText(R.string.order_5);
                    try {
                        innerManagerUpdate(5);
                        populateListView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    rollBackSign = 2;
                    orderBtn.setText(R.string.order_2);
                    try {
                        innerManagerUpdate(2);
                        populateListView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        });
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
                this, R.layout.layout_da_item, scoreText);


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

        String filename;
        if(rollBackSign == 2){
            filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord.txt";//record the path of file
        }
        else if(rollBackSign == 3){
            filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord3.txt";//record the path of file
        }
        else{
            filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord5.txt";//record the path of file
        }
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

    public void reset(View view) throws IOException {
        String filename;
        if(rollBackSign == 2){
            filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord.txt";//record the path of file
        }
        else if(rollBackSign == 3){
            filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord3.txt";//record the path of file
        }
        else{
            filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord5.txt";//record the path of file
        }
        File dir = new File(filename);
        dir.createNewFile();
        GameRecord(180,"Mr.Panda");
        GameRecord(180,"Mr.James");
        GameRecord(180,"Mr.David");
        GameRecord(180,"Mr.Vinesh");
        GameRecord(180,"Mr.Brain");
        populateListView();
    }

    private void innerManagerUpdate(int order) throws IOException {
        String filename;
        if(order == 2){
            filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord.txt";//record the path of file
        }
        else if(order == 3){
            filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord3.txt";//record the path of file
        }
        else{
            filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord5.txt";//record the path of file
        }
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
    }

    public void back(View view) {
        this.finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_top);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(HighScoreActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}