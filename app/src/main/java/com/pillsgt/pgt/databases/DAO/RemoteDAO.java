package com.pillsgt.pgt.databases.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pillsgt.pgt.models.remote.Keyword;
import com.pillsgt.pgt.models.remote.PillsUa;

import java.util.List;

@Dao
public interface RemoteDAO {

    //keywords


    @Query("select * from keywords")
    public List<Keyword> getKeywords();

    @Query("select * from keywords where keyword like '%' || :keyword || '%'")
    public List<Keyword> searchKeywords(String keyword);


}
