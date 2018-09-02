package com.pillsgt.pgt;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.models.UserSetting;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface MyDAO {

    //pill_rules

    @Query("select * from pill_rules where id = :id")
    PillRule loadRuleById(int id);

    @Query("select * from pill_rules")
    public List<PillRule> getRules();

    @Insert
    void addRule(PillRule rule);

    @Update(onConflict = REPLACE)
    void updateRule(PillRule rule);

    @Query("delete from pill_rules where id= :id")
    void deleteRuleById(int id);


    //user_settings

    @Query("select * from user_settings")
    public List<UserSetting> getUserSettings();

    @Query("select * from user_settings where name = :name")
    UserSetting loadUserSettingByName(String name);

    @Insert
    void addUserSetting(UserSetting userSetting);

    @Update(onConflict = REPLACE)
    void updateUserSetting(UserSetting userSetting);



}
