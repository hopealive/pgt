package com.pillsgt.pgt.databases;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.pillsgt.pgt.databases.DAO.LocalDAO;
import com.pillsgt.pgt.models.Doc;
import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.models.PillTask;
import com.pillsgt.pgt.models.UserSetting;

@Database(entities = {UserSetting.class, PillRule.class, PillTask.class, Doc.class}, exportSchema = false, version = 1)
public abstract class LocalDatabase extends RoomDatabase {

    public abstract LocalDAO localDAO();

}
