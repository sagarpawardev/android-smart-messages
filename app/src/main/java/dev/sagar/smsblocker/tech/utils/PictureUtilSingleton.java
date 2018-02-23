package dev.sagar.smsblocker.tech.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.TypedValue;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by sagarpawar on 23/02/18.
 */

public class PictureUtilSingleton {
    private static final PictureUtilSingleton ourInstance = new PictureUtilSingleton();

    public static PictureUtilSingleton getInstance() {
        return ourInstance;
    }

    private PictureUtilSingleton() {
    }

    public Drawable getPictureThumbDrawable(Context context, Drawable drawable){

        int actionBarHeight = 0;
        int actionBarWidth = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,context.getResources().getDisplayMetrics());
            actionBarWidth = actionBarHeight;
        }


        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, actionBarWidth, actionBarHeight);
        Drawable d = new BitmapDrawable(context.getResources(), bitmap);
        return  d;
    }


    public Drawable getPictureThumbDrawable(Context context, Uri uri){
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = decodeSmallImage(is, 100, 100);
            Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            return drawable;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Bitmap decodeSmallImage(InputStream is, int reqWidth, int reqHeight) {

        //First Load to check width and height of Image
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(is, null, options);
        //-- First Load to check width and height of Image

        //Calculate Scaling Factor
        options.inSampleSize = calculateInSampleFactor(options, reqWidth, reqHeight);
        //-- Calculate Scaling Factor

        //Decode reduced Image
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is, null, options);
        //-- Decode reduce Image
    }

    private int calculateInSampleFactor(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
