package com.nameisknowledge.knowledgebank.ui;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nameisknowledge.knowledgebank.constants.UserConstants;
import com.nameisknowledge.knowledgebank.FireBaseRepository;
import com.nameisknowledge.knowledgebank.modelClasses.gamePlay.GamePlay;
import com.nameisknowledge.knowledgebank.modelClasses.PlayerMD;
import com.nameisknowledge.knowledgebank.modelClasses.questions.FireBaseQuestionMD;
import com.nameisknowledge.knowledgebank.MyApplication;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public abstract class GamePlayViewModel extends ViewModel {
    private PlayerMD player;
    private final String mode;
    private PlayerMD enemy;
    private final String roomID,gamePlayCollection;
    private final FireBaseRepository fireBaseRepository;
    public MutableLiveData<FireBaseQuestionMD> question = new MutableLiveData<>();
    public MutableLiveData<PlayerMD> winner = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public MutableLiveData<String> playerScore = new MutableLiveData<>();
    public MutableLiveData<String> enemyScore = new MutableLiveData<>();
    public MutableLiveData<String> enemyName = new MutableLiveData<>();
    public MutableLiveData<String> playerName = new MutableLiveData<>();

    public GamePlayViewModel(String roomID,String gamePlayCollection,String mode) {
        this.roomID = roomID;
        this.mode = mode;
        this.gamePlayCollection  = gamePlayCollection;
        this.fireBaseRepository = FireBaseRepository.getInstance();
    }

    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    abstract public void submitAnswer(String realAnswer, String input);
    abstract public void setTheWinner(PlayerMD winner);
    abstract public SingleObserver<GamePlay> getGamePlayObserver();
    abstract public SingleObserver<PlayerMD> finishTheGameObserver();
    abstract public Observable<String> gameFlowObservable(String gamePlayCollection);
    abstract public Observer<String> gameFlowObserver();


    public String getMode() {
        return mode;
    }

    public String getGamePlayCollection(){
        return gamePlayCollection;
    }

    public PlayerMD getPlayer() {
        return player;
    }

    public PlayerMD getEnemy() {
        return enemy;
    }

    public String getRoomID() {
        return roomID;
    }

    public FireBaseRepository getFireBaseRepository() {
        return fireBaseRepository;
    }

    public void setPlayers(List<PlayerMD> players){
        String playerName = UserConstants.getCurrentUser(MyApplication.getContext()).getUsername();
        if (players.get(0).getPlayerName().equals(playerName)){
            player = players.get(0);
            enemy = players.get(1);
        }else {
            player = players.get(1);
            enemy = players.get(0);
        }
    }
}
