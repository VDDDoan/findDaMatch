/*
    class which handles the game logic
 */
package com.cmpt276.finddamatch.model;

import java.util.ArrayList;
import java.util.Random;

public class GameLogic {
    private Options options = Options.getInstance();
    private int numCardsPerSet = options.getNumCardsPerSet();
    private int numImagesPerCard = options.getNumImagesPerCard();
    private int currentCard;
    private static final int NUM_SWAPS_IN_SHUFFLE = 50;

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

    // scans the next card in the deck for a matching image to the picked one
    // returns true if there is and false otherwise
    public boolean isMatch(int input) {
        boolean hasMatch = false;

        for (int i = 0; i < numImagesPerCard; i++) {
            if (input == currentCard + 1) {
                hasMatch = true;
                break;
            }
        }

        return hasMatch;
    }
}
