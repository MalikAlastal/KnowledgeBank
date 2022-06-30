package com.nameisknowledge.knowledgebank.baseViewModels;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.nameisknowledge.knowledgebank.MyApplication;
import com.nameisknowledge.knowledgebank.constants.UserConstants;
import com.nameisknowledge.knowledgebank.modelClasses.PlayerMD;
import com.nameisknowledge.knowledgebank.modelClasses.gamePlay.GamePlay;
import com.nameisknowledge.knowledgebank.modelClasses.questions.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class MultiPlayersGameViewModel<T extends Question,E> extends BaseGameViewMode<T> {
    private final String roomID, gamePlayCollection;
    private PlayerMD player,enemy;
    public Disposable gamePlayDisposable;
    public int questionIndex;
    public List<E> questions = new ArrayList<>();
    public ListenerRegistration gameFlowListenerRegistration, winnerListenerRegistration, playerLeftBeforeStartRegistration;
    public final MutableLiveData<String> winner = new MutableLiveData<>();
    public final MutableLiveData<String> playerScore = new MutableLiveData<>();
    public final MutableLiveData<String> enemyScore = new MutableLiveData<>();
    public final MutableLiveData<String> playerLeftBeforeStart = new MutableLiveData<>();

    public MultiPlayersGameViewModel(String mode, String roomID, String gamePlayCollection) {
        super(mode);
        this.roomID = roomID;
        this.gamePlayCollection = gamePlayCollection;
        winnerObservable().subscribe(winnerObserver());
    }

    @Override
    public boolean isGameFinished() {
        return questionIndex == questions.size()-1;
    }

    public void setTheWinner(PlayerMD winner) {
        getFireBaseRepository().setTheWinner(winner, getRoomID(), getGamePlayCollection());
        updateUserAttackPoints(getPlayer().getPlayerID());
    }


    @Override
    public void submitAnswer(String realAnswer, String input) {
        if (realAnswer.equals(input)) {
            updateGamePlayScore();
            currentQuestionAnswered(2);
        }
    }

    abstract public SingleObserver<GamePlay> getGamePlayObserver();


    void currentQuestionAnswered(int num) {
        Completable completable = getFireBaseRepository()
                .currentQuestionAnsweredObservable(num, getRoomID(), getGamePlayCollection());
        if (num == 0) {
            completable.subscribe(currentQuestionAnsweredObserver());
        } else {
            completable.subscribe();
        }
    }


    void updateGamePlayScore() {
        getFireBaseRepository().updateUserScore(getPlayer().getPlayerName(), getRoomID(), getGamePlayCollection());
    }

    public CompletableObserver currentQuestionAnsweredObserver() {
        return new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                getCompositeDisposable().add(d);
            }

            @Override
            public void onComplete() {
                nextQuestion();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }
        };
    }

    public Observer<String> gameFlowObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                getCompositeDisposable().add(d);
            }

            @Override
            public void onNext(@NonNull String s) {
                currentQuestionAnswered(0);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }

            @Override
            public void onComplete() {
                gameFlowListenerRegistration.remove();
                finishTheGame();
            }
        };
    }

    @Override
    public void finishTheGame() {
        getFireBaseRepository()
                .finishTheGameObservable(getPlayer(),getEnemy(),getRoomID(),getGamePlayCollection())
                .subscribe(finishTheGameObserver());
    }

    public Observable<String> winnerObservable() {
        return Observable.create((ObservableOnSubscribe<String>) emitter -> {
            winnerListenerRegistration = FirebaseFirestore.getInstance()
                    .collection(getGamePlayCollection())
                    .document(getRoomID())
                    .addSnapshotListener((value, error) -> {
                        assert value != null;
                        emitter.onNext(value.getString("winner"));
                    });
        }).filter(s -> !s.isEmpty());
    }

    public Observer<String> winnerObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                getCompositeDisposable().add(d);
            }

            @Override
            public void onNext(@NonNull String mWinnerName) {
                if (getPlayer().getPlayerName().equals(mWinnerName)){
                    updateUserModeScore(getPlayer().getPlayerID(),1);
                }
                winner.setValue(mWinnerName);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }

            @Override
            public void onComplete() {
                //
            }
        };
    }

    public SingleObserver<PlayerMD> finishTheGameObserver() {
        return new SingleObserver<PlayerMD>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                getCompositeDisposable().add(d);
            }

            @Override
            public void onSuccess(@NonNull PlayerMD mWinner) {
                setTheWinner(mWinner);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }
        };
    }

    public Observable<String> gameFlowObservable(String gamePlayCollection) {
        return Observable.create((ObservableOnSubscribe<String>) emitter->{
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection(gamePlayCollection).document(getRoomID());
             gameFlowListenerRegistration = documentReference
                    .addSnapshotListener(((value, error) -> {
                        assert value != null;
                        GamePlay gamePlay = value.toObject(GamePlay.class);
                        playerScore.setValue(String.valueOf(gamePlay.getScores().get(getPlayer().getPlayerName())));
                        enemyScore.setValue(String.valueOf(gamePlay.getScores().get(getEnemy().getPlayerName())));
                        if (Objects.requireNonNull(gamePlay).getCurrentQuestion() == 2){
                            if (questionIndex != questions.size()-1){
                                emitter.onNext("");
                            }else {
                                emitter.onComplete();
                            }
                        }
                    }));
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public String getRoomID() {
        return roomID;
    }

    public String getGamePlayCollection() {
        return gamePlayCollection;
    }


    public PlayerMD getPlayer() {
        return player;
    }


    public PlayerMD getEnemy() {
        return enemy;
    }

    public void setPlayers(List<PlayerMD> players) {
        String playerName = UserConstants.getCurrentUser(MyApplication.getContext()).getUsername();
        if (players.get(0).getPlayerName().equals(playerName)) {
            player = players.get(0);
            enemy = players.get(1);
        } else {
            player = players.get(1);
            enemy = players.get(0);
        }
    }
}
