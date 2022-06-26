package com.nameisknowledge.knowledgebank.ModelClasses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePlayMD {
    private String winner;
    private Map<String,Integer> players;
    private int currentQuestion;
    private List<EmitterQuestion> index;

    public GamePlayMD(List<EmitterQuestion> index,String player,String enemy) {
        this.players = new HashMap<>();
        this.players.put(player,0);
        this.players.put(enemy,0);
        this.index = index;
    }

    public GamePlayMD() {
    }

    public String getWinner() {
        return winner;
    }

    public List<EmitterQuestion> getIndex() {
        return index;
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

    public void setIndex(List<EmitterQuestion> index) {
        this.index = index;
    }

    public Map<String, Integer> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, Integer> players) {
        this.players = players;
    }
}
