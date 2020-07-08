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
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Chronometer;
import android.widget.ImageView;
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

    private TypedArray fruitImages;
    ConstraintLayout constraintLayout;

    private float boardHeight;
    private float boardWidth;
    private float cardHeight;
    private float cardWidth;

    private int numCardsPerSet = Options.getInstance().getNumCardsPerSet();

    private GameLogic gameLogic;
    private Chronometer timer;
    private TextView txtNumCardsRemaining;
    private CardLayout[] uiDeck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        constraintLayout = new ConstraintLayout(this);
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

        uiDeck = new CardLayout[NUM_CARDS_IN_ACTIVITY];

        uiDeck[CARD_HAND] = findViewById(R.id.view_card_hand);
        uiDeck[CARD_PLAY] = findViewById(R.id.view_card_play);
        uiDeck[CARD_DECK] = findViewById(R.id.view_card_deck);

        for (int i = 0; i < NUM_CARDS_IN_ACTIVITY; i++) {
            uiDeck[i].setTranslationZ(NUM_CARDS_IN_ACTIVITY - i);
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
    private void resetCardX(CardLayout card) {
        ObjectAnimator resetPosAnim = ObjectAnimator.ofFloat(card, "translationX", 0.0f);
        resetPosAnim.setDuration(TIME_SHUFFLE_CARD_MS/2);
        resetPosAnim.start();
    }

    // takes a card and plays a brief shuffle animation
    private void shuffleCardInDeck(final CardLayout card) {
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
                uiDeck[CARD_DECK].setTranslationZ(3);
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
        handler.postDelayed(() -> {
            gameLogic.startTimer(timer);
            flipCardAnim(uiDeck[CARD_PLAY]);
            uiDeck[CARD_HAND].setTranslationZ(0);
        }, TIME_DEAL_CARD_MS + TIME_FLIP_CARD_MS);
    }

    private void shuffleUIDeck() {
        gameLogic.shuffleDeck();
        for (int i = 0; i < NUM_CARDS_IN_ACTIVITY; i++) {
            shuffleCardInDeck(uiDeck[i]);
        }
        final Handler handler = new Handler();
        handler.postDelayed(() -> isShuffled = true, TIME_SHUFFLE_CARD_MS * 2);
    }

    private void revealNextMiddleCard() {
        CardLayout prevCard = uiDeck[CARD_HAND];
        CardLayout nextCard = uiDeck[CARD_PLAY];
        // show images that were on the PLAY CARD on the HAND CARD
        applyCardImages(prevCard, gameLogic.getCard(gameLogic.getCurrentCardIndex() - 1));
        // flip PLAY CARD to face down
        flipCard(nextCard);
        // move PLAY CARD back to draw pile (DECK CARD)
        resetImageViews(nextCard);
        nextCard.setTranslationY(0.0f);
        // flip card animation
        flipCardAnim(nextCard);
    }

    // deals next card from deck to hand ( show deal animation and handle game logic implications)
    private void dealCard(final CardLayout card) {
        ObjectAnimator dealAnimation = ObjectAnimator.ofFloat(card, "translationY", boardHeight/2);
        dealAnimation.setDuration(TIME_DEAL_CARD_MS);
        dealAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (card == uiDeck[CARD_HAND]) {
                    flipCardAnim(card);
                } else if (gameLogic.getCurrentCardIndex() < numCardsPerSet - 1){
                    revealNextMiddleCard();
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
    private void flipCardAnim(final CardLayout card) {
        ObjectAnimator flipAnimation = ObjectAnimator.ofFloat(card, "rotationX", 0.0f, 180f);
        flipAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        flipAnimation.setDuration(TIME_FLIP_CARD_MS);
        flipAnimation.start();
        card.setRotation(180f);

        final Handler handler = new Handler();
        handler.postDelayed(() -> flipCard(card), TIME_FLIP_CARD_MS/2);
    }

    private void flipCard(CardLayout card) {
        if (card.getTag() == TAG_CARD_BACK) {
            card.setTag(TAG_CARD_FACE);
            card.setBackgroundResource(R.drawable.menu_bg_card_face);
            drawCardImages(card);
        } else if (card.getTag() == TAG_CARD_FACE){
            card.setTag(TAG_CARD_BACK);
            card.setBackgroundResource(R.drawable.menu_bg_card_back);
        }
    }

    private void resetImageViews(CardLayout card) {
        ImageView[] imageViews = new ImageView[Options.getInstance().getNumImagesPerCard()];
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = card.findViewWithTag(String.valueOf(i));
            imageViews[i].setImageResource(android.R.color.transparent);
        }
    }

    private void applyCardImages(CardLayout card, int[] images) {
        ImageView[] imageViews = new ImageView[images.length];
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = card.findViewWithTag(String.valueOf(i));
            imageViews[i].setImageResource(fruitImages.getResourceId(images[i], i));
        }
    }

    private void createCardImages(CardLayout card, int[] images) {
        ImageView[] imageViews = new ImageView[images.length];
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = new ImageView(this);
            ViewGroup.LayoutParams imageParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            imageParams.height = (int) cardHeight/3;
            imageParams.width = (int) cardWidth/3;
            imageViews[i].setTag(String.valueOf(i));
            imageViews[i].setLayoutParams(imageParams);
            imageViews[i].setImageResource(fruitImages.getResourceId(images[i], i));
            imageViews[i].setClickable(true);
            imageViews[i].setFocusable(true);
            card.addView(imageViews[i]);
        }
    }

    // gets the images required for the given card and displays them on the card
    private void drawCardImages(final CardLayout card) {
        int[] images;
        if (card == uiDeck[CARD_HAND]) {
            images = gameLogic.getCard(gameLogic.getCurrentCardIndex() - 1);
        } else {
            images = gameLogic.getCard(gameLogic.getCurrentCardIndex());
        }
        // if there's no images
        if (card.findViewWithTag("0") == null) {
            createCardImages(card, images);
        } else {
            applyCardImages(card, images);
        }
    }

}