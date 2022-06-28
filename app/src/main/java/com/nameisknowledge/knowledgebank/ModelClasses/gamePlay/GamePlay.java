package com.nameisknowledge.knowledgebank.ModelClasses.gamePlay;

import com.nameisknowledge.knowledgebank.ModelClasses.PlayerMD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class GamePlay {
    private String winner;
    private final List<PlayerMD> players = new ArrayList<>();
    private final Map<String,Integer> scores = new HashMap<>();
    private int currentQuestion;

    public GamePlay(PlayerMD player,PlayerMD enemy) {
        this.players.add(player);
        this.players.add(enemy);
        this.scores.put(player.getPlayerName(),0);
        this.scores.put(enemy.getPlayerName(),0);
        this.winner = "";
        this.currentQuestion = 0;
    }

    public GamePlay() {
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public List<PlayerMD> getPlayers() {
        return players;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }
}
