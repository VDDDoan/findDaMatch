package com.cmpt276.finddamatch.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cmpt276.finddamatch.model.FlickrFetchr;
import com.cmpt276.finddamatch.model.FlickrImagesManager;
import com.cmpt276.finddamatch.model.GalleryItem;
import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.ThumbnailDownloader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView photoRecyclerView;
    private List<GalleryItem> items = new ArrayList<>();
    private List<GalleryItem> selectedItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> thumbnailDownloader;
    private FlickrImagesManager flickrManager;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateItems();
        flickrManager = FlickrImagesManager.get(getActivity());
        Handler responseHandler = new Handler();
        thumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        thumbnailDownloader.setThumbnailDownloadListener(
                (photoHolder, bitmap) -> {
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    photoHolder.bindDrawable(drawable);
                }
        );
        thumbnailDownloader.start();
        thumbnailDownloader.getLooper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        photoRecyclerView = v.findViewById(R.id.photo_recycler_view);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return v;
    }

    private void setupAdapter() {
        if (isAdded()) {
            photoRecyclerView.setAdapter(new PhotoAdapter(items));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        thumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thumbnailDownloader.quit();
    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView itemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);

            itemImageView = itemView.findViewById(R.id.item_image_view);
            itemImageView.setOnClickListener(this);
        }

        public void bindDrawable(Drawable drawable) {
            itemImageView.setImageDrawable(drawable);
        }

        @Override
        public void onClick(View v) {
            // TODO implement the following
            // if item is in the list
            //      remove from selected list
            //      deselect
            // else
            //      add to selected list
            //      ( run async task to save the gallery items )
            int position = getAdapterPosition();
            GalleryItem galleryItem = items.get(position);
            String name = galleryItem.getUrl();
            Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
            new SaveAsyncTask().execute(name);
        }

        private class SaveAsyncTask extends AsyncTask<String, Void, Void> {

            @Override
            protected Void doInBackground(String... strings) {
                URL url_value = null;
                try {
                    url_value = new URL(strings[0]);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    Bitmap imageToSave = BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
                    flickrManager.add(imageToSave);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> galleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            this.galleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = galleryItems.get(position);
            Drawable placeholder = ContextCompat.getDrawable(getActivity(), R.drawable.button_style);;
            photoHolder.bindDrawable(placeholder);
            //photoHolder.itemView.setSelected();
            thumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
            if (selectedPosition == position) {
                ShapeDrawable sd = new ShapeDrawable();
                sd.setShape(new RectShape());
                sd.getPaint().setColor(Color.BLUE);
                sd.getPaint().setStrokeWidth(50f);
                sd.getPaint().setStyle(Paint.Style.STROKE);
                photoHolder.itemView.setForeground(sd);
            } else {
                photoHolder.itemView.setForeground(new ColorDrawable(Color.TRANSPARENT));
            }
            photoHolder.itemView.setOnClickListener(v -> {
                photoHolder.onClick(v);
                selectedPosition = position;
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return galleryItems.size();
        }
    }
    private void updateItems(){
        String query = PhotoGalleryActivity.getWords();
        new FetchItemsTask(query).execute();
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        private String query;

        public FetchItemsTask(String query) {
            this.query = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            if (query == null) {
                return new FlickrFetchr().fetchRecentPhotos();
            } else {
                return new FlickrFetchr().searchPhotos(query);
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            PhotoGalleryFragment.this.items = items;
            setupAdapter();
        }

    }

}