package com.pillsgt.pgt;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONException;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pillsgt.pgt.databases.InitDatabases;
import com.pillsgt.pgt.databases.LocalDatabase;
import com.pillsgt.pgt.managers.DocsManager;
import com.pillsgt.pgt.models.UserSetting;
import com.pillsgt.pgt.managers.DownloadDbManager;
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

        localDatabase = InitDatabases.buildLocalDatabase(getApplicationContext());
        initUserSettings();

        Boolean firstStart = testLocalUserSettingByName("first_start");
        if (!firstStart){
            this.duration = 0;
            createDefaultUserSettings();
            initUserSettings();
        }

        apiStartRequest();
        setServiceAlarm();

        long elapsedTime = (System.currentTimeMillis() - sTime)/1000;
        if ( elapsedTime > this.duration){
            this.duration = elapsedTime+1;
        }
        SystemClock.sleep(TimeUnit.SECONDS.toMillis(this.duration));

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
                    String syncDbLink = response.getString("sync_db_link");
                    compareDatabases( cDbVersion, syncDbLink );

                    String cDocsVersion = response.getString("current_docs_version");
                    String syncDocsLink = response.getString("sync_docs_link");
                    compareDocs( cDocsVersion, syncDocsLink );

                    if (!testLocalUserSettingByName("first_start")) {
                        UserSetting firstStart = new UserSetting();
                        firstStart.setName("first_start");
                        firstStart.setValue("exists");
                        localDatabase.localDAO().addUserSetting(firstStart);
                    }

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

    public void setServiceAlarm(){
        Context sContext = getApplicationContext();
        Intent intent = new Intent(sContext, RemindService.class);
        PendingIntent pendingIntent = PendingIntent.getService(sContext, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)sContext.getSystemService(Context.ALARM_SERVICE);
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

//todo: think about this
//        Integer i = 0;
//        String eatingField;
//        for (final String field : PillsDateTimeLists.defaultEatingFields ){
//            eatingField = "time_"+field;
//            if (!testLocalUserSettingByName(eatingField)){
//                fUserSetting.setName(eatingField);
//                fUserSetting.setValue( PillsDateTimeLists.defaultEatingTimes.get(i) );
//                localDatabase.localDAO().addUserSetting(fUserSetting);
//            } else {
//                Log.e(TAG, "Eating Field "+eatingField+" already exists in new DB");
//            }
//            ++i;
//        }
    }

    /**
     * If cDbVersion from server > current update download fresh DB from dDbLink
     * @String cDbVersion current DB version from web storage
     * @String dDbLink link for download DB from web storage
     */
    public void compareDatabases(final String cDbVersion, String syncDbLink){
        String localDbFieldName = "db_version";
        boolean localVersionExists = false;
        boolean needSync = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (!userSettings.isEmpty()){
            //exists version of remote DB in local DB
            for (final UserSetting userSetting : userSettings ) {
                if ( userSetting.getName().equals(localDbFieldName) ) {
                    localVersionExists = true;
                    if (Integer.valueOf( userSetting.getValue() )  < Integer.valueOf( cDbVersion ) ) {
                        //download & update DB-file & update setting
                        needSync = true;
                    }
                }
            }
        }

        if (userSettings.isEmpty() || localVersionExists != true){
            //no version of remote DB in local DB: maybe first time or error
            needSync = true;

            //create setting
            initNullField(localDbFieldName);
        }

        if ( needSync == true ){
            //download & create DB-file: Start thread for download
            new DownloadDbManager( cDbVersion, syncDbLink, getApplicationContext(), connectivityManager );
        }

    }

    protected void compareDocs( String cDocsVersion, String syncDocsLink ){
        boolean localVersionExists = false;
        boolean needSync = false;
        String localDbFieldName = "docs_version";
        if (!userSettings.isEmpty()){
            //exists version of remote DB in local DB
            for (final UserSetting userSetting : userSettings ) {
                if ( userSetting.getName().equals(localDbFieldName) ) {
                    localVersionExists = true;
                    if (Integer.valueOf( userSetting.getValue() )  < Integer.valueOf( cDocsVersion ) ) {
                        //download & update DB-file & update setting
                        needSync = true;
                    }
                }
            }
        }

        if (userSettings.isEmpty() || localVersionExists != true){
            //no version of remote DB in local DB: maybe first time or error
            needSync = true;

            //create setting
            initNullField(localDbFieldName);
        }

        if ( needSync == true ){
            //download docs
            new DocsManager().apiStartRequest(this, syncDocsLink);
        }

    }

    protected void initNullField(String fieldName) {
        UserSetting versionUserSetting = new UserSetting();
        versionUserSetting.setName(fieldName);
        versionUserSetting.setValue("0");
        localDatabase.localDAO().addUserSetting(versionUserSetting);

    }
}
