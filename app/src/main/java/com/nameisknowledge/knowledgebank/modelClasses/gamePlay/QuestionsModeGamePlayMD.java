package com.nameisknowledge.knowledgebank.modelClasses.gamePlay;

import com.nameisknowledge.knowledgebank.modelClasses.PlayerMD;
import com.nameisknowledge.knowledgebank.modelClasses.questions.FireBaseQuestionMD;
import com.nameisknowledge.knowledgebank.modelClasses.questions.LocalQuestionMD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsModeGamePlayMD extends GamePlay {
    private int isQuestionsAdded;
    private final Map<String,List<LocalQuestionMD>> questions = new HashMap<>();

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

    public Map<String, List<LocalQuestionMD>> getQuestions() {
        return questions;
    }
}
