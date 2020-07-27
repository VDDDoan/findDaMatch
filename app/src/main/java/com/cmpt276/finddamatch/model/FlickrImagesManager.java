package com.cmpt276.finddamatch.model;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class FlickrImagesManager implements Iterable<Bitmap> {
    private static FlickrImagesManager FlickrImagesManager;
    private List<Bitmap> flickrImages;
    private List<String> fileId;
    private SaveImages fileLocation;
    private Context context;
    
    private FlickrImagesManager(Context context){
        flickrImages = new ArrayList<>();
        fileId = new ArrayList<>();
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

    public void update() {
        String[] temp = fileLocation.getFileNames();
        fileId.addAll(Arrays.asList(temp));
        System.out.println("manager id size = " + fileId.size());
        for (int i = 0; i < fileId.size(); i++){
            System.out.println("load file id at " + i);
            flickrImages.add(fileLocation.load(fileId.get(i)));
        }
    }

    @NonNull
    @Override
    public Iterator<Bitmap> iterator() {
        return flickrImages.iterator();
    }

    public void add(Bitmap image, URL url_value){
        fileLocation.save(image, url_value.toString());
        flickrImages.add(image);
        fileId.add(url_value.toString());
    }

    public List<Bitmap> getBitmaps() {
        return flickrImages;
    }

    public Bitmap getBitmapAt(int position) {
        return flickrImages.get(position);
    }

    //says that operations not supported in flickrImages.remove and fileId.remove
    public void deleteImage(String name, int position){
        fileLocation.deleteFile(name);
        flickrImages.remove(position);
        fileId.remove(position);
    }
    //keep getting out of bounds error
    public String returnFileId(int position){
        return fileId.get(position);
    }
}
