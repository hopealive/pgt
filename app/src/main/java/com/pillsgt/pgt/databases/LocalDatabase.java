package com.pillsgt.pgt.databases;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.pillsgt.pgt.databases.DAO.LocalDAO;
import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.models.UserSetting;

@Database(entities = {PillRule.class, UserSetting.class}, version = 1)
public abstract class LocalDatabase extends RoomDatabase {

    public abstract LocalDAO localDAO();
}
