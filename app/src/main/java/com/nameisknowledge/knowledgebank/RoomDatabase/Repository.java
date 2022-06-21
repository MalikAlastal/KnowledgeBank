package com.nameisknowledge.knowledgebank.RoomDatabase;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.nameisknowledge.knowledgebank.RoomDatabase.Daos.QuestionRMDao;
import com.nameisknowledge.knowledgebank.RoomDatabase.Entity.QuestionRMD;

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
