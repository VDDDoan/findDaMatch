package com.cmpt276.finddamatch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class CustomDeckActivity extends AppCompatActivity {

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
            recreate();
        });

        ImageView add = findViewById(R.id.btn_addtoflickrdeck);
        add.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Where would you like to add photos from?")
                    .setPositiveButton("Flickr Photos", (dialog, id) -> startFlickrGallery())
                    .setNegativeButton("My Photos", (dialog, id) -> startUserGallery());
            AlertDialog alert = builder.create();
            alert.show();
        });

        deckRecyclerView = findViewById(R.id.recycler_view_flickrdeck);
        deckRecyclerView.setLayoutManager(new GridLayoutManager(CustomDeckActivity.this, 3));

        flickrImagesManager = FlickrImagesManager.getInstance(this);

        images = FlickrImagesManager.getInstance(CustomDeckActivity.this).getBitmaps();
        deckRecyclerView.setAdapter(new CustomDeckActivity.DeckImgAdapter(images));
    }

    private void startFlickrGallery() {
        Intent intent = new Intent(CustomDeckActivity.this, FlickrGalleryActivity.class);
        startActivity(intent);
    }

    private void startUserGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deckRecyclerView.getAdapter() != null) {
            images = FlickrImagesManager.getInstance(CustomDeckActivity.this).getBitmaps();
            deckRecyclerView.setAdapter(new CustomDeckActivity.DeckImgAdapter(images));
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

        @NonNull
        @Override
        public DeckImgHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            ImageView view = (ImageView) LayoutInflater.from(CustomDeckActivity.this)
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