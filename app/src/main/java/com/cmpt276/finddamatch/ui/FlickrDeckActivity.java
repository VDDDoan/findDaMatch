package com.cmpt276.finddamatch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.FlickrImagesManager;

import java.util.List;

public class FlickrDeckActivity extends AppCompatActivity {

    private RecyclerView deckRecyclerView;
    private List<Bitmap> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_deck);

        ImageView add = findViewById(R.id.btn_addtoflickrdeck);
        add.setOnClickListener(v -> {
            Intent intent = new Intent(FlickrDeckActivity.this, PhotoGalleryActivity.class);
            startActivity(intent);
        });

        deckRecyclerView = findViewById(R.id.recycler_view_flickrdeck);
        deckRecyclerView.setLayoutManager(new GridLayoutManager(FlickrDeckActivity.this, 3));

        images = FlickrImagesManager.getInstance(FlickrDeckActivity.this).getBitmaps();
        deckRecyclerView.setAdapter(new FlickrDeckActivity.DeckImgAdapter(images));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deckRecyclerView.getAdapter() != null) {
            deckRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private class DeckImgHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView itemImageView;

        public DeckImgHolder(@NonNull View itemView) {
            super(itemView);

            itemImageView = itemView.findViewById(R.id.item_image_view);
            itemImageView.setOnClickListener(this);
        }

        public void setImage(Bitmap img) {
            itemImageView.setImageBitmap(img);
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class DeckImgAdapter extends RecyclerView.Adapter<DeckImgHolder> {
        private List<Bitmap> images;

        public DeckImgAdapter(List<Bitmap> images) {
            this.images = images;
        }

        @Override
        public DeckImgHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(FlickrDeckActivity.this);
            View view = inflater.inflate(R.layout.layout_gallery_list_item, viewGroup, false);
            return new FlickrDeckActivity.DeckImgHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DeckImgHolder holder, int position) {
            Bitmap deckImg = images.get(position);
            holder.setImage(deckImg);
        }

        @Override
        public int getItemCount() {
            return images.size();
        }
    }
}