package com.nameisknowledge.knowledgebank;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nameisknowledge.knowledgebank.modelClasses.MapAreaMD;
import com.nameisknowledge.knowledgebank.ui.attackArea.AttackAreaViewModel;
import com.nameisknowledge.knowledgebank.ui.questionsMode.QuestionsModeViewModel;
import com.nameisknowledge.knowledgebank.ui.renderGamePlay.RenderGamePlayViewModel;
import com.nameisknowledge.knowledgebank.ui.duoMode.DuoModeViewModel;
import com.nameisknowledge.knowledgebank.modelClasses.questions.MapFireBaseQuestionMD;

import java.util.List;

public class ViewModelsFactory implements ViewModelProvider.Factory {
    private String roomID,senderName,senderId,mode,gamePlayCollection;
    private MapAreaMD mapArea;


    public ViewModelsFactory(String roomID,String gamePlayCollection) {
        this.roomID = roomID;
        this.gamePlayCollection = gamePlayCollection;
    }

    public ViewModelsFactory(String senderName,String senderId,String mode) {
        this.senderName = senderName;
        this.mode = mode;
        this.senderId = senderId;
    }

    public ViewModelsFactory(MapAreaMD mapArea) {
        this.mapArea = mapArea;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DuoModeViewModel.class)){
            return (T) new DuoModeViewModel(roomID,gamePlayCollection,mode);
        }else if (modelClass.isAssignableFrom(RenderGamePlayViewModel.class)){
            return (T) new RenderGamePlayViewModel(senderName,senderId,mode);
        }else if (modelClass.isAssignableFrom(QuestionsModeViewModel.class)){
            return (T) new QuestionsModeViewModel(roomID,gamePlayCollection,mode);
        }else if (modelClass.isAssignableFrom(AttackAreaViewModel.class)){
            return (T) new AttackAreaViewModel(mapArea);
        }
        return null;
    }
}
