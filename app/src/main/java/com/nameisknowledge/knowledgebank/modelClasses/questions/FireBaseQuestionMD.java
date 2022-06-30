package com.nameisknowledge.knowledgebank.modelClasses.questions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FireBaseQuestionMD extends Question implements Serializable {
    private String category;
    protected int index;
    private final List<String> hints = new ArrayList<>();

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

    public List<String> getHints() {
        return hints;
    }
}
