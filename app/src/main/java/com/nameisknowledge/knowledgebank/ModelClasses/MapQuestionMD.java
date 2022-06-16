package com.nameisknowledge.knowledgebank.ModelClasses;

public class MapQuestionMD extends QuestionMD{
    private int index;
    public MapQuestionMD(String question, String answer, int index,String category,String hardLevel) {
        super(question, answer,category,hardLevel);
        this.index = index;
    }

    public MapQuestionMD() {
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
