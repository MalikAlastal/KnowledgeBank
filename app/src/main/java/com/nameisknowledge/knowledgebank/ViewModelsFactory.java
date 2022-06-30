package com.nameisknowledge.knowledgebank;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nameisknowledge.knowledgebank.modelClasses.MapAreaMD;
import com.nameisknowledge.knowledgebank.ui.attackArea.AttackAreaViewModel;
import com.nameisknowledge.knowledgebank.ui.renderGamePlay.RenderGamePlayViewModel;
import com.nameisknowledge.knowledgebank.ui.duoMode.DuoModeViewModel;
import com.nameisknowledge.knowledgebank.ui.questionsMode.QuestionsModeGameViewModel;
import com.nameisknowledge.knowledgebank.ui.soloMode.SoloModeViewModel;

public class ViewModelsFactory implements ViewModelProvider.Factory {
    private String roomID,senderName,senderId,mode,gamePlayCollection,soloModeUserId;
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

    public ViewModelsFactory(String soloModeUserId) {
        this.soloModeUserId = soloModeUserId;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DuoModeViewModel.class)){
            return (T) new DuoModeViewModel(mode,roomID,gamePlayCollection);
        }else if (modelClass.isAssignableFrom(RenderGamePlayViewModel.class)){
            return (T) new RenderGamePlayViewModel(senderName,senderId,mode);
        }else if (modelClass.isAssignableFrom(AttackAreaViewModel.class)){
            return (T) new AttackAreaViewModel(mapArea);
        }else if (modelClass.isAssignableFrom(QuestionsModeGameViewModel.class)){
            return (T) new QuestionsModeGameViewModel(mode,roomID,gamePlayCollection);
        }else if (modelClass.isAssignableFrom(SoloModeViewModel.class)){
            return (T) new SoloModeViewModel(mode,soloModeUserId);
        }
        return null;
    }
}
