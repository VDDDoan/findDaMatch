/*
    Singleton class that controls the images that the game will use
 */
package com.cmpt276.finddamatch.model;

public class Options {
    private static Options instance;
    private int imageSetIndex;
    private static final int NUM_CARDS_PER_SET = 7;
    private static final int NUM_IMAGES_PER_CARD = 3;

    public static Options getInstance() {
        if(instance == null) {
            instance = new Options();
        }
        return instance;
    }
    public Options() {

    }

    public int getNumImagesPerCard() {
        return NUM_IMAGES_PER_CARD;
    }

    public int getNumCardsPerSet() {
        return NUM_CARDS_PER_SET;
    }

    public int getImageSetIndex() {
        return imageSetIndex;
    }

    public void setImageSetIndex(int imageSetIndex) {
        this.imageSetIndex = imageSetIndex;
    }


}
