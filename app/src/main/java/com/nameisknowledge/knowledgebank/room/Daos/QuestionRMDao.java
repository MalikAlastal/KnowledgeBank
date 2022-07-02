package com.nameisknowledge.knowledgebank.room.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.nameisknowledge.knowledgebank.room.Entity.QuestionRMD;

import java.util.List;

@Dao
public interface QuestionRMDao {
    @Insert
    void inset(QuestionRMD...questionRMDS);
    @Query("select * from QuestionRMD")
    LiveData<List<QuestionRMD>> getQuestions();
}
