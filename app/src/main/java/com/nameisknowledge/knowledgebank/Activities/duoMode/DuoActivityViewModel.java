package com.nameisknowledge.knowledgebank.Activities.duoMode;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.nameisknowledge.knowledgebank.Constants.UserConstants;
import com.nameisknowledge.knowledgebank.FireBaseRepository;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;

import com.nameisknowledge.knowledgebank.ModelClasses.GamePlayMD;
import com.nameisknowledge.knowledgebank.ModelClasses.PlayerMD;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.MyApplication;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DuoActivityViewModel extends ViewModel {
    private final String[] letters = {
            "أ", "ب", "ت", "ث", "ج", "ح","خ", "د","ذ","ر","ز","س","ش", "ص", "ض", "ط","ظ","ع", "غ","ف", "ق","م","ل","ك","ن", "ه","و","ي"
    };
    private GamePlayMD gamePlayMD;
    private PlayerMD player;
    private PlayerMD enemy;
    private final String roomID;
    private final FireBaseRepository fireBaseRepository;
    private ListenerRegistration gameFlowListenerRegistration;
    private int questionIndex = 0;
    private int setScoreIndex;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Disposable initialCompositeDisposable;
    public MutableLiveData<String> question = new MutableLiveData<>();
    public MutableLiveData<String> winnerName = new MutableLiveData<>();
    public MutableLiveData<String> emptyAnswer = new MutableLiveData<>();
    public MutableLiveData<String> longAnswer = new MutableLiveData<>();
    public MutableLiveData<String> realAnswer = new MutableLiveData<>();

    public DuoActivityViewModel(String roomID) {
        this.roomID = roomID;
        this.fireBaseRepository = FireBaseRepository.getInstance();
        fireBaseRepository.getGamePlayObservable(roomID).subscribe(getGamePlayObserver());
    }

    public void submit(String answer,String input){
        if (answer.equals(input)){
            fireBaseRepository.setTheScore(player.getPlayerName(),roomID);
            fireBaseRepository.questionAnsweredObservable(2,roomID).subscribe();
        }
    }

    public void setTheWinner(String name){
        fireBaseRepository.setTheWinner(name,roomID);
    }

    public void getTheQuestionByIndex(int index) {
        fireBaseRepository
                .getQuestionObservable(gamePlayMD.getIndex().get(index))
                .subscribe(getQuestionObserver());
    }

//    private SingleObserver<List<EmitterQuestion>> getQuestionsIndexesObserver() {
//        return new SingleObserver<List<EmitterQuestion>>() {
//            @Override
//            public void onSubscribe(@NonNull Disposable d) {
//                initialCompositeDisposable = d;
//            }
//
//            @Override
//            public void onSuccess(@NonNull List<EmitterQuestion> questions) {
//                emitterQuestions = questions;
//                getTheQuestionByIndex(questionIndex);
//                gameFlowObservable().subscribe(gameFlowObserver());
//                initialCompositeDisposable.dispose();
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//                //
//            }
//        };
//    }

    private SingleObserver<GamePlayMD> getGamePlayObserver(){
        return new SingleObserver<GamePlayMD>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                initialCompositeDisposable = d;
            }

            @Override
            public void onSuccess(@NonNull GamePlayMD gamePlay) {
                gamePlayMD = gamePlay;
                setPlayers(gamePlayMD.getPlayers());
                getTheQuestionByIndex(questionIndex);
                gameFlowObservable().subscribe(gameFlowObserver());
                initialCompositeDisposable.dispose();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }
        };
    }

    private SingleObserver<String> endTheGameObserver(){
        return new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull String s) {
                winnerName.setValue(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        };
    }

    private SingleObserver<QuestionMD> getQuestionObserver() {
        return new SingleObserver<QuestionMD>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull QuestionMD questionMD) {
                question.setValue(questionMD.getQuestion());
                realAnswer.setValue(clearAnswerSpaces(questionMD.getAnswer()));
                longAnswer.setValue(makeAnswerLonger(clearAnswerSpaces(questionMD.getAnswer())));
                emptyAnswer.setValue(makeStringEmpty(clearAnswerSpaces(questionMD.getAnswer())));
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        };
    }

    public Observer<DocumentReference> gameFlowObserver(){
        return new Observer<DocumentReference>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@NonNull DocumentReference documentReference) {
                compositeDisposable.add(
                fireBaseRepository.questionAnsweredObservable(0,roomID)
                        .subscribe(()->{
                            questionIndex++;
                            getTheQuestionByIndex(questionIndex);
                        }));
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                fireBaseRepository.endTheGameObservable(player.getPlayerName(),enemy.getPlayerName(),roomID).subscribe(endTheGameObserver());
            }
        };
    }

    private Observable<DocumentReference> gameFlowObservable(){
        return Observable.create((ObservableOnSubscribe<DocumentReference>) emitter->{
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION).document(roomID);
            gameFlowListenerRegistration = documentReference
                    .addSnapshotListener(((value, error) -> {
                        assert value != null;
                        GamePlayMD gamePlayMD = value.toObject(GamePlayMD.class);
                        if (Objects.requireNonNull(gamePlayMD).getCurrentQuestion() == 2){
                            if (questionIndex != gamePlayMD.getIndex().size()-1){
                                emitter.onNext(documentReference);
                            }else {
                                emitter.onComplete();
                            }
                        }
                    }));
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }

    public String makeStringEmpty(String s) {
        char[] array = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            array[i] = ' ';
        }
        return String.valueOf(array);
    }

    private String clearAnswerSpaces(String answer) {
        StringBuilder formatAnswer = new StringBuilder();
        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) != ' ') {
                formatAnswer.append(answer.charAt(i));
            }
        }
        return formatAnswer.toString();
    }

    private String randomTheAnswer(String string) {
        char[] array = string.toCharArray();
        for (int i = 0; i < array.length; i++) {
            int randomIndexToSwap = new Random().nextInt(array.length);
            char temp = array[randomIndexToSwap];
            array[randomIndexToSwap] = array[i];
            array[i] = temp;
        }
        return String.valueOf(array);
    }

    private String makeAnswerLonger(String answer) {
        StringBuilder finalAnswer = new StringBuilder();
        finalAnswer.append(answer);
        for (int i = answer.length(); i < answer.length() + 4; i++) {
            finalAnswer.append(letters[new Random().nextInt(((letters.length - 1)) + 1)]);
        }
        return randomTheAnswer(finalAnswer.toString());
    }

    private void setPlayers(List<PlayerMD> players){
        String playerName = UserConstants.getCurrentUser(MyApplication.getContext()).getUsername();
        if (players.get(0).getPlayerName().equals(playerName)){
            setScoreIndex = 0;
            player = players.get(0);
            enemy = players.get(1);
        }else {
            setScoreIndex = 1;
            player = players.get(1);
            enemy = players.get(0);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        gameFlowListenerRegistration.remove();
        compositeDisposable.dispose();
    }

}
