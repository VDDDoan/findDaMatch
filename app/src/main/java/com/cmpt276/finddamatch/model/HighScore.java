/*
    Class that represents a single High score
 */
package com.cmpt276.finddamatch.model;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.util.Calendar;

public class HighScore {
    private long time;
    private String nickname;
    private String date;


    // set default high score
    public HighScore() {
        time = 0;
        nickname = "John Doe";
        date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
    }

    public HighScore(long time, String nickname, String date) {
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
        long minutes = (time / 1000) / 60;
        long seconds = (time / 1000) % 60;
        String scoreString = minutes + "m " + seconds + "s " + nickname + " on " + date;
        return scoreString;
    }
}