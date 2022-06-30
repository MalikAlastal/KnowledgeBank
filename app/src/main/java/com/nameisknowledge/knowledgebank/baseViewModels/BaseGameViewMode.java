package com.nameisknowledge.knowledgebank.baseViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nameisknowledge.knowledgebank.FireBaseRepository;

import io.reactivex.rxjava3.disposables.CompositeDisposable;


public abstract class BaseGameViewMode<T> extends ViewModel {
    private int points;
    private final String mode;
    private final FireBaseRepository fireBaseRepository;
    public final MutableLiveData<T> question = new MutableLiveData<>();
    public final MutableLiveData<Integer> userPoints = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public BaseGameViewMode(String mode) {
        this.mode = mode;
        fireBaseRepository = FireBaseRepository.getInstance();
    }

    abstract public void submitAnswer(String realAnswer, String input);

    abstract public void nextQuestion();

    abstract public boolean isGameFinished();

    abstract public void finishTheGame();

    public void updateUserModeScore(String id,int points) {
        getFireBaseRepository().setFinalPlayerScore(getMode(),id,points);
    }

    public void updateUserAttackPoints(String id) {
        getFireBaseRepository().updateAreaAttackPoints(id,getPoints()).subscribe();
    }

    public FireBaseRepository getFireBaseRepository() {
        return fireBaseRepository;
    }

    public int getPoints() {
        return points;
    }

    public String getMode() {
        return mode;
    }


    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    public void updatePoints(String type){
        if (points!=0){
            switch (type){
                case "hint":
                    points = points-5;
                    break;
                case "showChar":
                    points = points-2;
                    break;
                case "delete":
                    points = points-1;
                    break;
            }
        }
        userPoints.setValue(points);
    }

    public void setPoints(int points){
        this.points =+ points;
        userPoints.setValue(points);
    }

}
