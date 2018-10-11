package com.pillsgt.pgt.databases;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.pillsgt.pgt.utils.Utils;

public class InitDatabases {


    public static LocalDatabase buildLocalDatabase(Context context ){
        return Room.databaseBuilder(context,LocalDatabase.class, Utils.localDbName)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration() //todo: temporary. remove before release. Migrations for future
                .build();
    }

    public static RemoteDatabase buildRemoteDatabase(Context context ){
        return Room.databaseBuilder(context,RemoteDatabase.class, Utils.remoteDbName)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration() //todo: temporary. remove before release. Migrations for future
                .build();
    }

}
