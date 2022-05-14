package com.nameisknowledge.knowledgebank.ModelClasses;

import java.io.Serializable;

public class QuestionMD implements Serializable {
    private String question,answer;
    private int index;

    public QuestionMD(String question, String answer, int index) {
        this.question = question;
        this.answer = answer;
        this.index = index;
    }

    public QuestionMD(String question, String answer) {
        this.question = question;
        this.answer = answer;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
