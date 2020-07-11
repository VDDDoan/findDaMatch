/*
    Class which handles the 5 top highscores
    can overwrite one of the current high scores with a better one
 */
package com.cmpt276.finddamatch.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HighScoreManager {
    private static final int NUM_HIGH_SCORES = 5;

    private List<HighScore> highScores = new ArrayList<>();

    private static HighScoreManager instance;

    public static HighScoreManager getInstance() {
        if (instance == null) {
            instance = new HighScoreManager();
          /*  for (int i = 0; i < NUM_HIGH_SCORES; i++) {
                instance.highScores.add(new HighScore(
                        i * 1000 + 1000,
                        String.valueOf(i),
                        DateFormat.getDateInstance().format(Calendar.getInstance().getTime())));
            }
           */
          instance.highScores.add(new HighScore(
                    180,
                    "Mr.Panda",
                    DateFormat.getDateInstance().format(Calendar.getInstance().getTime())));
            instance.highScores.add(new HighScore(
                    180,
                    "Mr.James",
                    DateFormat.getDateInstance().format(Calendar.getInstance().getTime())));
            instance.highScores.add(new HighScore(
                    180,
                    "Mr.David",
                    DateFormat.getDateInstance().format(Calendar.getInstance().getTime())));
            instance.highScores.add(new HighScore(
                    180,
                    "Mr.Vinesh",
                    DateFormat.getDateInstance().format(Calendar.getInstance().getTime())));
            instance.highScores.add(new HighScore(
                    180,
                    "Mr.Brain",
                    DateFormat.getDateInstance().format(Calendar.getInstance().getTime())));
        }
        return instance;
    }

    public static int getNumHighScores() {
        return NUM_HIGH_SCORES;
    }

    public List<HighScore> getHighScores() {
        return highScores;
    }

    // inserts newScore into highScores before the next best (lowest) time
    // removes the high score with the greatest time
    public void setHighScore(HighScore newScore) {
        for (int i = 0; i < highScores.size(); i++) {
            if (newScore.getTime() < highScores.get(i).getTime()) {
                highScores.add(i, newScore);
                highScores.remove(highScores.size() - 1);
                break;
            }
        }
    }
}
