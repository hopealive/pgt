package com.pillsgt.pgt;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONException;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pillsgt.pgt.databases.LocalDatabase;
import com.pillsgt.pgt.models.UserSetting;
import com.pillsgt.pgt.utils.DownloadTask;
import com.pillsgt.pgt.utils.Utils;

import android.os.SystemClock;

import java.text.DateFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class App extends Application {

    public static LocalDatabase localDatabase;
    protected List<UserSetting> userSettings;
    private static final String TAG = "initAPP";

    protected long duration = 2;

    @Override
    public void onCreate() {
        super.onCreate();

        long sTime = System.currentTimeMillis();

        initMyDatabase();
        initUserSettings();
        Boolean firstStart = testLocalUserSettingByName("first_start");
        if (!firstStart){
            createDefaultUserSettings();
            initUserSettings();
        }

        apiStartRequest();
        setServiceAlarm (getApplicationContext());

        long elapsedTime = (System.currentTimeMillis() - sTime)/1000;
        if ( elapsedTime > duration){
            duration = elapsedTime+1;
        }
        SystemClock.sleep(TimeUnit.SECONDS.toMillis(duration));

    }

    protected void initMyDatabase() {
        localDatabase = Room.databaseBuilder(getApplicationContext(), LocalDatabase.class, Utils.localDbName)
                .allowMainThreadQueries()
                .build();
    }

    protected void initUserSettings() {
        userSettings = localDatabase.localDAO().getUserSettings();
    }


    /**
     * Void activity block
     */
    public void apiStartRequest(){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET,
                Utils.mainUrl, null, reqSuccessListener(), reqErrorListener());
        queue.add(myReq);
    }

    private Response.Listener<JSONObject> reqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //process response info
                    String cDbVersion = response.getString("current_db_version");
                    String dDbLink = response.getString("download_db_link");
                    compareDatabases( cDbVersion, dDbLink );
                } catch (JSONException e) {
                    Log.e(TAG, "Unknown error in reqSuccessListener: "+e.getMessage());
                }
            }
        };
    }

    private Response.ErrorListener reqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.getMessage() );
            }
        };
    }

    public void setServiceAlarm(Context context){
        Intent intent = new Intent(context, RemindService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), Utils.alaramManagerPeriod*1000, pendingIntent);
    }


    /**
     * Utils block
     */
    protected boolean testLocalUserSettingByName(String name){
        if (!userSettings.isEmpty()) {
            for (final UserSetting userSetting : userSettings) {
                if (userSetting.getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * create basic variable
     */
    protected void createDefaultUserSettings() {
        UserSetting fUserSetting = new UserSetting();
        String defaultSettingValue;
        for (final String field : Utils.defaultUserSettingFields ){
            if (!testLocalUserSettingByName(field)){
                fUserSetting.setName(field);

                defaultSettingValue = "";
                if ( field == "db_version"){
                    defaultSettingValue = "0";
                }

                fUserSetting.setValue(defaultSettingValue);
                localDatabase.localDAO().addUserSetting(fUserSetting);
            } else {
                Log.e(TAG, "Field "+field+" already exists in new DB");
            }
        }

        Integer i = 0;
        String eatingField;
        String[] days = new DateFormatSymbols(Locale.getDefault()).getWeekdays();
        for (final String field : Utils.defaultEatingFields ){
            for (final String day : days ){
                if ( day.length() > 0){
                    eatingField = "task_"+day.toLowerCase()+"_"+field;
                    if (!testLocalUserSettingByName(eatingField)){
                        fUserSetting.setName(eatingField);
                        fUserSetting.setValue( Utils.defaultEatingTimes.get(i) );
                        localDatabase.localDAO().addUserSetting(fUserSetting);
                    } else {
                        Log.e(TAG, "Eating Field "+eatingField+" already exists in new DB");
                    }
                }
            }
            ++i;
        }

    }

    /**
     * If cDbVersion from server > current update download fresh DB from dDbLink
     * @string cDbVersion
     * @string dDbLink
     */
    public void compareDatabases(final String cDbVersion, String dDbLink){
        boolean localDbVerionExists = false;
        UserSetting dbVersionUserSetting = new UserSetting();
        if (!userSettings.isEmpty()){
            for (final UserSetting userSetting : userSettings ) {
                if ( userSetting.getName().equals("db_version") ) {
                    localDbVerionExists = true;
                    if (Integer.valueOf( userSetting.getValue() )  < Integer.valueOf( cDbVersion ) ) {
                        //download & update DB-file & update setting
                        if (downloadDb(dDbLink)) {
                            //update db version
                            userSetting.setValue(cDbVersion);
                            localDatabase.localDAO().updateUserSetting(userSetting);

                        }
                    }
                }
            }
        }

        if (userSettings.isEmpty() || localDbVerionExists != true){
            //download & create DB-file & create setting
            if ( downloadDb(dDbLink) ){
                dbVersionUserSetting.setName("db_version");
                dbVersionUserSetting.setValue(cDbVersion);
                localDatabase.localDAO().addUserSetting(dbVersionUserSetting);
            } else {
                Log.e(TAG, "No link for download");
            }
        }
    }

    /**
     * Start thread for download
     * @string dbUrl
     * @return boolean
     */
    private boolean downloadDb(String dbUrl) {
        if (isConnectingToInternet()) {
            DownloadTask downloadTask = new DownloadTask( dbUrl, getApplicationContext());
            return true;
        } else {
            Log.e(TAG, "No INTERNET for starting download");
        }
        return false;
    }

    /**
     * Check if internet is present or not
     * @return boolean
     */
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


}
