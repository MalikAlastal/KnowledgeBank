package com.nameisknowledge.knowledgebank.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.nameisknowledge.knowledgebank.Adapters.TestRvAdapter;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Dialogs.WinnerDialog;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.Methods.ToastMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.GamePlayMD;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.ModelClasses.TestRvMD;
import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.databinding.ActivityDuoModeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DuoModeActivity extends AppCompatActivity {
    private ActivityDuoModeBinding binding;
    private String roomId,senderId;
    private UserMD me,otherPlayer;
    private List<QuestionMD> questions;
    private int index;
    private ToastMethods toastMethods;
    private TestRvAdapter adapter1,adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDuoModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialValues();

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = binding.edAnswer.getText().toString();
                submit(answer);
            }
        });
    }

    private void initialValues(){
        roomId = getIntent().getStringExtra("roomID");
        senderId = getIntent().getStringExtra("senderID");
        this.index = 0;
        this.questions = new ArrayList<>();
        this.toastMethods = new ToastMethods(this);

        getUserFromFireStore(senderId, new GenericListener<UserMD>() {
            @Override
            public void getData(UserMD userMD) {
                otherPlayer = userMD;
            }
        });

        getUserFromFireStore(FirebaseAuth.getInstance().getUid(), new GenericListener<UserMD>() {
            @Override
            public void getData(UserMD userMD) {
                me = userMD;
            }
        });

        getGamePlayData(new GenericListener<Integer>() {
            @Override
            public void getData(Integer integer) {
                getQuestionFromFireStore(integer, new GenericListener<QuestionMD>() {
                    @Override
                    public void getData(QuestionMD questionMD) {
                        binding.tvQuestion.setText(questions.get(index).getQuestion());
                        binding.rv.setHasFixedSize(true);
                        binding.rv.setLayoutManager(new GridLayoutManager(getApplicationContext(),5));
                        binding.ansRv.setHasFixedSize(true);
                        binding.ansRv.setLayoutManager(new GridLayoutManager(getApplicationContext(),5));
                        adapter = new TestRvAdapter("", new GenericListener<TestRvMD>() {
                            @Override
                            public void getData(TestRvMD s) {
                                adapter1.setChar(s);
                            }
                        },2);
                        adapter1 = new TestRvAdapter(checkAnswerLength(questionMD.getAnswer()), new GenericListener<TestRvMD>() {
                            @Override
                            public void getData(TestRvMD s) {
                                adapter.addChar(s);
                                submit(getString(adapter.getMyList()));
                            }
                        },1);
                        binding.ansRv.setAdapter(adapter);
                        binding.rv.setAdapter(adapter1);
                    }
                });
            }
        });

        gameFlow();
    }

    private void submit(String answer){
        if (TextUtils.equals(answer,questions.get(index).getAnswer())){
            toastMethods.success("Nice!!");
            setTheScore();
            currentQuestion(2, new GenericListener<Void>() {
                @Override
                public void getData(Void unused) {
                }
            });
        }
    }

    private void clearAdapters(){
        adapter.clearArray();
        adapter1.clearArray();
        adapter1.setMyList(adapter1.cutString(checkAnswerLength(questions.get(index).getAnswer()).toCharArray()));
        toastMethods.success(checkAnswerLength(questions.get(index).getAnswer())+"::"+index);
    }

    private String getString(List<TestRvMD> list){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0;i<list.size();i++) {
            stringBuilder.append(list.get(i).getLetter());
        }
        return stringBuilder.toString();
    }


    private void nextQuestion(){
        this.index++;
        binding.tvQuestion.setText(questions.get(this.index).getQuestion());
    }

    private void getQuestionFromFireStore(int size,GenericListener<QuestionMD> listener){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.QUESTIONS_COLLECTION)
                .document(index+"")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        questions.add(documentSnapshot.toObject(QuestionMD.class));
                        index++;
                        if (index<size){
                            getQuestionFromFireStore(size,listener);
                        }else {
                            index = 0;
                            listener.getData(questions.get(index));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMethods.error(e.getMessage());
            }
        });
    }

    private void getUserFromFireStore(String id, GenericListener<UserMD> listener){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.USERS_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        listener.getData(documentSnapshot.toObject(UserMD.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMethods.error(e.getMessage());
            }
        });
    }


    private void endGame(){
        checkTheWinner(new GenericListener<String>() {
            @Override
            public void getData(String s) {
                WinnerDialog winnerDialog = WinnerDialog.newInstance(s);
                winnerDialog.show(getSupportFragmentManager(),"Winner Dialog");
            }
        });
    }

    private void setTheWinner(String winner,GenericListener<Void> listener){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(roomId)
                .update("Winner",winner)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.getData(unused);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMethods.error(e.getMessage());
            }
        });
    }

    private void getTheWinner(GenericListener<String> listener){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION).
                document(roomId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        listener.getData(documentSnapshot.getString("Winner"));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMethods.error(e.getMessage());
            }
        });
    }

    private void setTheScore(){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(roomId)
                .update("ids"+"."+FirebaseAuth.getInstance().getUid(),FieldValue.increment(1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        toastMethods.success("Done!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastMethods.error(e.getMessage());
                    }
                });
    }

    private void gameFlow(){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(roomId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        GamePlayMD gamePlayMD = Objects.requireNonNull(value).toObject(GamePlayMD.class);
                        if (Objects.requireNonNull(gamePlayMD).getCurrentQuestion() == 2){
                            if (index != questions.size()-1){
                                currentQuestion(0, new GenericListener<Void>() {
                                    @Override
                                    public void getData(Void unused) {
                                        nextQuestion();
                                        clearAdapters();
                                    }
                                });
                            }else {
                                endGame();
                            }
                        }
                    }
                });
    }

    private void checkTheWinner(GenericListener<String> listener){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(roomId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        GamePlayMD gamePlayMD = documentSnapshot.toObject(GamePlayMD.class);
                        long myScore = (long) gamePlayMD.getIds().get(FirebaseAuth.getInstance().getUid());
                        long otherPlayerScore = (long) gamePlayMD.getIds().get(senderId);
                        long winner = Math.max(myScore,otherPlayerScore);
                        String win = "";

                        if (winner == myScore){
                            win = me.getUsername();
                        }else {
                            win = otherPlayer.getUsername();
                        }

                        setTheWinner(win, new GenericListener<Void>() {
                            @Override
                            public void getData(Void unused) {
                                getTheWinner(new GenericListener<String>() {
                                    @Override
                                    public void getData(String s) {
                                        listener.getData(s);
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMethods.error(e.getMessage());
            }
        });
    }

    private void currentQuestion(int number,GenericListener<Void> listener){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(roomId)
                .update("currentQuestion",number)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.getData(unused);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastMethods.error(e.getMessage());
                    }
                });
    }

    private void getGamePlayData(GenericListener<Integer> listener){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION).document(roomId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<Integer> indexes = (List<Integer>) documentSnapshot.get("index");
                        listener.getData(indexes.size());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMethods.error(e.getMessage());
            }
        });
    }

    private String checkAnswerLength(String answer){
        StringBuilder fina = new StringBuilder();
        fina.append(answer);
        for (int i=answer.length();i<10;i++){
            fina.append("a");
        }
        return fina.toString();
    }
}