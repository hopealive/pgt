package com.pillsgt.pgt;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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

    public void initDatabases(){
        localDatabase = InitDatabases.buildLocalDatabase(getApplicationContext());
        remoteDatabase = InitDatabases.buildRemoteDatabase(getApplicationContext());
    }

    public void initMainMenu(BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener,
                         NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = findViewById(R.id.bottom_nav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //left menu
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();//so interesting hook for menu
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

}
