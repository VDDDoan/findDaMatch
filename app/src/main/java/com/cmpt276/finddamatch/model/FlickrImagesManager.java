package com.cmpt276.finddamatch.model;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class FlickrImagesManager implements Iterable<Bitmap> {
    private static FlickrImagesManager FlickrImagesManager;
    private List<Bitmap> flickrImages;
    private String[] fileId;
    private SaveImages fileLocation;
    private Context context;
    
    private FlickrImagesManager(Context context){
        flickrImages = new ArrayList<>();
        this.context = context;
        fileLocation = new SaveImages(context);
        update();
    }

    public static FlickrImagesManager getInstance(Context context){
        if(FlickrImagesManager == null){
            FlickrImagesManager = new FlickrImagesManager(context);
        }
        return FlickrImagesManager;
    }

    private void update() {
        fileId = fileLocation.getFileNames();
        for (int i = 0; 0 < fileId.length; i++){
           fileLocation.setFileName(fileId[i]);
           flickrImages.add(fileLocation.load());
        }
    }

    @NonNull
    @Override
    public Iterator<Bitmap> iterator() {
        return flickrImages.iterator();
    }

    public void add (Bitmap image){
        UUID random = UUID.randomUUID();
        fileLocation.setFileName(random.toString());
        fileLocation.save(image);
    }

    public List<Bitmap> getBitmaps() {
        return flickrImages;
    }

    public Bitmap getBitmapAt(int position) {
        return flickrImages.get(position);
    }
}
