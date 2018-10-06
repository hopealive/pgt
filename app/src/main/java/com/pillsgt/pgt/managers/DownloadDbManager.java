package com.pillsgt.pgt.managers;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.pillsgt.pgt.UnzipUtil;
import com.pillsgt.pgt.databases.LocalDatabase;
import com.pillsgt.pgt.models.UserSetting;
import com.pillsgt.pgt.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadDbManager {

    private static final String TAG = "Download Task";
    private String syncDbLink;
    private String cDbVersion;
    private Context mContext;
    private ConnectivityManager connectivityManager;

    public DownloadDbManager(String cDbVersion, String syncDbLink, Context mContext, ConnectivityManager connectivityManager) {
        this.syncDbLink = syncDbLink;
        this.cDbVersion = cDbVersion;
        this.mContext = mContext;
        this.connectivityManager = connectivityManager;

        //Start Downloading Task
        new DownloadDbManager.DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {
        public LocalDatabase localDatabase;

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            Utils.isConnectingToInternet( connectivityManager );
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            initMyDatabase();
            updateDbVersion();
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if ( download() ){
                unzipFile();
            }

            return null;
        }

        protected boolean download() {
            try {
                URL url = new URL(syncDbLink);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.connect();

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());
                }

                apkStorage = new File(Environment.getDataDirectory()
                        + "/data/com.pillsgt.pgt/databases/");


                InputStream is = c.getInputStream();
                BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);

                outputFile = new File(apkStorage + "/" + Utils.downloadFileArchiveName);
                if (outputFile.exists())
                {
                    outputFile.delete();
                }
                outputFile.createNewFile();

                FileOutputStream outStream = new FileOutputStream(outputFile);
                byte[] buff = new byte[5 * 1024];

                int len;
                while ((len = inStream.read(buff)) != -1)
                {
                    outStream.write(buff, 0, len);
                }

                outStream.flush();
                outStream.close();
                inStream.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }
            return false;
        }

        protected void unzipFile() {
            try {
                if (outputFile != null) {
                    UnzipUtil unzipUtil = new UnzipUtil(
                            outputFile.getAbsolutePath(),
                            apkStorage.getAbsolutePath()+"/"
                    );
                    unzipUtil.unzip();
                    outputFile.delete();

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
        }

        protected void updateDbVersion() {
            UserSetting dbVersionUserSetting;
            dbVersionUserSetting = localDatabase.localDAO().loadUserSettingByName("db_version");
            dbVersionUserSetting.setValue(cDbVersion);
            localDatabase.localDAO().updateUserSetting(dbVersionUserSetting);
        }


        protected void initMyDatabase() {
            localDatabase = Room.databaseBuilder(mContext, LocalDatabase.class, Utils.localDbName)
                    .allowMainThreadQueries()
                    .build();
        }

    }

}
