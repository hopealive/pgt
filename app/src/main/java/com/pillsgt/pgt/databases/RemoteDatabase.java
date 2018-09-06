package com.pillsgt.pgt.databases;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.pillsgt.pgt.databases.DAO.RemoteDAO;
import com.pillsgt.pgt.models.remote.Keyword;
import com.pillsgt.pgt.models.remote.PillsUa;

@Database(entities = {Keyword.class, PillsUa.class}, exportSchema = false, version = 1)
public abstract class RemoteDatabase extends RoomDatabase {

    public abstract RemoteDAO remoteDAO();

}
