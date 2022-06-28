package com.nameisknowledge.knowledgebank.ModelClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePlayMD implements Serializable {
    private String winner;
    private final List<PlayerMD> players = new ArrayList<>();
    private final Map<String,Integer> scores = new HashMap<>();
    private int currentQuestion;
    private List<EmitterQuestion> index;

    public GamePlayMD(List<EmitterQuestion> index,PlayerMD player,PlayerMD enemy) {
        this.index = index;
        this.players.add(player);
        this.players.add(enemy);
        this.scores.put(player.getPlayerName(),0);
        this.scores.put(enemy.getPlayerName(),0);
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

    public List<PlayerMD> getPlayers() {
        return players;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }
}
