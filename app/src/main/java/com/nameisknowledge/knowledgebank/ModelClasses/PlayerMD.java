package com.nameisknowledge.knowledgebank.ModelClasses;

import java.io.Serializable;

public class PlayerMD implements Serializable {
    private String playerName,playerID;

    public PlayerMD(String playerName, String playerID) {
        this.playerName = playerName;
        this.playerID = playerID;
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
}
