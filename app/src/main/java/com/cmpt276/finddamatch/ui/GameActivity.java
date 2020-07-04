/*
    UI class for playing the game
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;

import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.GameLogic;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GameLogic gameLogic = new GameLogic();

        final ConstraintLayout gameBoard = findViewById(R.id.view_game_board);
        final float[] height = new float[1];
        final float[] width = new float[1];

        gameBoard.post(new Runnable() {
            @Override
            public void run() {
                height[0] = (float) gameBoard.getHeight();
                width[0] = (float) gameBoard.getWidth();
            }
        });

        final CardCanvasView canvasView = findViewById(R.id.view_card_hand);

        canvasView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ObjectAnimator animation = ObjectAnimator.ofFloat(canvasView, "translationY", height[0]/2);
                animation.setDuration(500);
                animation.start();
            }
        });
    }
}