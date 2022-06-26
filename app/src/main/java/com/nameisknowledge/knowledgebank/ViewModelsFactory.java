package com.nameisknowledge.knowledgebank;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nameisknowledge.knowledgebank.Activities.renderGamePlay.RenderActivityViewModel;
import com.nameisknowledge.knowledgebank.Activities.duoMode.DuoActivityViewModel;
import com.nameisknowledge.knowledgebank.ModelClasses.ResponseMD;

public class ViewModelsFactory implements ViewModelProvider.Factory {
    private String roomID,senderName,senderId;

    public ViewModelsFactory(String roomID) {
        this.roomID = roomID;
    }

    public ViewModelsFactory(String senderName,String senderId) {
        this.senderName = senderName;
        this.senderId = senderId;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DuoActivityViewModel.class)){
            return (T) new DuoActivityViewModel(roomID);
        }else if (modelClass.isAssignableFrom(RenderActivityViewModel.class)){
            return (T) new RenderActivityViewModel(senderName,senderId);
        }
        return null;
    }
}
