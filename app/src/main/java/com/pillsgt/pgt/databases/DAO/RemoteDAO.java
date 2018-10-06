package com.pillsgt.pgt.databases.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.models.remote.Keyword;
import com.pillsgt.pgt.models.remote.PillsUa;
import com.pillsgt.pgt.models.remote.KeywordRelation;

import java.util.List;

@Dao
public interface RemoteDAO {

    @Query("select * from keywords")
    public List<Keyword> getKeywords();

    @Query("select * from keywords where keyword like '%' || :keyword || '%'")
    public List<Keyword> searchKeywords(String keyword);

    @Query("select * from keyword_relations where keyword_id=:keywordId")
    public KeywordRelation getKeywordRelationsByKeywordId(int keywordId);

    @Query("select * from pills_ua where id=:id")
    PillsUa loadPillsUaById(int id);

}
