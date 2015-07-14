package org.bdaoust.project1spotifystreamerstage1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public abstract class ThumbnailDownloader {

    private String thumbnailPath;

    public ThumbnailDownloader(String thumbnailPath){
        this.thumbnailPath = thumbnailPath;
    }

    public void download(){
        DownloadThumbnailTask downloadThumbnail = new DownloadThumbnailTask(this.thumbnailPath);
        downloadThumbnail.execute();
    }

    private class DownloadThumbnailTask extends AsyncTask<Void, Void, Void> {

        private Bitmap bitmap;
        private String thumbnailPath;

        public DownloadThumbnailTask(String thumbnailPath){
            this.thumbnailPath = thumbnailPath;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL url = new URL(this.thumbnailPath);
                URLConnection urlConnection = url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                bitmap = BitmapFactory.decodeStream(in);
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            onThumbnailDowloaded(thumbnailPath, bitmap);
            super.onPostExecute(aVoid);
        }
    }

    public abstract void onThumbnailDowloaded(String thumbnailPath, Bitmap bitmap);
}
