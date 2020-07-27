package com.cmpt276.finddamatch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.FlickrImagesManager;

import java.util.ArrayList;
import java.util.List;

public class FlickrDeckActivity extends AppCompatActivity {

    private RecyclerView deckRecyclerView;
    private List<Bitmap> images;
    private FlickrImagesManager flickrImagesManager;
    private final List<String> selectedItems = new ArrayList<>();
    private final List<Integer> deletedIndex = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_deck);

        ImageView deleteImages = findViewById(R.id.btn_deleteflickrimage);
        deleteImages.setOnClickListener(v->{
            for(int i = 0; i < selectedItems.size(); i++){
                System.out.println("deleted index at index" + i + "= " + deletedIndex.get(i));
                flickrImagesManager.deleteImage(selectedItems.get(0), deletedIndex.get(0));
            }
            //flickrImagesManager.update();
            recreate();
        });

        ImageView add = findViewById(R.id.btn_addtoflickrdeck);
        add.setOnClickListener(v -> {
            Intent intent = new Intent(FlickrDeckActivity.this, PhotoGalleryActivity.class);
            startActivity(intent);
        });

        deckRecyclerView = findViewById(R.id.recycler_view_flickrdeck);
        deckRecyclerView.setLayoutManager(new GridLayoutManager(FlickrDeckActivity.this, 3));

        flickrImagesManager = FlickrImagesManager.getInstance(this);

        images = FlickrImagesManager.getInstance(FlickrDeckActivity.this).getBitmaps();
        deckRecyclerView.setAdapter(new FlickrDeckActivity.DeckImgAdapter(images));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deckRecyclerView.getAdapter() != null) {
            images = FlickrImagesManager.getInstance(FlickrDeckActivity.this).getBitmaps();
            deckRecyclerView.setAdapter(new FlickrDeckActivity.DeckImgAdapter(images));
            deckRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private class DeckImgAdapter extends RecyclerView.Adapter<DeckImgAdapter.DeckImgHolder> {
        private List<Bitmap> images;

        private class DeckImgHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            private ImageView itemImageView;

            public DeckImgHolder(ImageView view) {
                super(view);
                itemImageView = view;
                itemImageView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getLayoutPosition();
                Integer integerPos = position;
                String name = flickrImagesManager.returnFileId(position);
                if (!selectedItems.contains(name)) {
                    selectedItems.add(name);
                    deletedIndex.add(integerPos);
                } else {
                    selectedItems.remove(name);
                    deletedIndex.remove(integerPos);
                }
                deletedIndex.sort(Integer::compareTo);
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
            // bug here out of bounds error in returnFileId when pressing back from adding new flickr images
            if (selectedItems.contains(flickrImagesManager.returnFileId(position))) {
                ShapeDrawable sd = new ShapeDrawable();
                sd.setShape(new RectShape());
                sd.getPaint().setColor(Color.RED);
                sd.getPaint().setStrokeWidth(50f);
                sd.getPaint().setStyle(Paint.Style.STROKE);
                holder.itemView.setForeground(sd);
            } else {
                holder.itemView.setForeground(new ColorDrawable(Color.TRANSPARENT));
            }
            holder.itemView.setOnClickListener(v -> {
                holder.onClick(v);
                notifyDataSetChanged();
            });


        }

        @Override
        public int getItemCount() {
            System.out.println("size = " + images.size());
            return images.size();
        }
    }
}