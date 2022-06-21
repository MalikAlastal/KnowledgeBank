package com.nameisknowledge.knowledgebank.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.recyclerview.widget.GridLayoutManager;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.Adapters.TestRvAdapter;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Dialogs.WinnerDialog;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.Methods.ToastMethods;
import com.nameisknowledge.knowledgebank.Methods.ViewMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.EmitterQuestion;
import com.nameisknowledge.knowledgebank.ModelClasses.GamePlayMD;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.ModelClasses.TestRvMD;
import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.databinding.ActivityDuoModeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DuoModeActivity extends AppCompatActivity {
    private final String TAG = "DuoModeActivity";
    public final String[] letters = {
            "أ", "ب", "ت", "ث", "ج", "ح","خ", "د","ذ","ر","ز","س","ش", "ص", "ض", "ط","ظ","ع", "غ","ف", "ق","م","ل","ك","ن", "ه","و","ي"
    };
    private ActivityDuoModeBinding binding;
    private String roomId,senderId;
    private UserMD me,otherPlayer;
    private List<QuestionMD> questions;
    private int index;
    private ToastMethods toastMethods;
    private TestRvAdapter answerAdapter,inputAdapter;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

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
        roomId = getIntent().getStringExtra("roomID");
        senderId = getIntent().getStringExtra("senderID");
        this.index = 0;
        this.questions = new ArrayList<>();
        this.toastMethods = new ToastMethods(this);

        getUserFromFireStore(senderId, userMD -> otherPlayer = userMD);

        getUserFromFireStore(FirebaseAuth.getInstance().getUid(), userMD -> me = userMD);

        compositeDisposable.add(
                // first : get gamePlay data from fireStore by gamePlay document name (roomId)
                getGamePlayData()
                        .subscribe(gamePlayMD ->{
                            compositeDisposable.add(
                                // here we got gamePlay object
                                getQuestionsIndexes(gamePlayMD)
                                        /*
                                        passing gamePlay object to "getQuestionsByIndexes" method to get questions indexes,
                                        this method "getQuestionsByIndexes" get questions indexes from gamePlay object
                                        then pass it index by index to "getQuestion" method
                                         */
                                        .subscribe(emitterQuestion -> {
                                            compositeDisposable.add(
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

        gameFlow();
    }
    private void submit(String answer) {
        if (TextUtils.equals(answer, questions.get(index).getAnswer())) {
            toastMethods.success("Nice!!");
            setTheScore();
            currentQuestion(2, unused -> {
            });
        }
    }

    private void setUpRv(QuestionMD questionMD){
        binding.tvQuestion.setText(questions.get(index).getQuestion());
        binding.rvAnswer.setHasFixedSize(true);
        binding.rvAnswer.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        binding.rvInput.setHasFixedSize(true);
        binding.rvInput.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));

        answerAdapter = new TestRvAdapter(makeStringEmpty(questionMD.getAnswer()), false, testRvMD -> {
            if (testRvMD.getLetter() != ' '){
                inputAdapter.setChar(testRvMD);
            }
        });

        inputAdapter = new TestRvAdapter(checkAnswerLength(questionMD.getAnswer()), true, testRvMD -> answerAdapter.checkEmpty(list -> {
            if (list.size() != 0) {
                inputAdapter.setEmpty(testRvMD.getIndex(), testRvMD);
                answerAdapter.addChar(testRvMD);
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

    private String getString(List<TestRvMD> list) {
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

    private void getUserFromFireStore(String id, GenericListener<UserMD> listener) {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.USERS_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> listener.getData(documentSnapshot.toObject(UserMD.class)))
                .addOnFailureListener(e -> toastMethods.error(e.getMessage()));
    }

    private void endGame() {
        checkTheWinner(s -> {
            WinnerDialog winnerDialog = WinnerDialog.newInstance(s);
            winnerDialog.show(getSupportFragmentManager(), "Winner Dialog");
        });
    }

    private void setTheWinner(String winner, GenericListener<Void> listener) {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(roomId)
                .update("winner", winner)
                .addOnSuccessListener(listener::getData)
                .addOnFailureListener(e -> toastMethods.error(e.getMessage()));
    }

    private void getTheWinner(GenericListener<String> listener) {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION).
                document(roomId)
                .get()
                .addOnSuccessListener(documentSnapshot -> listener.getData(documentSnapshot.getString("winner")))
                .addOnFailureListener(e -> toastMethods.error(e.getMessage()));
    }

    private void setTheScore() {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(roomId)
                .update("ids" + "." + FirebaseAuth.getInstance().getUid(), FieldValue.increment(10))
                .addOnSuccessListener(unused -> toastMethods.success("Done!"))
                .addOnFailureListener(e -> toastMethods.error(e.getMessage()));
    }

    private void getPlayersProgress(){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(roomId)
                .get()
                .addOnSuccessListener(d->{
                    GamePlayMD gamePlayMD = d.toObject(GamePlayMD.class);
                    long playerScore = (long) gamePlayMD.getIds().get(FirebaseAuth.getInstance().getUid());
                    long enemyScore = (long) gamePlayMD.getIds().get(senderId);
                    changeProgress(binding.ownerProgressLine, (int) playerScore);
                    changeProgress(binding.enemyProgressLine, (int) enemyScore);
                })
                .addOnFailureListener(e->toastMethods.error(e.getMessage()));
    }

    private void gameFlow() {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(roomId)
                .addSnapshotListener((value, error) -> {
                    GamePlayMD gamePlayMD = Objects.requireNonNull(value).toObject(GamePlayMD.class);
                    if (Objects.requireNonNull(gamePlayMD).getCurrentQuestion() == 2) {
                        if (index != questions.size() - 1) {
                            currentQuestion(0, unused -> {
                                getPlayersProgress();
                                nextQuestion();
                                clearAdapters();
                            });
                        } else {
                            endGame();
                        }
                    }
                });
    }

    private void checkTheWinner(GenericListener<String> listener) {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(roomId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    GamePlayMD gamePlayMD = documentSnapshot.toObject(GamePlayMD.class);
                    long myScore = (long) gamePlayMD.getIds().get(FirebaseAuth.getInstance().getUid());
                    long otherPlayerScore = (long) gamePlayMD.getIds().get(senderId);
                    long winner = Math.max(myScore, otherPlayerScore);
                    String win;

                    if (winner == myScore) {
                        win = me.getUsername();
                    } else {
                        win = otherPlayer.getUsername();
                    }

                    setTheWinner(win, unused -> getTheWinner(listener));
                }).addOnFailureListener(e -> toastMethods.error(e.getMessage()));
    }

    private void currentQuestion(int number, GenericListener<Void> listener) {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(roomId)
                .update("currentQuestion", number)
                .addOnSuccessListener(listener::getData)
                .addOnFailureListener(e -> toastMethods.error(e.getMessage()));
    }
    private Observable<GamePlayMD> getGamePlayData() {
        return Observable.create((ObservableOnSubscribe<GamePlayMD>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .document(roomId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        GamePlayMD gamePlayMD = documentSnapshot.toObject(GamePlayMD.class);
                        emitter.onNext(gamePlayMD);
                    }).addOnFailureListener(e -> Log.d(TAG,e.getMessage()));
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
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

    private void changeProgress(NumberProgressBar progressBar, int targetProgress){
        ProgressHandler handler  = new ProgressHandler();

        int currentProgress = progressBar.getProgress() ;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (targetProgress>=currentProgress){
                    for (int i = currentProgress  ; i<=targetProgress ; i++){
                        try {

                            Message message = new Message() ;
                            message.what = i;
                            message.obj = progressBar;
                            handler.sendMessage(message);
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    for (int i = currentProgress  ; i>=targetProgress ; i--){
                        try {
                            handler.sendEmptyMessage(i);
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public class ProgressHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            NumberProgressBar bar = (NumberProgressBar) msg.obj;
            bar.setProgress(msg.what);
        }
    }

}