package com.nameisknowledge.knowledgebank.ui.soloMode;


import com.nameisknowledge.knowledgebank.modelClasses.questions.FireBaseQuestionMD;
import com.nameisknowledge.knowledgebank.baseViewModels.BaseGameViewMode;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class SoloModeViewModel extends BaseGameViewMode<FireBaseQuestionMD> {
    private int answeredQuestionsCount;
    public boolean gameFinished;
    private final String userId;

    public SoloModeViewModel(String mode, String userId) {
        super(mode);
        this.userId = userId;
        getFireBaseRepository().generateRandomQuestionObservable().subscribe(generateRandomQuestionObserver());
    }

    @Override
    public void submitAnswer(String realAnswer, String input) {
        if (realAnswer.equals(input)){
            nextQuestion();
        }
    }

    @Override
    public void nextQuestion() {
        answeredQuestionsCount++;
        getFireBaseRepository().generateRandomQuestionObservable().subscribe(generateRandomQuestionObserver());
    }

    @Override
    public boolean isGameFinished() {
        return gameFinished;
    }

    @Override
    public void finishTheGame() {
        updateUserAttackPoints(userId);
        updateUserModeScore(userId,answeredQuestionsCount);
    }

    private SingleObserver<FireBaseQuestionMD> generateRandomQuestionObserver(){
        return new SingleObserver<FireBaseQuestionMD>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                getCompositeDisposable().add(d);
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

    @Override
    protected void onCleared() {
        super.onCleared();
        getCompositeDisposable().dispose();
    }
}
