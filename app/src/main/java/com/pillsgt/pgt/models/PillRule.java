package com.pillsgt.pgt.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.sql.Date;

@Entity(tableName = "pill_rules")
public class PillRule {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    private int cron_type;

    private int cron_interval;

    private String start_date;

    private String end_date;

    private int is_continues;

    private int number_days;

    private int duration_type;

    private String duration_value;

    private String updated_at;

    private String created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCron_type() {
        return cron_type;
    }

    public void setCron_type(int cron_type) {
        this.cron_type = cron_type;
    }

    public int getCron_interval() {
        return cron_interval;
    }

    public void setCron_interval(int cron_interval) {
        this.cron_interval = cron_interval;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getIs_continues() {
        return is_continues;
    }

    public void setIs_continues(int is_continues) {
        this.is_continues = is_continues;
    }

    public int getNumber_days() {
        return number_days;
    }

    public void setNumber_days(int number_days) {
        this.number_days = number_days;
    }

    public int getDuration_type() {
        return duration_type;
    }

    public void setDuration_type(int duration_type) {
        this.duration_type = duration_type;
    }

    public String getDuration_value() {
        return duration_value;
    }

    public void setDuration_value(String duration_value) {
        this.duration_value = duration_value;
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
