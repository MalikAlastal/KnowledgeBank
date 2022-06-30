package com.nameisknowledge.knowledgebank.room.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class QuestionRMD {
    @PrimaryKey
    private int index;
    private String question,answer;
    public QuestionRMD(String question, String answer, int index) {
        this.question = question;
        this.answer = answer;
        this.index = index;
    }

    public QuestionRMD() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
