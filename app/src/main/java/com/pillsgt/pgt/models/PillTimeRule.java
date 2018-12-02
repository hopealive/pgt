package com.pillsgt.pgt.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "pill_time_rules")
public class PillTimeRule {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int rule_id;

    private String alarm_at;

    private String updated_at;

    private String created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRule_id() {
        return rule_id;
    }

    public void setRule_id(int rule_id) {
        this.rule_id = rule_id;
    }

    public String getAlarm_at() {
        return alarm_at;
    }

    public void setAlarm_at(String alarm_at) {
        this.alarm_at = alarm_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
