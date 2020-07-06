/*
    Original python script written by WRadigan:
    https://github.com/WRadigan/pySpot-It

    Translated into Java and modified for Find daMatch game
*/

package com.cmpt276.finddamatch.model;

import java.util.ArrayList;
import java.lang.Math;

public class CardGenerator {

    // actual class stuff
    public CardGenerator() {
    }

    /*
        takes the order and if its prime
        returns an ArrayList cards x image index, new row every (order + 1) elements,
        first element is the order + 1
        else it returns null
        where the order is a prime number and order + 1 = number of images per card

        *original spot it has 55 cards and 57 images but mathematically the
        number of cards and images should equal to guarantee only 1 shared image per card
    */
    public ArrayList<Integer> create_cards(int order) {
        int min_factor;
        boolean isPrime = true;
        for (min_factor = 2; min_factor < 1 + (int) Math.sqrt(order); min_factor++ ) {
            if (order % min_factor == 0) {
                isPrime = false;
                break;
            }
        }
        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.add(order + 1);

        if (!isPrime) {
            System.out.println("\nERROR: order must be prime\n");
            return null;
        } else {
            min_factor = order;
        }

        for (int i = 0; i < order; i++) {
            for (int j = 0; j < order; j++) {
                cards.add(i * order + j);
            }
            cards.add(order * order);
        }

        for (int i = 0; i < min_factor; i++) {
            for (int j = 0; j < order; j++) {

                for (int k = 0; k < order; k++) {
                    cards.add(k * order + (j + i * k) % order);
                }
                cards.add(order * order + 1 + i);
            }
        }

        for (int i = 0; i < min_factor + 1; i++) {
            cards.add(order * order + i);
        }

        return cards;
    }
}