/*
    Singleton class that controls the images that the game will use
 */
package com.cmpt276.finddamatch.model;

public class Options {
    private static Options instance;
    private int imageSetIndex = 0;
    private int numCardsPerSet = 7;
    private int numImagesPerCard = 3;

    public static Options getInstance() {
        if(instance == null) {
            instance = new Options();
        }
        return instance;
    }
    public Options() {

    }

    public int getNumImagesPerCard() {
        return numImagesPerCard;
    }

    public int getNumCardsPerSet() {
        return numCardsPerSet;
    }

    public int getImageSetIndex() {
        return imageSetIndex;
    }

    public void setImageSetIndex(int imageSetIndex) {
        this.imageSetIndex = imageSetIndex;
    }

    public void setNumImagesPerCard(int numOfImages) {
        this.numImagesPerCard = numOfImages;
    }

    public void setNumCardsPerSet(int numCardsPerSet) {
        this.numCardsPerSet = numCardsPerSet;
    }

}
