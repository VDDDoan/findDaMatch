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
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.GameLogic;
import com.cmpt276.finddamatch.model.HighScoreManager;
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

    private TypedArray fruitImages;

    private float boardHeight;
    private float boardWidth;
    private float cardHeight;
    private float cardWidth;

    private int numCardsPerSet = Options.getInstance().getNumCardsPerSet();

    private GameLogic gameLogic;
    private Chronometer timer;
    private TextView txtNumCardsRemaining;
    private CardCanvasView[] uiDeck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initGame();
        fruitImages = getResources().obtainTypedArray(R.array.fruitImageSet);
        handCardListener();
        playCardListener();
        deckCardListener();
    }
    private void initGame() {
        gameLogic = new GameLogic();

        isShuffled = false;

        txtNumCardsRemaining = findViewById(R.id.txt_num_cards_remaining);
        timer = findViewById(R.id.timer_game);

        txtNumCardsRemaining.setText("Cards Remaining: " + (numCardsPerSet - gameLogic.getCurrentCardIndex()));

        uiDeck = new CardCanvasView[NUM_CARDS_IN_ACTIVITY];

        uiDeck[CARD_HAND] = findViewById(R.id.view_card_hand);
        uiDeck[CARD_PLAY] = findViewById(R.id.view_card_play);
        uiDeck[CARD_DECK] = findViewById(R.id.view_card_deck);

        for (int i = 0; i < NUM_CARDS_IN_ACTIVITY; i++) {
            uiDeck[i].setTag(TAG_CARD_BACK);
            uiDeck[i].setBackgroundResource(R.drawable.menu_bg_card_back);
        }

        final ConstraintLayout gameBoard = findViewById(R.id.view_game_board);
        gameBoard.post(new Runnable() {
            @Override
            public void run() {
                boardHeight = (float) gameBoard.getHeight();
                boardWidth = (float) gameBoard.getWidth();
                cardHeight = (float) uiDeck[0].getHeight();
                cardWidth = (float) uiDeck[0].getWidth();
            }
        });
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

    // takes a card and plays a brief shuffle animation
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

    private void deckCardListener() {
        uiDeck[CARD_DECK].setOnClickListener(v -> {
            if (gameLogic.getCurrentCardIndex() == numCardsPerSet - 1) {
                // deal HAND CARD to discard pile (HAND CARD)
                dealCard(uiDeck[CARD_DECK]);
                // win screen and times up
                gameLogic.stopTimer(timer);
                // show user dialog(fragment?) asking for name
            }
        });
    }

    private void playCardListener() {
        uiDeck[CARD_PLAY].setOnClickListener(v -> {
            if (gameLogic.getCurrentCardIndex() > 0 && gameLogic.getCurrentCardIndex() < numCardsPerSet - 1) {
                // deal PLAY CARD to discard pile (HAND CARD)
                dealCard(uiDeck[CARD_PLAY]);
            }
        });
    }

    // deals first card and starts the game
    private void handCardListener() {
        uiDeck[CARD_HAND].setOnClickListener(v -> {
            if (isShuffled) {
                if (gameLogic.getCurrentCardIndex() == 0) {
                    drawCardImages(uiDeck[CARD_HAND]);
                    dealFirstCard();
                } else {
                    // something related to image selection
                }
            } else {
                shuffleUIDeck();
            }
        });
    }

    private void dealFirstCard() {
        dealCard(uiDeck[CARD_HAND]);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameLogic.startTimer(timer);
                flipCardAnim(uiDeck[CARD_PLAY]);
            }
        }, TIME_DEAL_CARD_MS + TIME_FLIP_CARD_MS);
    }

    private void shuffleUIDeck() {
        gameLogic.shuffleDeck();
        for (int i = 0; i < NUM_CARDS_IN_ACTIVITY; i++) {
            shuffleCardInDeck(uiDeck[i]);
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isShuffled = true;
            }
        }, TIME_SHUFFLE_CARD_MS * 2);
    }

    // deals next card from deck to hand ( show deal animation and handle game logic implications)
    private void dealCard(final CardCanvasView card) {
        ObjectAnimator dealAnimation = ObjectAnimator.ofFloat(card, "translationY", boardHeight/2);
        dealAnimation.setDuration(TIME_DEAL_CARD_MS);
        dealAnimation.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (card == uiDeck[CARD_HAND]) {
                    flipCardAnim(card);
                } else if (gameLogic.getCurrentCardIndex() < numCardsPerSet - 1){
                    // show images that were on the PLAY CARD on the HAND CARD

                    // drawCardImages(something);

                    // flip PLAY CARD to face down
                    flipCard(uiDeck[CARD_PLAY]);
                    // move PLAY CARD back to draw pile (DECK CARD)
                    uiDeck[CARD_PLAY].setTranslationY(0.0f);
                    // flip card animation
                    flipCardAnim(uiDeck[CARD_PLAY]);
                    // show images of next card on PLAY CARD
                } else if (gameLogic.getCurrentCardIndex() == numCardsPerSet - 1) {
                    flipCardAnim(uiDeck[CARD_DECK]);
                }
            }
        });
        dealAnimation.start();
        gameLogic.incrementCurrentCardIndex();
        txtNumCardsRemaining.setText("Cards Remaining: " + (numCardsPerSet - gameLogic.getCurrentCardIndex()));
    }

    // takes a card plays the animation, switches the background halfway through the animation
    private void flipCardAnim(final CardCanvasView card) {
        ObjectAnimator flipAnimation = ObjectAnimator.ofFloat(card, "rotationX", 0.0f, 180f);
        flipAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        flipAnimation.setDuration(TIME_FLIP_CARD_MS);
        flipAnimation.start();
        card.setRotation(180f);

        final Handler handler = new Handler();
        handler.postDelayed(() -> flipCard(card), TIME_FLIP_CARD_MS/2);
    }

    private void flipCard(CardCanvasView card) {
        if (card.getTag() == TAG_CARD_BACK) {
            card.setTag(TAG_CARD_FACE);
            card.setBackgroundResource(R.drawable.menu_bg_card_face);
            drawCardImages(card);
        } else if (card.getTag() == TAG_CARD_FACE){
            card.setTag(TAG_CARD_BACK);
            card.setBackgroundResource(R.drawable.menu_bg_card_back);
        }
    }

    // gets the images required for the given card and displays them on the card
    private void drawCardImages(final CardCanvasView card) {
        int[] images = new int[Options.getInstance().getNumImagesPerCard()];
        images = gameLogic.getCard(gameLogic.getCurrentCardIndex());

    }
/*
    public void loadImages(int imageArr[]){
        ImageView imagesViewArr[] = new ImageView[imageArr.length];
        for (int i = 0; i < imageArr.length - 1; i++){
            imagesViewArr[i] = new ImageView(this);
            imagesViewArr[i].setImageResource(fruitImages.getResourceId(imageArr[i], 0));
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            this.addContentView(imagesViewArr[i], lp);

        }
    }*/

}