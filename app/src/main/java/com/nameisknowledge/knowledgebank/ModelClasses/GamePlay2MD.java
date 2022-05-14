package com.nameisknowledge.knowledgebank.ModelClasses;

import java.util.List;
import java.util.Map;

public class GamePlay2MD {
    private Map<String,Integer> ids;
    private Map<String,List<QuestionMD>> data;
    private String winner;
    private int currentQuestion;

    public GamePlay2MD() {
    }

    public GamePlay2MD(Map<String, Integer> ids, Map<String, List<QuestionMD>> data) {
        this.ids = ids;
        this.data = data;
        this.winner = "";
        this.currentQuestion = 0;
    }

    public Map<String, Integer> getIds() {
        return ids;
    }

    public void setIds(Map<String, Integer> ids) {
        this.ids = ids;
    }

    public Map<String, List<QuestionMD>> getData() {
        return data;
    }

    public void setData(Map<String, List<QuestionMD>> data) {
        this.data = data;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }
}
