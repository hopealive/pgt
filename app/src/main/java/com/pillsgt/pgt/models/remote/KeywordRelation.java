package com.pillsgt.pgt.models.remote;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "keyword_relations")
public class KeywordRelation {

    @PrimaryKey
    private int id;

    private int keyword_id;

    private int original_id;

    private String original_table;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKeyword_id() {
        return keyword_id;
    }

    public void setKeyword_id(int keyword_id) {
        this.keyword_id = keyword_id;
    }

    public int getOriginal_id() {
        return original_id;
    }

    public void setOriginal_id(int original_id) {
        this.original_id = original_id;
    }

    public String getOriginal_table() {
        return original_table;
    }

    public void setOriginal_table(String original_table) {
        this.original_table = original_table;
    }
}
