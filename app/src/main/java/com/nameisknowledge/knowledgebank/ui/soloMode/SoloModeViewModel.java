package com.nameisknowledge.knowledgebank.ui.soloMode;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nameisknowledge.knowledgebank.FireBaseRepository;
import com.nameisknowledge.knowledgebank.modelClasses.UserMD;
import com.nameisknowledge.knowledgebank.modelClasses.questions.FireBaseQuestionMD;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class SoloModeViewModel extends ViewModel {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final FireBaseRepository fireBaseRepository;
    public final MutableLiveData<FireBaseQuestionMD> question = new MutableLiveData<>();
    public final MutableLiveData<Integer> userPoints = new MutableLiveData<>();
    public final MutableLiveData<UserMD> updatedUser = new MutableLiveData<>();
    private int points;


    public SoloModeViewModel() {
        this.fireBaseRepository = FireBaseRepository.getInstance();
        fireBaseRepository.generateRandomQuestionObservable().subscribe(generateRandomQuestionObserver());
    }

    public void submit(String answer,String input){
        if (answer.equals(input)){
            fireBaseRepository.generateRandomQuestionObservable().subscribe(generateRandomQuestionObserver());
        }
    }

    public void updatePoints(String type){
        if (points!=0){
            switch (type){
                case "hint":
                    points = points-5;
                    userPoints.setValue(points);
                    break;
                case "showChar":
                    points = points-2;
                    userPoints.setValue(points);
                    break;
                case "delete":
                    points = points-1;
                    userPoints.setValue(points);
                    break;
            }
        }else {
            userPoints.setValue(points);
        }
    }

    public void setThePoints(int points){
        this.points =+ points;
        userPoints.setValue(points);
    }

    public void finishTheGame(String id){
        fireBaseRepository.updateAreaAttackPointsV2(id,points).subscribe(updateAreaAttackPointsObserver());
    }

    private SingleObserver<FireBaseQuestionMD> generateRandomQuestionObserver(){
        return new SingleObserver<FireBaseQuestionMD>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull FireBaseQuestionMD fireBaseQuestionMD) {
                question.setValue(fireBaseQuestionMD);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }
        };
    }

    private SingleObserver<UserMD> updateAreaAttackPointsObserver(){
        return new SingleObserver<UserMD>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull UserMD userMD) {
                updatedUser.setValue(userMD);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }
        };
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
