package com.cmpt276.finddamatch.model;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.util.Calendar;

public class HighScore {
    private static HighScore instance;
    private Calendar calendar = Calendar.getInstance();

    private int time;
    private String nickname;
    private String date;

    public static HighScore getInstance() {
        if (instance == null) {
            instance = new HighScore();
        }
        return instance;
    }

    // set default high scores
    public HighScore() {
        time = 0;
        nickname = "John Doe";
        date = DateFormat.getDateInstance().format(calendar.getTime());
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
