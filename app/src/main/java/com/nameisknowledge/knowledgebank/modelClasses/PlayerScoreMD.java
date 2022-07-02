package com.nameisknowledge.knowledgebank.modelClasses;

public class PlayerScoreMD {
    private int DuoMode,QuestionsMode,SoloMode;
    private String id;

    public PlayerScoreMD(String id,int DuoMode, int QuestionsMode, int SoloMode) {
        this.DuoMode = DuoMode;
        this.QuestionsMode = QuestionsMode;
        this.SoloMode = SoloMode;
        this.id = id;
    }

    public PlayerScoreMD() {
    }

    public int getDuoMode() {
        return DuoMode;
    }

    public void setDuoMode(int duoMode) {
        DuoMode = duoMode;
    }

    public int getQuestionsMode() {
        return QuestionsMode;
    }

    public void setQuestionsMode(int questionsMode) {
        QuestionsMode = questionsMode;
    }

    public int getSoloMode() {
        return SoloMode;
    }

    public void setSoloMode(int soloMode) {
        SoloMode = soloMode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
