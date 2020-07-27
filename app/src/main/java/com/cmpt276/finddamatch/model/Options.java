/*
    Singleton class that controls the images that the game will use
 */
package com.cmpt276.finddamatch.model;

public class Options {
    private static Options instance;
    private static final int FLICKR_DECK_INDEX = 2;
    private int imageSetIndex = 0;
    private int numCardsPerSet = 7;
    private int numImagesPerCard = 3;
    private int gameMode = 0;



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

    public int getOrderNum(){
        return numImagesPerCard - 1;
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

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public int getFlickrIndex() {
        return FLICKR_DECK_INDEX;
    }

}
