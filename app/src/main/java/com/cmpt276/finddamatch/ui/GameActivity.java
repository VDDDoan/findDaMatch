/*
    UI class for playing the game
 */
package com.cmpt276.finddamatch.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.FlickrImagesManager;
import com.cmpt276.finddamatch.model.GameLogic;
import com.cmpt276.finddamatch.model.HighScore;
import com.cmpt276.finddamatch.model.HighScoreManager;
import com.cmpt276.finddamatch.model.Options;
import com.cmpt276.finddamatch.model.SoundPoolUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GameActivity<soundInstance> extends AppCompatActivity {
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

    private final int numCardsPerSet = Options.getInstance().getNumCardsPerSet();

    private boolean isShuffled;
    private boolean startOfGame;
    private boolean isDealing;

    private TypedArray imageSetUI;
    private List<Bitmap> flickrSet;

    private float boardHeight;
    private float boardWidth;
    private float cardHeight;
    private float cardWidth;

    private GameLogic gameLogic;
    private Chronometer timer;
    private TextView txtNumCardsRemaining;
    private CardLayout[] uiDeck;
    private SoundPoolUtil soundInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initGame();

        handCardListener();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_top);
    }

    private void initGame() {

        gameLogic = new GameLogic();

        isShuffled = false;
        startOfGame = true;

        txtNumCardsRemaining = findViewById(R.id.txt_num_cards_remaining);
        timer = findViewById(R.id.timer_game);

        txtNumCardsRemaining.setText("Cards Remaining: " + (numCardsPerSet - gameLogic.getCurrentCardIndex()));
        uiDeck = new CardLayout[NUM_CARDS_IN_ACTIVITY];

        uiDeck[CARD_HAND] = findViewById(R.id.view_card_hand);
        uiDeck[CARD_PLAY] = findViewById(R.id.view_card_play);
        uiDeck[CARD_DECK] = findViewById(R.id.view_card_deck);
        soundInstance = SoundPoolUtil.getInstance(this);
        soundInstance.play(3);
       // MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.intro2);
       // mediaPlayer.start();
        for (int i = 0; i < NUM_CARDS_IN_ACTIVITY; i++) {
            uiDeck[i].setTranslationZ(NUM_CARDS_IN_ACTIVITY - i);
            uiDeck[i].setTag(TAG_CARD_BACK);
            uiDeck[i].setBackgroundResource(R.drawable.menu_bg_card_back);
        }

        final ConstraintLayout gameBoard = findViewById(R.id.view_game_board);
        gameBoard.post(() -> {
            boardHeight = (float) gameBoard.getHeight();
            boardWidth = (float) gameBoard.getWidth();
            cardHeight = (float) uiDeck[0].getHeight();
            cardWidth = (float) uiDeck[0].getWidth();
        });

        // if the option selected was 2 (flickr deck)
        if (isFlickrDeck()) {
            //flickr
            flickrSet = FlickrImagesManager.getInstance(GameActivity.this).getBitmaps();
        } else {
            //regular
            TypedArray imageSets = getResources().obtainTypedArray(R.array.imageSets);
            int resId = imageSets.getResourceId(Options.getInstance().getImageSetIndex(), 0);
            imageSetUI = getResources().obtainTypedArray(resId);
        }

    }

    private boolean isFlickrDeck() {
        return Options.getInstance().getImageSetIndex() == Options.getInstance().getFlickrIndex();
    }

    // generates random float between +max and min
    private float generateRandomBetween(float max, float min) {
        Random rand = new Random();
        return min + rand.nextFloat() * (max - min);
    }

    // animates a card to the origin
    private void resetCardX(CardLayout card) {
        ObjectAnimator resetPosAnim = ObjectAnimator.ofFloat(card, "translationX", 0.0f);
        resetPosAnim.setDuration(TIME_SHUFFLE_CARD_MS / 2);
        resetPosAnim.start();
    }

    // takes a card and plays a brief shuffle animation
    private void shuffleCardInDeck(final CardLayout card) {
        float displacement = generateRandomBetween(MAX_SHUFFLE_DISPLACEMENT, MAX_SHUFFLE_DISPLACEMENT / 2);

        Keyframe kf0 = Keyframe.ofFloat(0.0f, 0);
        Keyframe kf1 = Keyframe.ofFloat(0.25f, displacement);
        Keyframe kf2 = Keyframe.ofFloat(0.75f, -displacement);
        Keyframe kf3 = Keyframe.ofFloat(1.0f, 0);
        PropertyValuesHolder pvhTranslation = PropertyValuesHolder.ofKeyframe("translationX", kf0, kf1, kf2, kf3);
        ObjectAnimator shuffleAnim = ObjectAnimator.ofPropertyValuesHolder(card, pvhTranslation);
        shuffleAnim.setInterpolator(new LinearInterpolator());
        shuffleAnim.setDuration((long) generateRandomBetween(TIME_SHUFFLE_CARD_MS, (float) TIME_SHUFFLE_CARD_MS / 2));
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
    private void handCardListener() {
        uiDeck[CARD_HAND].setOnClickListener(v -> {
            if (isShuffled) {
                if (gameLogic.getCurrentCardIndex() == 0) {
                    dealFirstCard();
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
        isDealing = true;
        ObjectAnimator dealAnimation = ObjectAnimator.ofFloat(card, "translationY", boardHeight / 2);
        dealAnimation.setDuration(TIME_DEAL_CARD_MS);
        dealAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (card == uiDeck[CARD_HAND]) {
                    flipCardAnim(card);
                } else if (gameLogic.getCurrentCardIndex() < numCardsPerSet - 1) {
                    revealNextMiddleCard();
                } else if (gameLogic.getCurrentCardIndex() == numCardsPerSet - 1) {
                    flipCardAnim(uiDeck[CARD_DECK]);
                }
                isDealing = false;
            }
        });
        dealAnimation.start();

        gameLogic.incrementCurrentCardIndex();

        txtNumCardsRemaining.setText("Cards Remaining: " + (numCardsPerSet - gameLogic.getCurrentCardIndex()));
    }

    // takes a card plays the animation, switches the background halfway through the animation
    private void flipCardAnim(final CardLayout card) {
        if (Options.getInstance().getGameMode() == 0) {
            ObjectAnimator flipAnimationA = ObjectAnimator.ofFloat(card, "rotationX", 0.0f, 180f);
            flipAnimationA.setInterpolator(new AccelerateDecelerateInterpolator());
            flipAnimationA.setDuration(TIME_FLIP_CARD_MS/2);
            flipAnimationA.start();
            card.setRotation(180f);
        }
        else{
            ObjectAnimator flipAnimationA = ObjectAnimator.ofFloat(card, "rotationX", 0.0f, 90f);
            ObjectAnimator flipAnimationB = ObjectAnimator.ofFloat(card, "rotationX", -90f, 0.0f);
            flipAnimationA.setInterpolator(new AccelerateDecelerateInterpolator());
            flipAnimationA.setDuration(TIME_FLIP_CARD_MS/2);
            flipAnimationA.start();
            flipAnimationB.setInterpolator(new AccelerateDecelerateInterpolator());
            flipAnimationB.setDuration(TIME_FLIP_CARD_MS/2);
            flipAnimationB.start();
            card.setRotation(360f);

        }
        final Handler handler = new Handler();
        handler.postDelayed(() -> flipCard(card), TIME_FLIP_CARD_MS / 2);
    }

    private void flipCard(CardLayout card) {
        if (card.getTag() == TAG_CARD_BACK) {
            card.setTag(TAG_CARD_FACE);
            card.setBackgroundResource(R.drawable.menu_bg_card_face);
            drawCardImages(card);
        } else if (card.getTag() == TAG_CARD_FACE) {
            card.setTag(TAG_CARD_BACK);
            card.setBackgroundResource(R.drawable.menu_bg_card_back);
        }
    }

    private void resetImageViews(CardLayout card) {
        ImageView[] imageViews = new ImageView[Options.getInstance().getNumImagesPerCard()];
        TextView[] textViews = new TextView[Options.getInstance().getNumImagesPerCard()];
        for (int i = 0; i < imageViews.length; i++) {
            if(card.findViewWithTag(String.valueOf(i)) instanceof TextView){
                textViews[i] = card.findViewWithTag(String.valueOf(i));
                //textViews[i].setImageResource(android.R.color.transparent);
            }else{
                imageViews[i] = card.findViewWithTag(String.valueOf(i));
                imageViews[i].setImageResource(android.R.color.transparent);
            }
        }
    }

    private void applyCardImages(CardLayout card, int[] images) {
        ImageView[] imageViews = new ImageView[images.length];
        TextView[] textViews = new TextView[images.length];
        for (int i = 0; i < imageViews.length; i++) {
            final int index = images[i];
            if(card.findViewWithTag(String.valueOf(i)) instanceof TextView) {
                textViews[i] = card.findViewWithTag(String.valueOf(i));
                textViews[i].setText(imageSetUI.getResourceId(images[i], i));
                String name = textViews[i].getText().toString().substring(textViews[i].getText().toString().lastIndexOf("/") + 1, textViews[i].getText().toString().lastIndexOf("."));
                name = name.replace("ic_", "");
                textViews[i].setText(name);
                textViews[i].setGravity(Gravity.CENTER);
                textViews[i].setTextSize(20F);
                textViews[i].setClickable(true);
                textViews[i].setFocusable(true);

                if (!startOfGame && gameLogic.isMatch(index) && card == uiDeck[CARD_PLAY]) {
                    if (gameLogic.getCurrentCardIndex() < numCardsPerSet - 1) {
                        textViews[i].setOnClickListener(v -> {
                            if (!isDealing) {
                                dealCard(uiDeck[CARD_PLAY]);
                            }
                        });
                    } else {
                        textViews[i].setOnClickListener(null);
                    }
                } else {
                    textViews[i].setOnClickListener(null);
                }
            } else {
                imageViews[i] = card.findViewWithTag(String.valueOf(i));
                if(isFlickrDeck()) {
                    imageViews[i].setImageBitmap(flickrSet.get(images[i]));
                } else {
                    imageViews[i].setImageResource(imageSetUI.getResourceId(images[i], i));
                }

                if (!startOfGame && gameLogic.isMatch(index) && card == uiDeck[CARD_PLAY]) {
                    if (gameLogic.getCurrentCardIndex() < numCardsPerSet - 1) {
                        imageViews[i].setOnClickListener(v -> {
                            if (!isDealing) {
                                dealCard(uiDeck[CARD_PLAY]);
                            }
                        });
                    } else {
                        imageViews[i].setOnClickListener(null);
                    }
                } else {
                    imageViews[i].setOnClickListener(null);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showGameOver() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        soundInstance = SoundPoolUtil.getInstance(this);
        this.soundInstance.play(0);
        View view = getLayoutInflater().inflate(R.layout.dialog_gameover, null);
        builder.setView(view).
                //add action button
                        setPositiveButton("Confirm", (dialog, which) -> {
                    EditText userName = view.findViewById(R.id.e_text_username); //bug fixed inspired by https://stackoverflow.com/questions/24895509/getting-value-of-edittext-contained-in-a-custom-dialog-box
                    HighScore score = new HighScore(
                            gameLogic.getTime() / 1000,
                            userName.getText().toString(),
                            DateFormat.getDateInstance().format(Calendar.getInstance().getTime()));
                    //HighScoreManager.getInstance().setHighScore(score);
                    for (int i = HighScoreManager.getInstance().getHighScores().size() - 1; i >= 0; i--) {
                        if (score.getTime() < HighScoreManager.getInstance().getHighScores().get(i).getTime()) {
                            FileRecord(score);
                            break;
                        }
                    }
                    System.out.println(score.toString());
                    Intent intent = new Intent(GameActivity.this, HighScoreActivity.class);
                    startActivity(intent);
                    finish();
                });
        builder.setNegativeButton("Don't Record", (dialog, which) -> {
            dialog.cancel();
            finish();
            Intent intent = new Intent(GameActivity.this, HighScoreActivity.class);
            startActivity(intent);
        });
        builder.setOnDismissListener(dialog -> {
            finish();
            Intent intent = new Intent(GameActivity.this, HighScoreActivity.class);
            startActivity(intent);
        });

        builder.show();
    }

    private void createCardImages(CardLayout card, int[] images) {
        ImageView[] imageViews = new ImageView[images.length];
        soundInstance = SoundPoolUtil.getInstance(this);

        for (int i = 0; i < imageViews.length; i++) {
            final int index = images[i];
            imageViews[i] = new ImageView(this);
            imageViews[i].setTag(String.valueOf(i));
            imageViews[i].setLayoutParams(generateImagePosition(imageViews, i));
            if (isFlickrDeck()) {
                imageViews[i].setImageBitmap(flickrSet.get(images[i]));
            } else {
                imageViews[i].setImageResource(imageSetUI.getResourceId(images[i], i));
            }
            imageViews[i].setClickable(true);
            imageViews[i].setFocusable(true);
            card.addView(imageViews[i]);
            if (!startOfGame && gameLogic.isMatch(index)) {
                if (gameLogic.getCurrentCardIndex() < numCardsPerSet - 1) {
                    imageViews[i].setOnClickListener(v -> {
                        this.soundInstance.play(1);
                        if (!isDealing) {
                            dealCard(uiDeck[CARD_PLAY]);
                        }
                    });
                } else if (gameLogic.getCurrentCardIndex() == numCardsPerSet - 1) {
                    imageViews[i].setOnClickListener((v -> {
                        this.soundInstance.play(1);
                        uiDeck[CARD_DECK].setTranslationZ(3);
                        if (!isDealing) {
                            dealCard(uiDeck[CARD_DECK]);
                            // win screen and times up
                            gameLogic.stopTimer(timer);
                            showGameOver();
                        }

                    }));
                }
            }
            else if(gameLogic.isMatch(index)==false){
                imageViews[i].setOnClickListener((V->{
                    this.soundInstance.play(2);
                }));
            }
        }
    }


    private void createCardTexts(CardLayout card, int[] images) {
        TextView[] textViews = new TextView[images.length];
        for (int i = 0; i < textViews.length; i++) {
            final int index = images[i];
            textViews[i] = new TextView(this);
            textViews[i].setTag(String.valueOf(i));
            textViews[i].setLayoutParams(generateTextPosition(i));
            //textViews[i].setImageResource(imageSetUI.getResourceId(images[i], i));
            textViews[i].setText(imageSetUI.getResourceId(images[i], i));
            String name = textViews[i].getText().toString().substring(textViews[i].getText().toString().lastIndexOf("/") + 1, textViews[i].getText().toString().lastIndexOf("."));
            name = name.replace("ic_", "");
            textViews[i].setText(name);
            textViews[i].setGravity(Gravity.CENTER);
            textViews[i].setTextSize(20F);
            textViews[i].setClickable(true);
            textViews[i].setFocusable(true);
            card.addView(textViews[i]);
            if (!startOfGame && gameLogic.isMatch(index)) {
                if (gameLogic.getCurrentCardIndex() < numCardsPerSet - 1) {
                    textViews[i].setOnClickListener(v -> {
                        if (!isDealing) {
                            dealCard(uiDeck[CARD_PLAY]);
                        }
                    });
                } else if (gameLogic.getCurrentCardIndex() == numCardsPerSet - 1) {
                    textViews[i].setOnClickListener((v -> {
                        if (!isDealing) {
                            uiDeck[CARD_DECK].setTranslationZ(3);
                            dealCard(uiDeck[CARD_DECK]);
                            // win screen and times up
                            gameLogic.stopTimer(timer);
                            showGameOver();
                        }
                    }));
                }
            }
        }
    }


    private void createCardImagesAndText(CardLayout card, int[] images) {
        ImageView[] imageViews = new ImageView[images.length];
        TextView[] textViews = new TextView[images.length];
        for (int i = 0; i < imageViews.length; i++) {
            final int index = images[i];
            imageViews[i] = new ImageView(this);
            imageViews[i].setTag(String.valueOf(i));
            imageViews[i].setLayoutParams(generateImagePosition(imageViews, i));
            if (isFlickrDeck()) {
                imageViews[i].setImageBitmap(flickrSet.get(images[i]));
            } else {
                imageViews[i].setImageResource(imageSetUI.getResourceId(images[i], i));
            }
            imageViews[i].setClickable(true);
            imageViews[i].setFocusable(true);

            textViews[i] = new TextView(this);
            textViews[i].setTag(String.valueOf(i));
            textViews[i].setLayoutParams(generateTextPosition(i));
            //textViews[i].setImageResource(imageSetUI.getResourceId(images[i], i));
            textViews[i].setText(imageSetUI.getResourceId(images[i], i));
            String name = textViews[i].getText().toString().substring(textViews[i].getText().toString().lastIndexOf("/") + 1, textViews[i].getText().toString().lastIndexOf("."));
            name = name.replace("ic_", "");
            textViews[i].setText(name);
            textViews[i].setGravity(Gravity.CENTER);
            textViews[i].setTextSize(20F);
            textViews[i].setClickable(true);
            textViews[i].setFocusable(true);

            if (new Random().nextBoolean()) {
                card.addView(imageViews[i]);
                if (!startOfGame && gameLogic.isMatch(index)) {
                    if (gameLogic.getCurrentCardIndex() < numCardsPerSet - 1) {
                        imageViews[i].setOnClickListener(v -> {
                            if (!isDealing) {
                                dealCard(uiDeck[CARD_PLAY]);
                            }
                        });
                    } else if (gameLogic.getCurrentCardIndex() == numCardsPerSet - 1) {
                        imageViews[i].setOnClickListener((v -> {
                            if (!isDealing) {
                                uiDeck[CARD_DECK].setTranslationZ(3);
                                dealCard(uiDeck[CARD_DECK]);
                                // win screen and times up
                                gameLogic.stopTimer(timer);
                                showGameOver();
                            }

                        }));
                    }
                }
            } else {
                card.addView(textViews[i]);
                if (!startOfGame && gameLogic.isMatch(index)) {
                    if (gameLogic.getCurrentCardIndex() < numCardsPerSet - 1) {
                        textViews[i].setOnClickListener(v -> {
                            if (!isDealing) {
                                dealCard(uiDeck[CARD_PLAY]);
                            }
                        });
                    } else if (gameLogic.getCurrentCardIndex() == numCardsPerSet - 1) {
                        textViews[i].setOnClickListener((v -> {
                            if (!isDealing) {
                                uiDeck[CARD_DECK].setTranslationZ(3);
                                dealCard(uiDeck[CARD_DECK]);
                                // win screen and times up
                                gameLogic.stopTimer(timer);
                                showGameOver();
                            }
                        }));
                    }
                }
            }
        }
    }


    private RelativeLayout.LayoutParams generateImagePosition(ImageView[] imageViews, int index) {
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        setScalingFactor(imageParams,Options.getInstance().getNumImagesPerCard());

        switch (index) {
            case 0:
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
            case 1:
                if (Options.getInstance().getNumImagesPerCard() == 3){
                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);

                }else {
                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                }
                break;
            case 2:
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                break;
            case 3:
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
            case 4:
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                break;
            case 5:
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        }
        return imageParams;
    }

    private void setScalingFactor(RelativeLayout.LayoutParams imageParams, int numImagesPerCard) {
        double scalingFactor;
        switch (numImagesPerCard){
            case 4:
                scalingFactor = 2.5;
                break;
            case 6:
                scalingFactor = 3.0;
                break;
            default:
                scalingFactor = 2.0;
        }
        imageParams.height =(int)( (int) cardHeight / scalingFactor);
        imageParams.width = (int) ((int)cardWidth / scalingFactor);
    }


    private RelativeLayout.LayoutParams generateTextPosition(int index) {
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        setScalingFactor(imageParams,Options.getInstance().getNumImagesPerCard());

        switch (index) {
            case 0:
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
            case 1:
                if (Options.getInstance().getNumImagesPerCard() == 3){
                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);

                }else {
                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                }
                break;
            case 2:
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                break;
            case 3:
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
            case 4:
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                break;
            case 5:
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        }
        return imageParams;
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
            int gameMode = Options.getInstance().getGameMode();
            if (gameMode == 0) {
                createCardImages(card, images);
            } else if (gameMode == 1) {
                createCardTexts(card, images);
            } else if (gameMode == 2) {
                createCardImagesAndText(card, images);
            }
            startOfGame = false;
        } else {
            applyCardImages(card, images);
        }
    }

    public void FileRecord(HighScore newScore) {
        String filename;
        if(Options.getInstance().getOrderNum()== 2){
            filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord.txt";//record the path of file
        }
        else if(Options.getInstance().getOrderNum()== 3){
            filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord3.txt";//record the path of file
        }
        else{
            filename = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/gameRecord5.txt";//record the path of file
        }
        FileOutputStream fos;
        FileInputStream fis;
        PrintWriter pw = null;
        BufferedReader br = null;
        //if the director path not exist, then build it
        File file = new File(getExternalCacheDir().getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            File dir = new File(filename);
            if (!dir.exists()) dir.createNewFile();//if record file not be created
                //if record file has been created, check the # of record, we need only 5 records
            else {
                LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
                lnr.skip(Long.MAX_VALUE);
                int i = lnr.getLineNumber();
                lnr.close();
                //write back to list
                if (i >= 5) {
                    fis = new FileInputStream(filename);
                    br = new BufferedReader(new InputStreamReader(fis));
                    i = 0;
                    String str = null;
                    ArrayList<String> list = new ArrayList<String>();
                    while ((str = br.readLine()) != null) {
                        if (i == 0) {
                            i++;
                            continue;
                        }
                        i++;
                        list.add(str);
                    }
                    BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
                    for (String a : list) {
                        bw.write(a);
                        bw.newLine();
                    }
                    bw.close();
                    fis.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //record the new record
        try {
            fos = new FileOutputStream(filename, true);
            pw = new PrintWriter(fos);
            pw.println(newScore.getNickname() + ' ' + newScore.getTime() + ' ' + newScore.getDate());
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            assert pw != null;
            pw.close();
        }
    }
}