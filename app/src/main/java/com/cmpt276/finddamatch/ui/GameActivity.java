/*
    UI class for playing the game
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
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
    private static final int CARD_HAND = 0;
    private static final int CARD_PLAY = 1;
    private static final int CARD_DECK = 2;
    private static final int NUM_CARDS_IN_ACTIVITY = 3;
    private static final float MAX_SHUFFLE_DISPLACEMENT = 50.0f;
    private static final String TAG_CARD_FACE = "face";
    private static final String TAG_CARD_BACK = "back";

    private boolean isShuffled;

    private float boardHeight;
    private float boardWidth;
    private float cardHeight;
    private float cardWidth;

    private GameLogic gameLogic;
    private Chronometer timer;
    private TextView txtNumCardsRemaining;
    private CardCanvasView[] deck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameLogic = new GameLogic();

        isShuffled = false;

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
            }
        });

        dealFirstCard();
    }

    // generates random float between +max and min
    private float generateRandomBetween(float max, float min) {
        Random rand = new Random();
        return min + rand.nextFloat() * (max - min);
    }

    // animates a card to the origin
    private void resetCardX(CardCanvasView card) {
        ObjectAnimator resetPosAnim = ObjectAnimator.ofFloat(card, "translationX", 0.0f);
        resetPosAnim.setDuration(TIME_SHUFFLE_CARD_MS/2);
        resetPosAnim.start();
    }

    // takes a card and plays shuffle animation
    private void shuffleCardInDeck(final CardCanvasView card) {
        float displacement = generateRandomBetween(MAX_SHUFFLE_DISPLACEMENT, MAX_SHUFFLE_DISPLACEMENT/2);

        Keyframe kf0 = Keyframe.ofFloat(0.0f, 0);
        Keyframe kf1 = Keyframe.ofFloat(0.25f, displacement);
        Keyframe kf2 = Keyframe.ofFloat(0.75f, -displacement);
        Keyframe kf3 = Keyframe.ofFloat(1.0f, 0);
        PropertyValuesHolder pvhTranslation = PropertyValuesHolder.ofKeyframe("translationX", kf0, kf1, kf2, kf3);
        ObjectAnimator shuffleAnim = ObjectAnimator.ofPropertyValuesHolder(card, pvhTranslation);
        shuffleAnim.setInterpolator(new LinearInterpolator());
        shuffleAnim.setDuration((long) generateRandomBetween(TIME_SHUFFLE_CARD_MS, (float) TIME_SHUFFLE_CARD_MS/2));
        shuffleAnim.setRepeatCount(2);
        shuffleAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                resetCardX(card);
            }
        });
        shuffleAnim.start();
    }

    // deals first card and starts the game
    private void dealFirstCard() {
        deck[CARD_HAND].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShuffled) {
                    if (gameLogic.getCurrentCardIndex() == 0) {
                        dealCard(deck[CARD_HAND]);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                gameLogic.startTimer(timer);
                            }
                        }, TIME_DEAL_CARD_MS + TIME_FLIP_CARD_MS);
                    } else {
                        flipCard(deck[CARD_HAND]);
                    }
                } else {
                    for (int i = 0; i < NUM_CARDS_IN_ACTIVITY; i++) {
                        shuffleCardInDeck(deck[i]);
                    }
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isShuffled = true;
                        }
                    }, TIME_SHUFFLE_CARD_MS * 2);
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