package com.cmpt276.finddamatch.model;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CustomImagesManager implements Iterable<Bitmap> {
    private static CustomImagesManager CustomImagesManager;
    private List<Bitmap> customImages;
    private List<String> fileId;
    private SaveImages fileLocation;
    private Context context;
    
    private CustomImagesManager(Context context){
        customImages = new ArrayList<>();
        fileId = new ArrayList<>();
        this.context = context;
        fileLocation = new SaveImages(context);
        update();
    }

    public static CustomImagesManager getInstance(Context context){
        if(CustomImagesManager == null){
            CustomImagesManager = new CustomImagesManager(context);
        }
        return CustomImagesManager;
    }

    public void update() {
        String[] temp = fileLocation.getFileNames();
        fileId.addAll(Arrays.asList(temp));
        System.out.println("manager id size = " + fileId.size());
        for (int i = 0; i < fileId.size(); i++){
            System.out.println("load file id at " + i);
            customImages.add(fileLocation.load(fileId.get(i)));
        }
    }

    @NonNull
    @Override
    public Iterator<Bitmap> iterator() {
        return customImages.iterator();
    }

    public void add(Bitmap image, String imageName){
        fileLocation.save(image, imageName);
        customImages.add(image);
        fileId.add(imageName);
    }

    public List<Bitmap> getBitmaps() {
        return customImages;
    }

    public Bitmap getBitmapAt(int position) {
        return customImages.get(position);
    }

    public void deleteImage(String name, int position){
        fileLocation.deleteFile(name);
        customImages.remove(position);
        fileId.remove(position);
    }

    public String returnFileId(int position){
        return fileId.get(position);
    }

    public int numberOfImages(){
        return fileId.size();
    }
}
