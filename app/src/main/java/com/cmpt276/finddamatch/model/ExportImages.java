// this class saves and exports the cards to androids photo gallery

package com.cmpt276.finddamatch.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

// http://hackerseve.com/android-save-view-as-image-and-share-externally/

public class ExportImages {

    private View exportLayout;
    private Bitmap exportImage;
    private Context context;

    public ExportImages(View view, Context context) {
        this.context = context;
        exportLayout = view;
        exportImage = generateBitmap(exportLayout);
        if (checkPermissionREAD_EXTERNAL_STORAGE(context)) {
            saveImage(exportImage);
        }
    }
    private Bitmap generateBitmap(View view) {

        // Create a bitmap with same dimensions as view
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        // Create a canvas using bitmap
        Canvas canvas = new Canvas(bitmap);

        /**
         We need to check if view as backround image.
         It is important in case of overlays like photo frame feature
         */
        Drawable background = view.getBackground();
        if (background != null) {
            background.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }

        // draw the view on the canvas
        view.draw(canvas);

        // final bitmap
        return bitmap;
    }

    private void saveImage(Bitmap bitmap) {

        // Get the destination folder for saved images defined in strings.xml
        String dstFolder = "/CardImages/";

        // Create Destination folder in external storage. This will require EXTERNAL STORAGE permission
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File imgDir = new File(externalStorageDirectory.getAbsolutePath(), dstFolder);
        imgDir.mkdirs();

        // Generate a random file name for image
        @SuppressLint("SimpleDateFormat") String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpeg";
        File localFile = new File(imgDir, imageName);
        String path = "file://" + externalStorageDirectory.getAbsolutePath() + "/" + dstFolder;
        MediaStore.Images.Media.insertImage(context.getContentResolver(), exportImage, imageName , null);

        try {
            FileOutputStream fos = new FileOutputStream(localFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(new File(path))));
        } catch (Exception e)  {
            e.printStackTrace();
        }

    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                (dialog, which) -> ActivityCompat.requestPermissions((Activity) context,
                        new String[] { permission },
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE));
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }



}
