package com.nameisknowledge.knowledgebank.ModelClasses;

import java.io.Serializable;

public class PlayerMD implements Serializable {
    private String playerName,playerID;
    private int playerScore;

    public PlayerMD(String playerName, String playerID, int playerScore) {
        this.playerName = playerName;
        this.playerID = playerID;
        this.playerScore = playerScore;
    }

    public PlayerMD() {
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }
}
