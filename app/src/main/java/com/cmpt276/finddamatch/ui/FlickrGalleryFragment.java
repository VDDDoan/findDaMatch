package com.cmpt276.finddamatch.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.cmpt276.finddamatch.model.CustomImagesManager;
import com.cmpt276.finddamatch.model.GalleryItem;
import com.cmpt276.finddamatch.R;
import com.cmpt276.finddamatch.model.ThumbnailDownloader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class FlickrGalleryFragment extends Fragment implements FlickrGalleryActivity.saveInterface {

    private static final String TAG = "FlickrGalleryFragment";

    private RecyclerView photoRecyclerView;
    private List<GalleryItem> items = new ArrayList<>();
    private List<String> selectedItems;
    private ThumbnailDownloader<PhotoHolder> thumbnailDownloader;
    private CustomImagesManager flickrManager;

    public static FlickrGalleryFragment newInstance() {
        return new FlickrGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateItems();
        flickrManager = CustomImagesManager.getInstance(getActivity());
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
        View v = inflater.inflate(R.layout.container_flickr_gallery, container, false);
        photoRecyclerView = v.findViewById(R.id.photo_recycler_view);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        selectedItems = new ArrayList<>();

        return v;
    }

    @Override
    public void saveImagesToDeck() {
        if (selectedItems.size() > 0) {
            for (int i = 0; i < selectedItems.size(); i++) {
                new SaveAsyncTask().execute(selectedItems.get(i));
                System.out.println("selectedname = " + selectedItems.get(i));
            }
        }
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
            int position = getAdapterPosition();
            GalleryItem galleryItem = items.get(position);
            String name = galleryItem.getUrl();
            Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();

            if (!selectedItems.contains(name)) {
                selectedItems.add(name);
            } else {
                selectedItems.remove(name);
            }
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> galleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            this.galleryItems = galleryItems;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.layout_gallery_list_item, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = galleryItems.get(position);
            Drawable placeholder = ContextCompat.getDrawable(getActivity(), R.drawable.button_style);
            photoHolder.bindDrawable(placeholder);

            thumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
            if (selectedItems.contains(items.get(position).getUrl())) {
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
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return galleryItems.size();
        }
    }
    private void updateItems(){
        String query = FlickrGalleryActivity.getWords();
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
            FlickrGalleryFragment.this.items = items;
            setupAdapter();
        }

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
            System.out.println("url = " + url_value);
            try {
                Bitmap imageToSave = BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
                flickrManager.add(imageToSave, url_value.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}