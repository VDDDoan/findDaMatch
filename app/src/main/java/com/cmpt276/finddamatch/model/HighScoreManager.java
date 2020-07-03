package com.cmpt276.finddamatch.model;

public class HighScoreManager {
    private static final int NUM_HIGH_SCORES = 5;

    private HighScore[] highScores = new HighScore[NUM_HIGH_SCORES];

    private static HighScoreManager instance;

    public static HighScoreManager getInstance() {
        if (instance == null) {
            instance = new HighScoreManager();
        }
        return instance;
    }

    public static int getNumHighScores() {
        return NUM_HIGH_SCORES;
    }

    public HighScore[] getHighScores() {
        return highScores;
    }

    public void setHighScore(HighScore newScore) {
        for (int i = NUM_HIGH_SCORES; i > 0; i++) {
            if (newScore.getTime() > highScores[i].getTime()) {
                if (i != NUM_HIGH_SCORES) {
                    highScores[i+1] = newScore;
                }
            }
        }
    }
}
