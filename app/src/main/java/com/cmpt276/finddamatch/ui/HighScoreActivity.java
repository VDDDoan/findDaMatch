/*
    UI class for displaying the high scores
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cmpt276.finddamatch.R;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class HighScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
    }

    // record record (statue, time)
    public void GameRecord(int time, String usrName) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String filename = getExternalCacheDir().getAbsolutePath() + "/gameRecord.txt";//record the path of file
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //record the new record
        try {
            fos = new FileOutputStream(filename, true);
            pw = new PrintWriter(fos);
            pw.println(hour + ':' + minute + ' '+ usrName + ' ' + time + "   on  " + month +',' + day + ',' + year );
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }
    }

    //Record view
    public void RecordView(View view) throws IOException {
        String filename = getExternalCacheDir().getAbsolutePath() + "/gameRecord.txt";//path of file
        File file = new File(filename);
        Scanner inputStream = null;
        FileInputStream fis = null;
        BufferedReader br = null;
        String str;
        String message = "               High Record                    \n";
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

        try {
            //load file and pop up
            inputStream = new Scanner(new FileInputStream(filename));
            int i = 1;
            fis = new FileInputStream(filename);
            br = new BufferedReader(new InputStreamReader(fis));
            while ((str = br.readLine()) != null) {
                message = message + (i + ".  " + str + "\n");
                i++;
            }
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setNegativeButton("Yes", null)
                    .create().show();
            fis.close();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
    }

    public void reset(View view) throws IOException {
        String filename = getExternalCacheDir().getAbsolutePath() + "/gameRecord.txt";//path of file
        File dir = new File(filename);
        dir.createNewFile();
        GameRecord(180,"Mr.Panda");
        GameRecord(180,"Mr.James");
        GameRecord(180,"Mr.David");
        GameRecord(180,"Mr.Vinesh");
        GameRecord(180,"Mr.Brain");
    }

    public void back(View view) {
        finish();
    }

}