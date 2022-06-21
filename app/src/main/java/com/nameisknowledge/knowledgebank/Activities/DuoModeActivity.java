package com.nameisknowledge.knowledgebank.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.nameisknowledge.knowledgebank.Adapters.GamePlayAdapter;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Dialogs.WinnerDialog;
import com.nameisknowledge.knowledgebank.Methods.ToastMethods;
import com.nameisknowledge.knowledgebank.Methods.ViewMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.EmitterQuestion;
import com.nameisknowledge.knowledgebank.ModelClasses.GamePlayMD;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.ModelClasses.ResponseMD;
import com.nameisknowledge.knowledgebank.ModelClasses.InputsMD;
import com.nameisknowledge.knowledgebank.databinding.ActivityDuoModeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DuoModeActivity extends AppCompatActivity {
    private final String TAG = "DuoModeActivity";
    private final String[] letters = {
            "أ", "ب", "ت", "ث", "ج", "ح","خ", "د","ذ","ر","ز","س","ش", "ص", "ض", "ط","ظ","ع", "غ","ف", "ق","م","ل","ك","ن", "ه","و","ي"
    };
    private ActivityDuoModeBinding binding;
    private ResponseMD responseMD;
    private String playerName, enemyName;
    private List<QuestionMD> questions;
    private ListenerRegistration gameFlowListener;
    private int index;
    private ToastMethods toastMethods;
    private GamePlayAdapter answerAdapter,inputAdapter;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private CompositeDisposable generateGamePlayCompositeDisposable = new CompositeDisposable();
    private final CompositeDisposable progressDisposable = new CompositeDisposable();
    private Disposable enemyNameDisposable;
    private Disposable playerNameDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDuoModeBinding.inflate(getLayoutInflater());
        ViewMethods.setLocale(this , "ar");
        setContentView(binding.getRoot());
        initialValues();
    }

    private void initialValues(){

        binding.ownerProgressLine.setProgress(0);
        binding.enemyProgressLine.setProgress(0);
        responseMD = (ResponseMD) getIntent().getSerializableExtra("responseMD");
        this.index = 0;
        this.questions = new ArrayList<>();
        this.toastMethods = new ToastMethods(this);

        getUserNameFromFireStore(responseMD.getUserID())
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        enemyNameDisposable = d;
                    }
                    @Override
                    public void onSuccess(@NonNull String name) {
                        enemyName = name;
                        enemyNameDisposable.dispose();
                        enemyNameDisposable = null;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        toastMethods.error(e.getMessage());
                    }
                });

        getUserNameFromFireStore(FirebaseAuth.getInstance().getUid())
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        playerNameDisposable = d;
                    }
                    @Override
                    public void onSuccess(@NonNull String name) {
                        playerName = name;
                        playerNameDisposable.dispose();
                        playerNameDisposable = null;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        toastMethods.error(e.getMessage());
                    }
                });

        generateGamePlayCompositeDisposable.add(
                // first : get gamePlay data from fireStore by gamePlay document name (roomId)
                    getGamePlayData()
                        .subscribe(gamePlayMD ->{
                            generateGamePlayCompositeDisposable.add(
                                // here we got gamePlay object
                                getQuestionsIndexes(gamePlayMD)
                                        /*
                                        passing gamePlay object to "getQuestionsByIndexes" method to get questions indexes,
                                        this method "getQuestionsByIndexes" get questions indexes from gamePlay object
                                        then pass it index by index to "getQuestion" method
                                         */
                                        .subscribe(emitterQuestion -> {
                                            generateGamePlayCompositeDisposable.add(
                                                    // this method take question index then get Question object from the fireStore
                                                    getQuestion(emitterQuestion)
                                                            //here we got all the questions that we need
                                                            .doOnComplete(() -> {
                                                                QuestionMD questionMD = questions.get(index);
                                                                setUpRv(questionMD);
                                                            })
                                                            // add the question to the questionsList
                                                            .subscribe(questions::add)
                                            );
                                        })
                        );
                        })
        );

        compositeDisposable.add(
          gameFlow()
          .doOnComplete(this::endGame)
          .subscribe(gamePlayMD -> {
              compositeDisposable.add(
                currentQuestion(0)
                      .subscribe(()->{
                          getPlayersProgress();
                          nextQuestion();
                          clearAdapters();
                      })
              );
          }));
    }

    private void submit(String answer) {
        if (TextUtils.equals(answer, questions.get(index).getAnswer())) {
            toastMethods.success("Nice!!");
            setTheScore();
            currentQuestion(2).subscribe();
        }
    }

    private void setUpRv(QuestionMD questionMD){
        // after we got all the data we need for gamePlay we dispose there observable
        generateGamePlayCompositeDisposable.dispose();
        generateGamePlayCompositeDisposable = null;
        //
        binding.tvQuestion.setText(questions.get(index).getQuestion());
        binding.rvAnswer.setHasFixedSize(true);
        binding.rvAnswer.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        binding.rvInput.setHasFixedSize(true);
        binding.rvInput.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));

        answerAdapter = new GamePlayAdapter(makeStringEmpty(questionMD.getAnswer()), false, inputsMD -> {
            if (inputsMD.getLetter() != ' '){
                inputAdapter.setChar(inputsMD);
            }
        });

        inputAdapter = new GamePlayAdapter(checkAnswerLength(questionMD.getAnswer()), true, inputsMD -> answerAdapter.checkEmpty(list -> {
            if (list.size() != 0) {
                inputAdapter.setEmpty(inputsMD.getIndex(), inputsMD);
                answerAdapter.addChar(inputsMD);
            }
            submit(getString(answerAdapter.getMyList()));
        }));

        binding.rvAnswer.setAdapter(answerAdapter);
        binding.rvInput.setAdapter(inputAdapter);
    }

    private String makeStringEmpty(String s) {
        char[] array = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            array[i] = ' ';
        }
        return String.valueOf(array);
    }

    private void clearAdapters() {
        answerAdapter.clearArray();
        inputAdapter.clearArray();
        answerAdapter.setMyList(answerAdapter.cutString(makeStringEmpty(questions.get(index).getAnswer()).toCharArray()));
        inputAdapter.setMyList(inputAdapter.cutString(checkAnswerLength(questions.get(index).getAnswer()).toCharArray()));
    }

    private String getString(List<InputsMD> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i).getLetter());
        }
        return stringBuilder.toString();
    }

    private void nextQuestion() {
        this.index++;
        binding.tvQuestion.setText(questions.get(this.index).getQuestion());
    }

    private Single<String> getUserNameFromFireStore(String id) {
        return Single.create((SingleOnSubscribe<String>) emitter->{
            FirebaseFirestore.getInstance().collection(FirebaseConstants.USERS_COLLECTION)
                    .document(id)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        emitter.onSuccess(documentSnapshot.getString("username"));
                    })
                    .addOnFailureListener(e -> toastMethods.error(e.getMessage()));
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private void endGame() {
        compositeDisposable.add(
        checkTheWinner()
                .subscribe(winner->{
                   setTheWinner(winner);
                    WinnerDialog winnerDialog = WinnerDialog.newInstance(winner);
                    winnerDialog.show(getSupportFragmentManager(), "Winner Dialog");
                }));
    }

    private void setTheWinner(String winner) {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(responseMD.getRoomID())
                .update("winner", winner)
                .addOnFailureListener(e -> toastMethods.error(e.getMessage()));
    }

    private void setTheScore() {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(responseMD.getRoomID())
                .update("ids" + "." + FirebaseAuth.getInstance().getUid(), FieldValue.increment(10))
                .addOnSuccessListener(unused -> toastMethods.success("Done!"))
                .addOnFailureListener(e -> toastMethods.error(e.getMessage()));
    }

    private void getPlayersProgress(){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(responseMD.getRoomID())
                .get()
                .addOnSuccessListener(d->{
                    GamePlayMD gamePlayMD = d.toObject(GamePlayMD.class);
                    long playerScore = (long) gamePlayMD.getIds().get(FirebaseAuth.getInstance().getUid());
                    long enemyScore = (long) gamePlayMD.getIds().get(responseMD.getUserID());
                    changeProgress(binding.ownerProgressLine, (int) playerScore);
                    changeProgress(binding.enemyProgressLine, (int) enemyScore);
                }).addOnFailureListener(e->toastMethods.error(e.getMessage()));
    }

    private Observable<GamePlayMD> gameFlow() {
        return Observable.create((ObservableOnSubscribe<GamePlayMD>) emitter->{
            DocumentReference responseRef = FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .document(responseMD.getRoomID());
            gameFlowListener = responseRef.addSnapshotListener((value, error) -> {
                GamePlayMD gamePlayMD = Objects.requireNonNull(value).toObject(GamePlayMD.class);
                if (Objects.requireNonNull(gamePlayMD).getCurrentQuestion() == 2){
                    if (index != questions.size() -1){
                        emitter.onNext(gamePlayMD);
                    }else {
                        emitter.onComplete();
                    }
                }
            });
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private Single<String> checkTheWinner() {
        return Single.create((SingleOnSubscribe<String>) emitter -> {
            FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .document(responseMD.getRoomID())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        GamePlayMD gamePlayMD = documentSnapshot.toObject(GamePlayMD.class);
                        long playerScore = (long) gamePlayMD.getIds().get(FirebaseAuth.getInstance().getUid());
                        long enemyScore = (long) gamePlayMD.getIds().get(responseMD.getUserID());
                        long winner = Math.max(playerScore, enemyScore);
                        String win;

                        if (winner == playerScore) {
                            win = playerName;
                        } else {
                            win = enemyName;
                        }
                        emitter.onSuccess(win);

                    }).addOnFailureListener(e -> toastMethods.error(e.getMessage()));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Completable currentQuestion(int number) {
        return Completable.create(emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .document(responseMD.getRoomID())
                    .update("currentQuestion",number)
                    .addOnSuccessListener(unused -> emitter.onComplete())
                    .addOnFailureListener(e -> toastMethods.error(e.getMessage()));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<GamePlayMD> getGamePlayData() {
        return Single.create((SingleOnSubscribe<GamePlayMD>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .document(responseMD.getRoomID())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> emitter.onSuccess(documentSnapshot.toObject(GamePlayMD.class)))
                    .addOnFailureListener(e -> Log.d(TAG,e.getMessage()));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    private Observable<EmitterQuestion> getQuestionsIndexes(GamePlayMD gamePlayMD){
        return Observable.fromIterable(Objects.requireNonNull(gamePlayMD).getIndex())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<QuestionMD> getQuestion(EmitterQuestion item){
        return Observable.create((ObservableOnSubscribe<QuestionMD>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.QUESTIONS_COLLECTION)
                    .document(item.getTag())
                    .collection(FirebaseConstants.QUESTIONS_CONTAINER)
                    .document(String.valueOf(item.getIndex()))
                    .get()
                    .addOnSuccessListener(documentSnapshot1 -> {
                        QuestionMD questionMD = documentSnapshot1.toObject(QuestionMD.class);
                        Objects.requireNonNull(questionMD).setAnswer(clearAnswerSpaces(questionMD.getAnswer()));
                        emitter.onNext(questionMD);
                        if (questions.size() == 9) emitter.onComplete();
                    }).addOnFailureListener(e-> Log.d(TAG,e.getMessage()));
        }).subscribeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    }

    private String checkAnswerLength(String answer) {
        StringBuilder finalAnswer = new StringBuilder();
        finalAnswer.append(answer);
        for (int i = answer.length(); i < answer.length() + 4; i++) {
            finalAnswer.append(letters[new Random().nextInt(((letters.length - 1)) + 1)]);
        }
        return randomTheAnswer(finalAnswer.toString());
    }

    private String clearAnswerSpaces(String answer){
        StringBuilder formatAnswer = new StringBuilder();
        for (int i = 0 ; i<answer.length() ; i++) {
            if (answer.charAt(i)!=' '){
                formatAnswer.append(answer.charAt(i));
            }
        }
        return formatAnswer.toString() ;
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

    private void changeProgress(NumberProgressBar progressBar,int target){
        Observer<Long> observer = new Observer<Long>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                progressDisposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Long aLong) {
                progressBar.setProgress(Integer.parseInt(String.valueOf(aLong)));
                if (aLong == target) progressDisposable.clear();
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.d(TAG,e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"onComplete");
            }
        };

        Observable<Long> observable = Observable.interval(20,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose(()->{
                    // mission completes here
                    Log.d(TAG,"doOnDispose") ;
                });

        observable.subscribe(observer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.gameFlowListener.remove();
        this.compositeDisposable.dispose();
        this.progressDisposable.dispose();
    }
}