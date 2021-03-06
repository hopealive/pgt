package com.pillsgt.pgt.databases.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.models.PillTimeRule;
import com.pillsgt.pgt.models.PillTask;
import com.pillsgt.pgt.models.UserSetting;
import com.pillsgt.pgt.models.Doc;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface LocalDAO {

    //user_settings

    @Query("select * from user_settings")
    List<UserSetting> getUserSettings();

    @Query("select * from user_settings where name = :name")
    UserSetting loadUserSettingByName(String name);

    @Insert
    void addUserSetting(UserSetting userSetting);

    @Update(onConflict = REPLACE)
    void updateUserSetting(UserSetting userSetting);


    //pill_rules
    @Query("select * from pill_rules where id = :id")
    PillRule loadRuleById(int id);

    @Query("select * from pill_rules")
    List<PillRule> getRules();

    @Query("select * from pill_rules where pill_id = :id")
    List<PillRule> getRulesByPillId(int id);

    @Query("select pill_id from pill_rules")
    List<Integer> getPillIds();

    @Insert
    long addRule(PillRule rule);

    @Update(onConflict = REPLACE)
    void updateRule(PillRule rule);

    @Query("delete from pill_rules where id= :id")
    void deleteRuleById(int id);


    //pill_time_rules
    @Query("select * from pill_time_rules where rule_id = :rule_id")
    List<PillTimeRule> loadTimeRulesByRuleId(int rule_id);

    @Query("delete from pill_time_rules where rule_id= :rule_id")
    void deletePillTimeRuleByRuleId(int rule_id);

    @Insert
    void addPillTimeRule(PillTimeRule pillTimeRule);


    //pill_tasks
    @Query("select count(id) from pill_tasks where strftime('%Y-%m-%d', replace(alarm_at, \"GMT+00:00\", \"\")) = date('now')")
    String countPillTasks();

    @Query("select * from pill_tasks where id = :id")
    PillTask loadPillTaskById(int id);

    @Query("select * from pill_tasks where alarm_at >= :date_missed and alarm_at <= :date_future  and status = :status")
    List<PillTask> loadNotifyTasks(String date_missed, String date_future, String status);

    @Query("select * from pill_tasks where alarm_at >= :date_missed and alarm_at <= :date_future  and status = :status limit 1")
    PillTask loadNotifyTask(String date_missed, String date_future, String status);


    @Insert
    void addPillTask(PillTask pillTask);

    @Update(onConflict = REPLACE)
    void updatePillTask(PillTask pillTask);

    @Query("delete from pill_tasks where id= :id")
    void deletePillTaskById(int id);

    @Query("delete from pill_tasks where rule_id= :rule_id")
    void deletePillTaskByRuleId(int rule_id);


    //docs

    @Query("select * from docs where alias = :alias")
    Doc loadDocByAlias(String alias);

    @Insert
    void addDoc(Doc doc);

    @Update(onConflict = REPLACE)
    void updateDoc(Doc doc);



}
