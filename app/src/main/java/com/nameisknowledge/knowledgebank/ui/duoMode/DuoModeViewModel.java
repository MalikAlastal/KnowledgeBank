package com.nameisknowledge.knowledgebank.ui.duoMode;

import com.nameisknowledge.knowledgebank.modelClasses.EmitterQuestion;
import com.nameisknowledge.knowledgebank.modelClasses.gamePlay.DuoModeGamePlayMD;
import com.nameisknowledge.knowledgebank.modelClasses.gamePlay.GamePlay;
import com.nameisknowledge.knowledgebank.modelClasses.questions.FireBaseQuestionMD;
import com.nameisknowledge.knowledgebank.baseViewModels.MultiPlayersGameViewModel;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class DuoModeViewModel extends MultiPlayersGameViewModel<FireBaseQuestionMD,EmitterQuestion> {

    public DuoModeViewModel(String mode, String roomID, String gamePlayCollection) {
        super(mode, roomID, gamePlayCollection);
        getFireBaseRepository().getGamePlayObservable(roomID, gamePlayCollection).subscribe(getGamePlayObserver());
    }

    @Override
    public void nextQuestion() {
        questionIndex++;
        getFireBaseRepository()
                .getQuestionObservable(questions.get(questionIndex))
                .subscribe(getQuestionObserver());
    }


    public SingleObserver<FireBaseQuestionMD> getQuestionObserver() {
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
    public SingleObserver<GamePlay> getGamePlayObserver() {
        return new SingleObserver<GamePlay>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                gamePlayDisposable = d;
            }

            @Override
            public void onSuccess(@NonNull GamePlay game) {
                DuoModeGamePlayMD gamePlay = (DuoModeGamePlayMD) game;
                questions = gamePlay.getIndex();
                setPlayers(gamePlay.getPlayers());
                getFireBaseRepository().getQuestionObservable(questions.get(questionIndex)).subscribe(getQuestionObserver());
                gameFlowObservable(getGamePlayCollection()).subscribe(gameFlowObserver());
                gamePlayDisposable.dispose();
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
        winnerListenerRegistration.remove();
        gameFlowListenerRegistration.remove();
        getCompositeDisposable().dispose();
    }
}