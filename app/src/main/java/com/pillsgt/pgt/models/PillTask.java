package com.pillsgt.pgt.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "pill_tasks")
public class PillTask {

    public static final String STATUS_NEW = "new";
    public static final String STATUS_NOTTIFIED = "taked";
    public static final String STATUS_TAKED = "taked";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_CANCELED = "canceled";


    @PrimaryKey(autoGenerate = true)
    private int id;

    private int rule_id;

    private String title;

    private String short_title;

    private String description;

    private String status;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShort_title() {
        return short_title;
    }

    public void setShort_title(String short_title) {
        this.short_title = short_title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
