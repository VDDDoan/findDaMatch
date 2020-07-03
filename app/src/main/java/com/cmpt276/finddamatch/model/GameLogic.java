/*
    class which handles the game logic
 */
package com.cmpt276.finddamatch.model;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class GameLogic {
    private Options options = Options.getInstance();

    // createDeck calls the cardGenerator and places cards into a 2d array of card x image index;
    private int[][] createDeck() {
        CardGenerator cardGenerator = new CardGenerator();
        ArrayList<Integer> cards = cardGenerator.create_cards(options.getNumImagesPerCard() - 1);

        int numImagesPerSet = options.getNumImagesPerSet();
        int numImagesPerCard = options.getNumImagesPerCard();
        int[][] deck = new int[numImagesPerSet][numImagesPerCard];

        for (int i = 0; i < numImagesPerSet; i++) {
            for (int j = 0; j < numImagesPerCard; j++) {
                deck[i][j] = cards.get(i + j);
            }
        }
        return deck;
    }

    public GameLogic() {

    }

    public boolean isMatch(Drawable image) {

        return true;
    }
}
