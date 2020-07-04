/*
    UI class for playing the game
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.GameLogic;

public class GameActivity extends AppCompatActivity {
    private static final int TIME_FLIP_CARD_MS = 500;
    private static final int TIME_DEAL_CARD_MS = 500;
    private float boardHeight;
    private float boardWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GameLogic gameLogic = new GameLogic();

        final ConstraintLayout gameBoard = findViewById(R.id.view_game_board);
        TextView txtNumCardsRemaining = findViewById(R.id.txt_num_cards_remaining);

        gameBoard.post(new Runnable() {
            @Override
            public void run() {
                boardHeight = (float) gameBoard.getHeight();
                boardWidth = (float) gameBoard.getWidth();
            }
        });

        final CardCanvasView cardViewHand = findViewById(R.id.view_card_hand);
        cardViewHand.setTag("back");
        cardViewHand.setBackgroundResource(R.drawable.menu_bg_card_back);

        dealCard(cardViewHand);
    }

    // deals next card from deck to hand
    private void dealCard(final CardCanvasView card) {
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator dealAnimation = ObjectAnimator.ofFloat(card, "translationY", boardHeight/2);
                dealAnimation.setDuration(TIME_DEAL_CARD_MS);
                dealAnimation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        flipCard(card);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                dealAnimation.start();
            }
        });
    }

    // takes a card and flips it
    private void flipCard(final CardCanvasView card) {
        ObjectAnimator flipAnimation = ObjectAnimator.ofFloat(card, "rotationY", 0.0f, -180f);
        flipAnimation.setDuration(TIME_FLIP_CARD_MS);
        flipAnimation.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (card.getTag() == "back") {
                    card.setTag("face");
                    card.setBackgroundResource(R.drawable.menu_bg_card_face);
                } else if (card.getTag() == "face"){
                    card.setTag("back");
                    card.setBackgroundResource(R.drawable.menu_bg_card_back);
                }
            }
        }, TIME_FLIP_CARD_MS/2);
    }


}