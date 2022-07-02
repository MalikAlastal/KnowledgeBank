package com.nameisknowledge.knowledgebank.ui.duoMode;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.nameisknowledge.knowledgebank.ui.GamePlayViewModel;
import com.nameisknowledge.knowledgebank.modelClasses.PlayerMD;
import com.nameisknowledge.knowledgebank.modelClasses.gamePlay.DuoModeGamePlayMD;
import com.nameisknowledge.knowledgebank.modelClasses.gamePlay.GamePlay;
import com.nameisknowledge.knowledgebank.modelClasses.questions.FireBaseQuestionMD;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DuoModeViewModel extends GamePlayViewModel {
    private DuoModeGamePlayMD duoModeGamePlayMD;
    private int questionIndex;
    private ListenerRegistration gameFlowListenerRegistration;
    private Disposable gamePlayDisposable;

    public DuoModeViewModel(String roomID, String gamePlayCollection,String mode) {
        super(roomID, gamePlayCollection,mode);
        getFireBaseRepository().getGamePlayObservable(roomID, gamePlayCollection).subscribe(getGamePlayObserver());
    }

    @Override
    public void submitAnswer(String realAnswer, String input) {
        if (realAnswer.equals(input)) {
            getFireBaseRepository().setTheScore(getPlayer().getPlayerName(), getRoomID(), getGamePlayCollection());
            getFireBaseRepository().questionAnsweredObservable(2, getRoomID(), getGamePlayCollection()).subscribe();
        }

    }

    @Override
    public void setTheWinner(PlayerMD winner) {
        if (winner.getPlayerName().equals(getPlayer().getPlayerName())){
            getFireBaseRepository().setFinalPlayerScore(getMode(),winner.getPlayerID());
        }
        getFireBaseRepository().setTheWinner(winner.getPlayerName(), getRoomID(),getGamePlayCollection());
    }

    public void getTheQuestionByIndex(int index) {
        getFireBaseRepository()
                .getQuestionObservable(duoModeGamePlayMD.getIndex().get(index))
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
                playerName.setValue(getPlayer().getPlayerName());
                enemyName.setValue(getEnemy().getPlayerName());
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
            public void onSuccess(@NonNull GamePlay gamePlay) {
                duoModeGamePlayMD = (DuoModeGamePlayMD) gamePlay;
                setPlayers(duoModeGamePlayMD.getPlayers());
                getTheQuestionByIndex(questionIndex);
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
    public SingleObserver<PlayerMD> finishTheGameObserver() {
        return new SingleObserver<PlayerMD>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                getCompositeDisposable().add(d);
            }

            @Override
            public void onSuccess(@NonNull PlayerMD mWinner) {
                winner.setValue(mWinner);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }
        };
    }

    @Override
    public Observable<String> gameFlowObservable(String gamePlayCollection) {
        return Observable.create((ObservableOnSubscribe<String>) emitter->{
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection(getGamePlayCollection()).document(getRoomID());
            gameFlowListenerRegistration = documentReference
                    .addSnapshotListener(((value, error) -> {
                        assert value != null;
                        DuoModeGamePlayMD duoModeGamePlayMD = value.toObject(DuoModeGamePlayMD.class);
                        playerScore.setValue(String.valueOf(duoModeGamePlayMD.getScores().get(getPlayer().getPlayerName())));
                        enemyScore.setValue(String.valueOf(duoModeGamePlayMD.getScores().get(getEnemy().getPlayerName())));
                        if (Objects.requireNonNull(duoModeGamePlayMD).getCurrentQuestion() == 2){
                            if (questionIndex != duoModeGamePlayMD.getIndex().size()-1){
                                emitter.onNext("null");
                            }else {
                                emitter.onComplete();
                            }
                        }
                    }));
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }

    @Override
    public Observer<String> gameFlowObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                getCompositeDisposable().add(d);
            }

            @Override
            public void onNext(@NonNull String ss) {
                getCompositeDisposable().add(
                        getFireBaseRepository().questionAnsweredObservable(0, getRoomID(), getGamePlayCollection())
                                .subscribe(() -> {
                                    questionIndex++;
                                    getTheQuestionByIndex(questionIndex);
                                }));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }

            @Override
            public void onComplete() {
                getFireBaseRepository().finishTheGameObservable(getPlayer(), getEnemy(), getRoomID(), getGamePlayCollection()).subscribe(finishTheGameObserver());
            }
        };
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        gameFlowListenerRegistration.remove();
        getCompositeDisposable().dispose();
    }
}
