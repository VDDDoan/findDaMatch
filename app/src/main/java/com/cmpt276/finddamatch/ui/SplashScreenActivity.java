/*
    UI class for displaying some of the theme before starting (going to main menu)
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmpt276.finddamatch.R;

public class SplashScreenActivity extends AppCompatActivity {
    private TextView name1;
    private TextView name2;
    private TextView name3;
    private TextView name4;
    private TextView title;
    private ImageView eye;
    private Intent intent;
    private Handler handler = new Handler();
    boolean skipped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Button btnSkip = findViewById(R.id.btn_splash_skip);
        intent = new Intent(SplashScreenActivity.this, MainMenuActivity.class);
        btnSkip.setOnClickListener(v -> {
            finish();
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        name1 = findViewById(R.id.David);
        name2 = findViewById(R.id.James);
        name3 = findViewById(R.id.Vinesh);
        name4 = findViewById(R.id.Wei);
        title = findViewById(R.id.Title);
        eye = findViewById(R.id.sauron_eye);

        ObjectAnimator eyeRotate = rotateEye(eye);
        eyeRotate.start();
        ValueAnimator eyeAnimator = animateEye(eye);
        eyeAnimator.start();
        textAnimation();
        start();

    }

    private void start(){
        handler.postDelayed(runnable, 13700);   //5 seconds after animations
    }

    private Runnable runnable = new Runnable(){
        @Override
        public void run(){
            if (skipped){
                handler.removeCallbacks(runnable);
            }else{
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
            }
        }
    };

    private ValueAnimator animateEye(ImageView eye) {
        ValueAnimator anim = ValueAnimator.ofInt(0, 359);
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) eye.getLayoutParams();
            layoutParams.circleAngle = val;
            eye.setLayoutParams(layoutParams);
        });
        anim.setDuration(3000);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setRepeatCount(ValueAnimator.INFINITE);

        return anim;
    }

   private ObjectAnimator rotateEye(ImageView eye){
        ObjectAnimator objAnim = ObjectAnimator.ofFloat(eye, View.ROTATION, 0.0f, 360.0f);
        objAnim.setDuration(3000);
        objAnim.setRepeatCount(Animation.INFINITE);
        return objAnim;
    }

    private void textAnimation(){
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
        title.startAnimation(fadeIn);
        name1.startAnimation(fadeIn);
        name2.startAnimation(fadeIn);
        name3.startAnimation(fadeIn);
        name4.startAnimation(fadeIn);
        fadeIn.setDuration(3500);
        fadeIn.setFillAfter(true);
    }

    @Override
    public void onPause(){
        super.onPause();
        handler.removeCallbacks(runnable);
    }
    @Override
    public void onDestroy () {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}
