package io.oneinfinity.eventmanagement;

/**
 * Created by ujjwal on 12/19/2017.
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
public class AsyncTaskLoadImage  extends AsyncTask<String, String, Bitmap> {
    private final static String TAG = "AsyncTaskLoadImage";
    private ImageView imageView;
    private int imageWidth;
    private int imageHeight;

    public AsyncTaskLoadImage(ImageView imageView, int imageWidth, int imageHeight) {
        this.imageView = imageView;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }


    public AsyncTaskLoadImage(ImageView imageView) {
        this.imageView = imageView;
    }
    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(params[0]);
            bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, true);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return bitmap;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
