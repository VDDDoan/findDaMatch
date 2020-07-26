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
import android.widget.Toast;

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
            Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show();
            FlickrImagesManager.getInstance(FlickrDeckActivity.this).update();
            images = FlickrImagesManager.getInstance(FlickrDeckActivity.this).getBitmaps();
            deckRecyclerView.setAdapter(new FlickrDeckActivity.DeckImgAdapter(images));
            deckRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }



    private class DeckImgAdapter extends RecyclerView.Adapter<DeckImgAdapter.DeckImgHolder> {
        private List<Bitmap> images;

        private class DeckImgHolder extends RecyclerView.ViewHolder {
            private ImageView itemImageView;

            public DeckImgHolder(ImageView view) {
                super(view);
                itemImageView = view;
            }
        }

        public DeckImgAdapter(List<Bitmap> images) {
            this.images = images;
        }

        @Override
        public DeckImgHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            ImageView view = (ImageView) LayoutInflater.from(FlickrDeckActivity.this)
                    .inflate(R.layout.layout_gallery_list_item, viewGroup, false);
            return new DeckImgHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DeckImgHolder holder, int position) {
            Bitmap deckImg = images.get(position);
            holder.itemImageView.setImageBitmap(deckImg);
            System.out.println("image set");
        }

        @Override
        public int getItemCount() {
            System.out.println(images.size());
            return images.size();
        }
    }
}