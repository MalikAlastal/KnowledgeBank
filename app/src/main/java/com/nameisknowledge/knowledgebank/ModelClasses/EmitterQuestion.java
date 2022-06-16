package com.nameisknowledge.knowledgebank.ModelClasses;

public class EmitterQuestion {
    private String tag;
    private int index;

    public EmitterQuestion(String tag, int index) {
        this.tag = tag;
        this.index = index;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}