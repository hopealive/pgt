package com.pillsgt.pgt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pillsgt.pgt.databases.InitDatabases;
import com.pillsgt.pgt.databases.LocalDatabase;
import com.pillsgt.pgt.databases.RemoteDatabase;

public class AppActivity extends AppCompatActivity  {

    public static LocalDatabase localDatabase;
    public static RemoteDatabase remoteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDatabases();
    }

    protected void initDatabases(){
        localDatabase = InitDatabases.buildLocalDatabase(getApplicationContext());
        remoteDatabase = InitDatabases.buildRemoteDatabase(getApplicationContext());
    }
}
