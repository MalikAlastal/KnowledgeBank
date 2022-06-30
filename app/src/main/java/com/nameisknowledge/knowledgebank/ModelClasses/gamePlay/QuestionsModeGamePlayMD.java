package com.nameisknowledge.knowledgebank.ModelClasses.gamePlay;

import com.nameisknowledge.knowledgebank.ModelClasses.PlayerMD;
import com.nameisknowledge.knowledgebank.ModelClasses.questions.FireBaseQuestionMD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsModeGamePlayMD extends GamePlay {
    private int isQuestionsAdded;
    private final Map<String,List<FireBaseQuestionMD>> questions = new HashMap<>();

    public QuestionsModeGamePlayMD(PlayerMD player, PlayerMD enemy) {
        super(player, enemy);
        this.questions.put(player.getPlayerID(),new ArrayList<>());
        this.questions.put(enemy.getPlayerID(),new ArrayList<>());
    }

    public QuestionsModeGamePlayMD() {
    }

    public int getIsQuestionsAdded() {
        return isQuestionsAdded;
    }

    public void setIsQuestionsAdded(int isQuestionsAdded) {
        this.isQuestionsAdded = isQuestionsAdded;
    }

    public Map<String, List<FireBaseQuestionMD>> getQuestions() {
        return questions;
    }
}
