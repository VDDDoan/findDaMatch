/*
    Singleton class that controls the images that the game will use
 */
package com.cmpt276.finddamatch.model;

public class Options {
    private static Options instance;
    private int imageSetIndex;
    private int[][] imageSet;
    private static final int NUM_IMAGES_PER_SET = 7;
    private static final int NUM_IMAGES_PER_CARD = 3;
    private static final int NUM_IMAGE_SETS = 2;

    public static Options getInstance() {
        if(instance == null) {
            instance = new Options();
        }
        return instance;
    }
    public Options() {
        imageSetIndex = 0;
        imageSet = new int[NUM_IMAGE_SETS][NUM_IMAGES_PER_SET];
        /*
            for imageSet elements
                imageSet[i][j] = The corresponding R.drawable.image for [set][image]
         */
    }

    public int getNumImagesPerCard() {
        return NUM_IMAGES_PER_CARD;
    }

    public int getNumImagesPerSet() {
        return NUM_IMAGES_PER_SET;
    }

    public int getImageSetIndex() {
        return imageSetIndex;
    }

    public void setImageSetIndex(int imageSetIndex) {
        this.imageSetIndex = imageSetIndex;
    }


}
