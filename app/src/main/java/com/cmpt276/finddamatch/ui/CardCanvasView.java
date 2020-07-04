/*
    Animations for game cards
 */
package com.cmpt276.finddamatch.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

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

    public CardCanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
