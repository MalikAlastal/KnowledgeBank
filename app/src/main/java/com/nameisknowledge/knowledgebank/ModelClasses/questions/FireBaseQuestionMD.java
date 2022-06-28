package com.nameisknowledge.knowledgebank.ModelClasses.questions;

import java.io.Serializable;

public class FireBaseQuestionMD extends Question implements Serializable {
    private String category;
    private int index;

    public FireBaseQuestionMD(String question, String answer, String category, int index) {
        super(question,answer);
        this.category = category;
        this.index = index;
    }

    public FireBaseQuestionMD() {
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
