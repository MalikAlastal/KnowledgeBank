package com.nameisknowledge.knowledgebank.modelClasses.gamePlay;

import com.nameisknowledge.knowledgebank.modelClasses.EmitterQuestion;
import com.nameisknowledge.knowledgebank.modelClasses.PlayerMD;

import java.io.Serializable;
import java.util.List;

public class DuoModeGamePlayMD extends GamePlay implements Serializable {
    private List<EmitterQuestion> index;

    public DuoModeGamePlayMD(List<EmitterQuestion> index, PlayerMD player, PlayerMD enemy) {
        super(player, enemy);
        this.index = index;
    }

    public DuoModeGamePlayMD() {

    }

    public List<EmitterQuestion> getIndex() {
        return index;
    }

    public void setIndex(List<EmitterQuestion> index) {
        this.index = index;
    }
}
