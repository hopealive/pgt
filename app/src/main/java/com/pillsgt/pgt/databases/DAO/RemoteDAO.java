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

    //keywords

    @Query("select * from keywords")
    List<Keyword> getKeywords();

    @Query("select * from keywords where keyword like '%' || :keyword || '%'")
    List<Keyword> searchKeywords(String keyword);


    //keyword_relations

    @Query("select * from keyword_relations where keyword_id IN (:ids)")
    List<KeywordRelation> getKeywordRelationsByKeywordId(List<String> ids);


    //pills_ua

    @Query("select * from pills_ua where id = :id")
    PillsUa loadPillsUa(int id);


    @Query("select * from pills_ua where id IN (:ids)")
    List<PillsUa> loadPillsUaByIds(List<String> ids);

}
