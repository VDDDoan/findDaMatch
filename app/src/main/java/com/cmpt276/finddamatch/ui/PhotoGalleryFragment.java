package com.cmpt276.finddamatch.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import com.cmpt276.finddamatch.model.FlickrFetchr;
import com.cmpt276.finddamatch.model.GalleryItem;
import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.ThumbnailDownloader;

import java.util.ArrayList;
import java.util.List;


public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView photoRecyclerView;
    private List<GalleryItem> items = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> thumbnailDownloader;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateItems();
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

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView itemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);

            itemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
        }

        public void bindDrawable(Drawable drawable) {
            itemImageView.setImageDrawable(drawable);
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
            thumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
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