package com.nameisknowledge.knowledgebank.ModelClasses;

public class PlayerScoreMD {
    private int duoModeScore,questionsModeScore,soloModeScore;

    public PlayerScoreMD(int duoModeScore, int questionsModeScore, int soloModeScore) {
        this.duoModeScore = duoModeScore;
        this.questionsModeScore = questionsModeScore;
        this.soloModeScore = soloModeScore;
    }

    public PlayerScoreMD() {
    }

    public int getDuoModeScore() {
        return duoModeScore;
    }

    public void setDuoModeScore(int duoModeScore) {
        this.duoModeScore = duoModeScore;
    }

    public int getQuestionsModeScore() {
        return questionsModeScore;
    }

    public void setQuestionsModeScore(int questionsModeScore) {
        this.questionsModeScore = questionsModeScore;
    }

    public int getSoloModeScore() {
        return soloModeScore;
    }

    public void setSoloModeScore(int soloModeScore) {
        this.soloModeScore = soloModeScore;
    }
}
