/*
    UI class for playing the game
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Chronometer;
import android.widget.TextView;

import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.GameLogic;
import com.cmpt276.finddamatch.model.Options;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private static final int TIME_FLIP_CARD_MS = 500;
    private static final int TIME_DEAL_CARD_MS = 500;
    private static final int TIME_SHUFFLE_CARD_MS = 500;
    private static final int MAX_SHUFFLE_DISPLACEMENT = 125;
    private static final int CARD_HAND = 0;
    private static final int CARD_PLAY = 1;
    private static final int CARD_DECK = 2;
    private static final int NUM_CARDS_IN_ACTIVITY = 3;
    private static final String TAG_CARD_FACE = "face";
    private static final String TAG_CARD_BACK = "back";

    private GameLogic gameLogic;
    private Chronometer timer;

    private float boardHeight;
    private float boardWidth;
    private float cardHeight;
    private float cardWidth;

    private TextView txtNumCardsRemaining;
    private CardCanvasView cardViewHand, cardViewPlay, cardViewDeck;
    private CardCanvasView[] deck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameLogic = new GameLogic();

        final ConstraintLayout gameBoard = findViewById(R.id.view_game_board);
        txtNumCardsRemaining = findViewById(R.id.txt_num_cards_remaining);
        timer = findViewById(R.id.timer_game);

        txtNumCardsRemaining.setText("Cards Remaining: " + (Options.getInstance().getNumCardsPerSet() - gameLogic.getCurrentCardIndex()));

        deck = new CardCanvasView[NUM_CARDS_IN_ACTIVITY];

        deck[CARD_HAND] = findViewById(R.id.view_card_hand);
        deck[CARD_PLAY] = findViewById(R.id.view_card_play);
        deck[CARD_DECK] = findViewById(R.id.view_card_deck);

        for (int i = 0; i < NUM_CARDS_IN_ACTIVITY; i++) {
            deck[i].setTag(TAG_CARD_BACK);
            deck[i].setBackgroundResource(R.drawable.menu_bg_card_back);
        }

        gameBoard.post(new Runnable() {
            @Override
            public void run() {
                boardHeight = (float) gameBoard.getHeight();
                boardWidth = (float) gameBoard.getWidth();
                cardHeight = (float) deck[0].getHeight();
                cardWidth = (float) deck[0].getWidth();
                for (int j = 0; j < NUM_CARDS_IN_ACTIVITY; j++) {
                    shuffleCardInDeck(deck[j]);
                }
            }
        });

        dealFirstCard(deck[CARD_HAND]);
    }

    // generates random float between +max and -max
    private float getShuffleAnimDisplacement(float max) {
        Random rand = new Random();
        float min = -max;
        return min + rand.nextFloat() * (max - min);
    }

    // takes a card and plays animation
    private void shuffleCardInDeck(final CardCanvasView card) {
        final float displacement = getShuffleAnimDisplacement(MAX_SHUFFLE_DISPLACEMENT);
        final ObjectAnimator shuffleAnim = ObjectAnimator.ofFloat(card, "translationX", displacement);
        shuffleAnim.setDuration(TIME_SHUFFLE_CARD_MS/2);
        shuffleAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                final ObjectAnimator shuffleAnimReverse = ObjectAnimator.ofFloat(card, "translationX", 0.0f);
                shuffleAnimReverse.setDuration(TIME_SHUFFLE_CARD_MS/2);
                shuffleAnimReverse.start();
            }
        });
        shuffleAnim.start();
    }

    // deals first card and starts the game
    private void dealFirstCard(final CardCanvasView card) {
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameLogic.getCurrentCardIndex() == 0) {
                    dealCard(card);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gameLogic.startTimer(timer);
                        }
                    }, TIME_DEAL_CARD_MS + TIME_FLIP_CARD_MS);
                } else {
                    flipCard(card);
                }
            }
        });
    }

    // deals next card from deck to hand
    private void dealCard(final CardCanvasView card) {
        ObjectAnimator dealAnimation = ObjectAnimator.ofFloat(card, "translationY", boardHeight/2);
        dealAnimation.setDuration(TIME_DEAL_CARD_MS);
        dealAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                flipCard(card);
            }
        });
        dealAnimation.start();
        gameLogic.incrementCurrentCardIndex();
        txtNumCardsRemaining.setText("Cards Remaining: " + (Options.getInstance().getNumCardsPerSet() - gameLogic.getCurrentCardIndex()));
    }

    // takes a card and flips it
    private void flipCard(final CardCanvasView card) {
        ObjectAnimator flipAnimation = ObjectAnimator.ofFloat(card, "rotationX", 0.0f, 180f);
        flipAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        flipAnimation.setDuration(TIME_FLIP_CARD_MS);
        flipAnimation.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (card.getTag() == TAG_CARD_BACK) {
                    card.setTag(TAG_CARD_FACE);
                    card.setBackgroundResource(R.drawable.menu_bg_card_face);

                } else if (card.getTag() == TAG_CARD_FACE){
                    card.setTag(TAG_CARD_BACK);
                    card.setBackgroundResource(R.drawable.menu_bg_card_back);
                }
            }
        }, TIME_FLIP_CARD_MS/2);
    }

    // gets the images required for the given card and displays them on the card
    private void drawCardImages(CardCanvasView card) {

    }

}