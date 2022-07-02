package com.nameisknowledge.knowledgebank.room;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.nameisknowledge.knowledgebank.room.Daos.QuestionRMDao;
import com.nameisknowledge.knowledgebank.room.Entity.QuestionRMD;

import java.util.List;

public class Repository {
    private final QuestionRMDao questionRMDao;

    public Repository(Context application) {
        DatabaseClass databaseClass = DatabaseClass.getDatabase(application);
        this.questionRMDao = databaseClass.questionRMDao();
    }

    public void insertQuestion(QuestionRMD...questionRMDS){
        DatabaseClass.databaseWriteExecutor.execute(() -> questionRMDao.inset(questionRMDS));
    }

    public LiveData<List<QuestionRMD>> getQuestions(){
        return questionRMDao.getQuestions();
    }
}
