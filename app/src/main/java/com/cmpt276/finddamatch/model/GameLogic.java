/*
    class which handles the game logic
 */
package com.cmpt276.finddamatch.model;

import android.os.SystemClock;
import android.widget.Chronometer;

import java.util.ArrayList;
import java.util.Random;

public class GameLogic {
    private static final int NUM_SWAPS_IN_SHUFFLE = 50;

    private int numCardsPerSet = Options.getInstance().getNumCardsPerSet();

    public int getNumImagesPerCard() {
        return numImagesPerCard;
    }

    private int numImagesPerCard= Options.getInstance().getNumImagesPerCard();

    private long time;

    private int currentCardIndex;
    private int[][] deck;

    // createDeck calls the cardGenerator and places cards into a 2d array of card x image index;
    // i.e. returns 2d array with each card represented as respective row contents
    private int[][] createDeck() {
        CardGenerator cardGenerator = new CardGenerator();
        ArrayList<Integer> cards = cardGenerator.create_cards(numImagesPerCard - 1);

        int[][] deck = new int[numCardsPerSet][numImagesPerCard];

        for (int i = 0; i < numCardsPerSet; i++) {
            for (int j = 0; j < numImagesPerCard; j++) {
                deck[i][j] = cards.get(i * numImagesPerCard + j);
            }
        }
        return deck;
    }


    public GameLogic() {
        deck = createDeck();
    }

    public int[][] getDeck(){
        return deck;
    }

    // returns array of image indices for the card in deck at index;
    public int[] getCard(int index) {
        int[] card = new int[numImagesPerCard];
        System.arraycopy(deck[index], 0, card, 0, deck[0].length);
        return card;
    }

    public int getCurrentCardIndex() {
        return currentCardIndex;
    }

    public void incrementCurrentCardIndex() {
        currentCardIndex++;
    }

    public void setCurrentCardIndex(int currentCardIndex) {
        this.currentCardIndex = currentCardIndex;
    }

    // start timing the player
    public void startTimer(Chronometer timer) {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
    }

    // stop timing and record time
    public void stopTimer(Chronometer timer) {
        timer.stop();
        time = SystemClock.elapsedRealtime() - timer.getBase();
    }

    public long getTime() {
        return time;
    }

    // takes deck array and returns the deck with rows swapped randomly
    public int[][] shuffleDeck(int[][] deck) {
        Random rand = new Random();

        // randomly choose rows and swap them
        int temp;
        int randRow1, randRow2;
        for (int i = 0; i < NUM_SWAPS_IN_SHUFFLE; i++) {
            randRow1 = rand.nextInt(numCardsPerSet);
            randRow2 = rand.nextInt(numCardsPerSet);
            while (randRow1 == randRow2) {
                randRow2 = rand.nextInt(numCardsPerSet);
            }
            for (int column = 0; column < deck[0].length; column++) {
                temp = deck[randRow1][column];
                deck[randRow1][column] = deck[randRow2][column];
                deck[randRow2][column] = temp;
            }
        }

        return deck;
    }

    public void shuffleDeck() {
        Random rand = new Random();
        // randomly choose rows and swap them
        int temp;
        int randRow1, randRow2;
        for (int i = 0; i < NUM_SWAPS_IN_SHUFFLE; i++) {
            randRow1 = rand.nextInt(numCardsPerSet);
            randRow2 = rand.nextInt(numCardsPerSet);
            while (randRow1 == randRow2) {
                randRow2 = rand.nextInt(numCardsPerSet);
            }
            for (int column = 0; column < deck[0].length; column++) {
                temp = deck[randRow1][column];
                deck[randRow1][column] = deck[randRow2][column];
                deck[randRow2][column] = temp;
            }
        }
    }

    // scans the next card in the deck for a matching image to the picked one
    // if there's a match increment currentCard and return true, return false otherwise
    public boolean isMatch(int input) {
        boolean hasMatch = false;

        for (int i = 0; i < numImagesPerCard; i++) {
            if (input == deck[currentCardIndex - 1][i]) {
                hasMatch = true;
                break;
            }
        }

        return hasMatch;
    }

}
