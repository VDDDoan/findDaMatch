/*
    UI class for changing the game settings
 */
package com.cmpt276.finddamatch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.Options;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);


        updateScreen();
        onClickImgSetLisenter(R.id.btn_imageSet0,R.id.btn_imageSet1, 0);
        onClickImgSetLisenter(R.id.btn_imageSet1,R.id.btn_imageSet0, 1);

        Button orderButton = findViewById(R.id.orderChangeBtn);
        orderButton.setOnClickListener(v -> {
            int[] orderOptions = getResources().getIntArray(R.array.gameOrderSizes);
            int currentOrder = Options.getInstance().getNumImagesPerCard();
            for (int i = 0; i < orderOptions.length; i++){
                if(currentOrder == (orderOptions[i] + 1)){
                    if (i == orderOptions.length-1){
                        orderButton.setText(getString(R.string.current_order_size,orderOptions[0]));
                        Options.getInstance().setNumImagesPerCard(orderOptions[0]+1);
                    }
                    else{
                        orderButton.setText(getString(R.string.current_order_size,orderOptions[i+1]));
                        Options.getInstance().setNumImagesPerCard(orderOptions[i+1]+1);
                    }

                }
            }
            updateDrawPile();
            Toast.makeText(this,"Draw Pile Options Updated!",Toast.LENGTH_SHORT).show();
        });

        Button drawPileBtn = findViewById(R.id.numOfCardsBtn);
        drawPileBtn.setOnClickListener(v -> {
            incrementDrawPile();
        });

        ImageView returnBtn = findViewById(R.id.btn_options_back);
        returnBtn.setOnClickListener(v -> finish());

    }

    private void onClickImgSetLisenter(int btnImgSet, int btnOtherImgSet, int selectedIndex) {
        Button clickedButton = findViewById(btnImgSet);
        Button otherButton = findViewById(btnOtherImgSet);

        clickedButton.setOnClickListener(v ->{
            Options.getInstance().setImageSetIndex(selectedIndex);
            clickedButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCharcoalLite)));
            otherButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCharcoal)));
        });


    }

    private int[] getDrawPileOptions(int numImagesPerCard) {
        int[] drawPileOptions;
        switch (numImagesPerCard){
            case 3:
                drawPileOptions = getResources().getIntArray(R.array.drawPileOrder2);
                break;
            case 4:
                drawPileOptions = getResources().getIntArray(R.array.drawPileOrder3);
                break;
            case 6:
                drawPileOptions = getResources().getIntArray(R.array.drawPileOrder5);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + numImagesPerCard);
        }
        return drawPileOptions;
    }


    private void updateScreen() {
        Button btnImgSet0 = findViewById(R.id.btn_imageSet0);
        Button btnImgSet1 = findViewById(R.id.btn_imageSet1);
        Button orderBtn = findViewById(R.id.orderChangeBtn);
        Button drawPileBtn = findViewById(R.id.numOfCardsBtn);

        int selectedImgIndex = Options.getInstance().getImageSetIndex();
        switch (selectedImgIndex){
            case 0:
                btnImgSet0.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCharcoalLite)));
                break;
            case 1:
                btnImgSet1.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCharcoalLite)));
                break;
        }

        orderBtn.setText(getString(R.string.current_order_size,Options.getInstance().getNumImagesPerCard()-1));

        updateDrawPile();


    }

    private void updateDrawPile(){
        Button cardNumButton = findViewById(R.id.numOfCardsBtn);

        int[] drawSizeOptions = getDrawPileOptions(Options.getInstance().getNumImagesPerCard());
        int currentDrawSize = Options.getInstance().getNumCardsPerSet();
        boolean isNotAnOption = true;

        for (int i = 0; i < drawSizeOptions.length; i++) {
            if (currentDrawSize == drawSizeOptions[i]) {
                if (i == drawSizeOptions.length-1){
                    cardNumButton.setText(R.string.drawPileAllOption);
                }else{
                    cardNumButton.setText(getString(R.string.current_number_of_cards,drawSizeOptions[i]));
                }
                Options.getInstance().setNumCardsPerSet(drawSizeOptions[i]);
                isNotAnOption = false;
            }
        }

        if (isNotAnOption){
            cardNumButton.setText(getString(R.string.current_number_of_cards,drawSizeOptions[0]));
            Options.getInstance().setNumCardsPerSet((drawSizeOptions[0]));
        }
    }

    private void incrementDrawPile(){
        Button drawPileBtn = findViewById(R.id.numOfCardsBtn);

        int[] drawSizeOptions = getDrawPileOptions(Options.getInstance().getNumImagesPerCard());
        int currentDrawSize = Options.getInstance().getNumCardsPerSet();

        for (int i = 0; i < drawSizeOptions.length; i++) {
            if (currentDrawSize == drawSizeOptions[i]) {
                if (i == drawSizeOptions.length - 1) {
                    drawPileBtn.setText(getString(R.string.current_number_of_cards, drawSizeOptions[0]));
                    Options.getInstance().setNumCardsPerSet(drawSizeOptions[0]);
                } else if (i == drawSizeOptions.length - 2) {
                    drawPileBtn.setText(R.string.drawPileAllOption);
                    Options.getInstance().setNumCardsPerSet(drawSizeOptions[i + 1]);
                } else {
                    drawPileBtn.setText(getString(R.string.current_number_of_cards, drawSizeOptions[i + 1]));
                    Options.getInstance().setNumCardsPerSet(drawSizeOptions[i + 1]);
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_top);
    }
}