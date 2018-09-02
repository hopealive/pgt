package com.pillsgt.pgt;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.models.UserSetting;
import com.pillsgt.pgt.utils.Converters;

@Database(entities = {PillRule.class, UserSetting.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class MyAppDatabase extends RoomDatabase {

    public abstract MyDAO myDAO();
}
