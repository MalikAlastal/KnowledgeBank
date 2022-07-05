package com.nameisknowledge.knowledgebank.modelClasses;

public class PlayerScoreMD {
    private int duoMode,questionsMode,soloMode,mapsMode;
    private String id;

    public PlayerScoreMD(String id,int duoMode, int questionsMode, int soloMode, int mapsMode) {
        this.duoMode = duoMode;
        this.questionsMode = questionsMode;
        this.soloMode = soloMode;
        this.mapsMode = mapsMode;
        this.id = id;
    }

    public PlayerScoreMD() {
    }

    public int getDuoMode() {
        return duoMode;
    }

    public void setDuoMode(int duoMode) {
        this.duoMode = duoMode;
    }

    public int getQuestionsMode() {
        return questionsMode;
    }

    public void setQuestionsMode(int questionsMode) {
        this.questionsMode = questionsMode;
    }

    public int getSoloMode() {
        return soloMode;
    }

    public void setSoloMode(int soloMode) {
        this.soloMode = soloMode;
    }

    public int getMapsMode() {
        return mapsMode;
    }

    public void setMapsMode(int mapsMode) {
        this.mapsMode = mapsMode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
