/*
    Image drawing for game cards
 */
package com.cmpt276.finddamatch.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.cmpt276.finddamatch.R;

import java.util.Random;

public class CardCanvasView extends View {

    
    public CardCanvasView(Context context) {
        super(context);
    }

    public CardCanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CardCanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }



}
