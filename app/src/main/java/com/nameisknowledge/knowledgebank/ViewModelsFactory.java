package com.nameisknowledge.knowledgebank;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nameisknowledge.knowledgebank.Activities.duoMode.DuoActivityViewModel;
import com.nameisknowledge.knowledgebank.ModelClasses.ResponseMD;

public class ViewModelsFactory implements ViewModelProvider.Factory {
    private ResponseMD responseMD;
    private String playerName;
    public ViewModelsFactory(ResponseMD responseMD,String playerName) {
        this.responseMD = responseMD;
        this.playerName = playerName;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DuoActivityViewModel.class)){
            return (T) new DuoActivityViewModel(responseMD,playerName);
        }
        return null;
    }
}
