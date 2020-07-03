package com.cmpt276.finddamatch.model;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.util.Calendar;

public class HighScore {
    private final Calendar calendar = Calendar.getInstance();

    private long time;
    private String nickname;
    private String date;


    // set default high score
    public HighScore() {
        time = 0;
        nickname = "John Doe";
        date = DateFormat.getDateInstance().format(calendar.getTime());
    }

    public HighScore(int time, String nickname, String date) {
        this.time = time;
        this.nickname = nickname;
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
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
