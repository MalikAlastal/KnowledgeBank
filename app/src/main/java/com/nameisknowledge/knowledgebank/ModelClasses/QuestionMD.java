package com.nameisknowledge.knowledgebank.ModelClasses;

import java.io.Serializable;

public class QuestionMD implements Serializable {
    private String question,answer,category;
    private int index;

    public QuestionMD(String question, String answer, String category,int index) {
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.index = index;
    }

    public QuestionMD() {
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
