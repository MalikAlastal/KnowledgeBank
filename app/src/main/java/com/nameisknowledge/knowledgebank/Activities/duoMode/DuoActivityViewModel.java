package com.nameisknowledge.knowledgebank.Activities.duoMode;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.FireBaseRepository;
import com.nameisknowledge.knowledgebank.ModelClasses.EmitterQuestion;

import com.nameisknowledge.knowledgebank.ModelClasses.GamePlayMD;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.ModelClasses.ResponseMD;

import java.util.ArrayList;
import java.util.List;
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
    private List<EmitterQuestion> emitterQuestions = new ArrayList<>();
    private final String playerName;
    private final ResponseMD responseMD;
    private final FireBaseRepository fireBaseRepository;
    private ListenerRegistration gameFlowListenerRegistration;
    private int questionIndex = 0;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Disposable initialCompositeDisposable;
    public MutableLiveData<String> question = new MutableLiveData<>();
    public MutableLiveData<String> winnerName = new MutableLiveData<>();
    public MutableLiveData<String> emptyAnswer = new MutableLiveData<>();
    public MutableLiveData<String> longAnswer = new MutableLiveData<>();
    public MutableLiveData<String> realAnswer = new MutableLiveData<>();

    public DuoActivityViewModel(ResponseMD responseMD,String playerName) {
        this.responseMD = responseMD;
        this.playerName = playerName;
        this.fireBaseRepository = FireBaseRepository.getInstance(responseMD);
        fireBaseRepository.getQuestionsIndexesObservable().subscribe(getQuestionsIndexesObserver());
    }

    public void submit(String answer,String input){
        if (answer.equals(input)){
            fireBaseRepository.setTheScore(playerName);
            fireBaseRepository.questionAnsweredObservable(2).subscribe();
        }
    }

    public void setTheWinner(String name){
        fireBaseRepository.setTheWinner(name);
    }

    public void getTheQuestionByIndex(int index) {
        fireBaseRepository
                .getQuestionObservable(emitterQuestions.get(index))
                .subscribe(getQuestionObserver());
    }

    private SingleObserver<List<EmitterQuestion>> getQuestionsIndexesObserver() {
        return new SingleObserver<List<EmitterQuestion>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                initialCompositeDisposable = d;
            }

            @Override
            public void onSuccess(@NonNull List<EmitterQuestion> questions) {
                emitterQuestions = questions;
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
                fireBaseRepository.questionAnsweredObservable(0)
                        .subscribe(()->{
                            questionIndex++;
                            Log.d("abood","index "+questionIndex);
                            Log.d("abood","index at array "+emitterQuestions.get(questionIndex).getIndex());
                            Log.d("abood","tag at array "+emitterQuestions.get(questionIndex).getTag());
                            Log.d("abood","indexes size "+emitterQuestions.size());
                            getTheQuestionByIndex(questionIndex);
                        }));
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                fireBaseRepository.endTheGameObservable().subscribe(endTheGameObserver());
            }
        };
    }

    private Observable<DocumentReference> gameFlowObservable(){
        return Observable.create((ObservableOnSubscribe<DocumentReference>) emitter->{
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION).document(responseMD.getRoomID());
            gameFlowListenerRegistration = documentReference
                    .addSnapshotListener(((value, error) -> {
                        assert value != null;
                        GamePlayMD gamePlayMD = value.toObject(GamePlayMD.class);
                        if (Objects.requireNonNull(gamePlayMD).getCurrentQuestion() == 2){
                            if (questionIndex != emitterQuestions.size()-1){
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

    @Override
    protected void onCleared() {
        super.onCleared();
        gameFlowListenerRegistration.remove();
        compositeDisposable.dispose();
    }
}
