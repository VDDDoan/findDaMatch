package com.cmpt276.finddamatch.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Most functions by Ilya Gazman on 3/6/2016.
 * https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
 */
public class SaveImages {

    private String directoryName = "images";
    private String fileName = "image.png";
    private Context context;
    private boolean external;

    public SaveImages(Context context) {
        this.context = context;
    }

    public SaveImages setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public SaveImages setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
        return this;
    }

    public void save(Bitmap bitmapImage, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    private File createFile() {
        File directory;
        directory = context.getDir(directoryName, Context.MODE_PRIVATE);
        if(!directory.exists() && !directory.mkdirs()){
            Log.e("ImageSaver","Error creating directory " + directory);
        }

        return new File(directory, fileName);
    }


    public Bitmap load(String nameFile) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(nameFile);
            return BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String[] getFileNames(){
        return context.fileList();
    }

    public void deleteFile (String fileName){
        context.deleteFile(fileName);
    }
}