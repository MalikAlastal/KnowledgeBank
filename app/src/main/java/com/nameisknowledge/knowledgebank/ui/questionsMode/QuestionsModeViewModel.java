package com.nameisknowledge.knowledgebank.ui.questionsMode;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.nameisknowledge.knowledgebank.ui.GamePlayViewModel;
import com.nameisknowledge.knowledgebank.modelClasses.PlayerMD;
import com.nameisknowledge.knowledgebank.modelClasses.gamePlay.GamePlay;
import com.nameisknowledge.knowledgebank.modelClasses.questions.FireBaseQuestionMD;
import com.nameisknowledge.knowledgebank.modelClasses.gamePlay.QuestionsModeGamePlayMD;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QuestionsModeViewModel extends GamePlayViewModel {
    private QuestionsModeGamePlayMD gamePlay;
    private List<FireBaseQuestionMD> questions;
    private ListenerRegistration registration;
    private int questionIndex;
    private ListenerRegistration gameFlowListenerRegistration;
    private Disposable gamePlayDisposable;

    public QuestionsModeViewModel(String roomID,String gamePlayCollection,String mode) {
        super(roomID,gamePlayCollection,mode);
    }

    public void start(){
        getFireBaseRepository().getGamePlayObservable(getRoomID(),getGamePlayCollection()).subscribe(getGamePlayObserver());
    }

    @Override
    public void submitAnswer(String realAnswer, String input) {
        if (realAnswer.equals(input)){
            getFireBaseRepository().setTheScore(getPlayer().getPlayerName(),getRoomID(),getGamePlayCollection());
            getFireBaseRepository().questionAnsweredObservable(2,getRoomID(),getGamePlayCollection()).subscribe();
        }
    }

    @Override
    public void setTheWinner(PlayerMD winner) {
        if (winner.getPlayerName().equals(getPlayer().getPlayerName())){
            getFireBaseRepository().setFinalPlayerScore(getMode(),winner.getPlayerID());
        }
        getFireBaseRepository().setTheWinner(winner.getPlayerName(),getRoomID(),getGamePlayCollection());
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
                gamePlay = (QuestionsModeGamePlayMD) game;
                setPlayers(gamePlay.getPlayers());
                questions = gamePlay.getQuestions().get(getEnemy().getPlayerID());
                setDataForActivity(Objects.requireNonNull(questions).get(questionIndex));
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
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection(gamePlayCollection).document(getRoomID());
            gameFlowListenerRegistration = documentReference
                    .addSnapshotListener(((value, error) -> {
                        assert value != null;
                        QuestionsModeGamePlayMD gamePlay = value.toObject(QuestionsModeGamePlayMD.class);
                        playerScore.setValue(String.valueOf(gamePlay.getScores().get(getPlayer().getPlayerName())));
                        enemyScore.setValue(String.valueOf(gamePlay.getScores().get(getEnemy().getPlayerName())));

                        if (Objects.requireNonNull(gamePlay).getCurrentQuestion() == 2){
                            if (questionIndex != questions.size()-1){
                                emitter.onNext("l");
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
                getFireBaseRepository().questionAnsweredObservable(0,getRoomID(),getGamePlayCollection())
                        .subscribe(()->{
                            questionIndex++;
                            setDataForActivity(questions.get(questionIndex));
                        }));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }

            @Override
            public void onComplete() {
                getFireBaseRepository().finishTheGameObservable(getPlayer(),getEnemy(),getRoomID(),getGamePlayCollection()).subscribe(finishTheGameObserver());
            }
        };
    }

    private void setDataForActivity(FireBaseQuestionMD fireBaseQuestionMD){
        question.setValue(fireBaseQuestionMD);
        playerName.setValue(getPlayer().getPlayerName());
        enemyName.setValue(getEnemy().getPlayerName());
    }

    public Completable isQuestionsAdded(){
        return Completable.create(emitter -> {
            DocumentReference documentReference = FirebaseFirestore.getInstance()
                    .collection(getGamePlayCollection())
                    .document(getRoomID());
             registration = documentReference.addSnapshotListener((value, error) -> {
                assert value != null;
                QuestionsModeGamePlayMD gamePlay = value.toObject(QuestionsModeGamePlayMD.class);
                if (Objects.requireNonNull(gamePlay).getIsQuestionsAdded() == 2){
                    emitter.onComplete();
                }
            });
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        registration.remove();
        gameFlowListenerRegistration.remove();
        getCompositeDisposable().dispose();
    }
}
