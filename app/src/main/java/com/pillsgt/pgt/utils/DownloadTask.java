package com.pillsgt.pgt.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.pillsgt.pgt.UnzipUtil;
import com.pillsgt.pgt.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask {

    private static final String TAG = "Download Task";
    private String downloadUrl = "";
    private Context mContext;

    public DownloadTask(String downloadUrl, Context context) {
        this.downloadUrl = downloadUrl;
        this.mContext = context;

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "Download Started");
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    UnzipUtil unzipUtil = new UnzipUtil(
                            outputFile.getAbsolutePath(),
                            Environment.getExternalStorageDirectory() + "/"
                                    + Utils.downloadDirectory + "/"
                            );
                    unzipUtil.unzip();
                    outputFile.delete();

                    Log.i(TAG, "Download Completed");
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Change button text again after 3sec
                            Log.i(TAG, "Download Again");
                        }
                    }, 3000);

                    Log.e(TAG, "Download Failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "Download Again");
                    }
                }, 3000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());
            }

            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                //Get File if SD card is present
                if ( Utils.isSDCardPresent()) {
                    apkStorage = new File(
                            Environment.getExternalStorageDirectory() + "/"
                                    + Utils.downloadDirectory);
                } else {
                    Log.e(TAG, "Oops!! There is no SD Card.");
                    return null;
                }

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    try {
                        apkStorage.mkdir();
                    } catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "Oops!! Cannot create directory.");
                        return null;
                    }
                    Log.i(TAG, "Directory Created.");
                }

                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.connect();

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());
                }

                //Create Output file in Main File
                outputFile = new File(apkStorage, Utils.downloadFileName);

                //Create New File if not present
                if ( outputFile == null || !outputFile.exists()) {
                    outputFile = new File(apkStorage.getAbsolutePath(), Utils.downloadFileName);
                    Log.i(TAG, "File Created");
                } else {
                    outputFile.delete();
                }
                outputFile.createNewFile();

                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }
                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }

    }

}
