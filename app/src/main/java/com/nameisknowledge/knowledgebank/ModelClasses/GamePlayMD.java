package com.nameisknowledge.knowledgebank.ModelClasses;

import java.util.List;
import java.util.Map;

public class GamePlayMD {
    private Map<String,Integer> ids;
    private String winner;
    private int currentQuestion;
    private List<Integer> index;

    public GamePlayMD(Map<String, Integer> ids,List<Integer> index) {
        this.ids = ids;
        this.winner = "";
        this.currentQuestion = 0;
        this.index = index;
    }

    public GamePlayMD() {
    }

    public Map<String, Integer> getIds() {
        return ids;
    }

    public String getWinner() {
        return winner;
    }

    public List<Integer> getIndex() {
        return index;
    }

    public void setIds(Map<String, Integer> ids) {
        this.ids = ids;
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

    public void setIndex(List<Integer> index) {
        this.index = index;
    }
}
