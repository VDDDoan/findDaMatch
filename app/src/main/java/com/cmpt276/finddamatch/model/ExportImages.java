// this class saves and exports the cards to androids photo gallery

package com.cmpt276.finddamatch.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.cmpt276.finddamatch.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.Settings.System.getString;

// http://hackerseve.com/android-save-view-as-image-and-share-externally/

public class ExportImages {

    private View exportView;
    private Bitmap exportImage;
    private Context context;

    ExportImages(View view, Context context){
        this.context = context;
        exportView = view;
        exportImage = generateBitmap();
        saveImage(exportImage);
    }

    private Bitmap generateBitmap() {

        // Create a bitmap with same dimensions as view
        Bitmap bitmap = Bitmap.createBitmap(exportView.getWidth(), exportView.getHeight(), Bitmap.Config.ARGB_8888);

        // Create a canvas using bitmap
        Canvas canvas = new Canvas(bitmap);

        /*
         We need to check if view as background image.
         It is important in case of overlays like photo frame feature
         */
        Drawable background = exportView.getBackground();
        if (background != null) {
            background.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }

        // draw the view on the canvas
        exportView.draw(canvas);

        // final bitmap
        return bitmap;
    }

    private void saveImage(Bitmap bitmap) {

        // Get the destination folder for saved images defined in strings.xml
        String dstFolder = "CardImages";

        // Create Destination folder in external storage. This will require EXTERNAL STORAGE permission
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File imgDir = new File(externalStorageDirectory.getAbsolutePath(), dstFolder);
        imgDir.mkdirs();

        // Generate a random file name for image
        @SuppressLint("SimpleDateFormat") String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpeg";
        File localFile = new File(imgDir, imageName);
        String path = "file://" + externalStorageDirectory.getAbsolutePath() + "/" + dstFolder;

        MediaStore.Images.Media.insertImage(context.getContentResolver(), exportImage, imageName , dstFolder);

        try {
            FileOutputStream fos = new FileOutputStream(localFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e)  {
            e.printStackTrace();
        }

    }



}
