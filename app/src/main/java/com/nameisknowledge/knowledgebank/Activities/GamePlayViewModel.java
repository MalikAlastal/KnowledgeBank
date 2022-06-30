package com.nameisknowledge.knowledgebank.Activities;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nameisknowledge.knowledgebank.Constants.UserConstants;
import com.nameisknowledge.knowledgebank.FireBaseRepository;
import com.nameisknowledge.knowledgebank.ModelClasses.gamePlay.GamePlay;
import com.nameisknowledge.knowledgebank.ModelClasses.PlayerMD;
import com.nameisknowledge.knowledgebank.MyApplication;

import java.util.List;
import java.util.Map;
import java.util.Random;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public abstract class GamePlayViewModel extends ViewModel {
    private final String[] letters = {
            "أ", "ب", "ت", "ث", "ج", "ح","خ", "د","ذ","ر","ز","س","ش", "ص", "ض", "ط","ظ","ع", "غ","ف", "ق","م","ل","ك","ن", "ه","و","ي"
    };
    private PlayerMD player;
    private final String mode;
    private PlayerMD enemy;
    private final String roomID,gamePlayCollection;
    private final FireBaseRepository fireBaseRepository;
    public MutableLiveData<String> question = new MutableLiveData<>();
    public MutableLiveData<String> winnerName = new MutableLiveData<>();
    public MutableLiveData<String> emptyAnswer = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public MutableLiveData<String> longAnswer = new MutableLiveData<>();
    public MutableLiveData<String> realAnswer = new MutableLiveData<>();
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
    abstract public void setTheWinner(String winnerName);
    abstract public SingleObserver<GamePlay> getGamePlayObserver();
    abstract public SingleObserver<String> finishTheGameObserver();
    abstract public Observable<String> gameFlowObservable(String gamePlayCollection);
    abstract public Observer<String> gameFlowObserver();


    public void setEmptyAnswer(String answer){
        emptyAnswer.setValue(makeStringEmpty(clearAnswerSpaces(answer)));
    }

    public String getMode() {
        return mode;
    }

    public String getGamePlayCollection(){
        return gamePlayCollection;
    }

    public void setLongAnswer(String answer){
        longAnswer.setValue(makeAnswerLonger(clearAnswerSpaces(answer)));
    }

    public void setRealAnswer(String answer){
        realAnswer.setValue(clearAnswerSpaces(answer));
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

    public String makeStringEmpty(String s) {
        char[] array = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            array[i] = ' ';
        }
        return String.valueOf(array);
    }

    public String clearAnswerSpaces(String answer) {
        StringBuilder formatAnswer = new StringBuilder();
        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) != ' ') {
                formatAnswer.append(answer.charAt(i));
            }
        }
        return formatAnswer.toString();
    }

    public String randomTheAnswer(String string) {
        char[] array = string.toCharArray();
        for (int i = 0; i < array.length; i++) {
            int randomIndexToSwap = new Random().nextInt(array.length);
            char temp = array[randomIndexToSwap];
            array[randomIndexToSwap] = array[i];
            array[i] = temp;
        }
        return String.valueOf(array);
    }

    public String makeAnswerLonger(String answer) {
        StringBuilder finalAnswer = new StringBuilder();
        finalAnswer.append(answer);
        for (int i = answer.length(); i < answer.length() + 4; i++) {
            finalAnswer.append(letters[new Random().nextInt(((letters.length - 1)) + 1)]);
        }
        return randomTheAnswer(finalAnswer.toString());
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
