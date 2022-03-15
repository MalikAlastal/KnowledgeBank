package com.nameisknowledge.knowledgebank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.FileObserver;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.Methods.ToastMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.ActivityDuoModeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DuoModeActivity extends AppCompatActivity {
    private ActivityDuoModeBinding binding;
    private String roomId,senderId;
    private List<QuestionMD> questions;
    private int index,size,score;
    private ToastMethods toastMethods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDuoModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialValues();

        FirebaseFirestore.getInstance().collection(FirebaseConstants.GamePlay_COLLECTION).document(roomId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        getQuestionFromFireStore();
                        List<Integer> indexes = (List<Integer>) documentSnapshot.get("questionsIndex");
                        size = Objects.requireNonNull(indexes).size();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DuoModeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = binding.edAnswer.getText().toString();
                submit(answer,questions.get(index));
            }
        });
    }

    private void getQuestionFromFireStore(){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.Questions_COLLECTION)
                .document(index+"")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        questions.add(documentSnapshot.toObject(QuestionMD.class));
                        index++;
                        if (index<size){
                            getQuestionFromFireStore();
                        }else {
                            binding.tvQuestion.setText(questions.get(0).getQuestion());
                            index = 0;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DuoModeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialValues(){
        roomId = getIntent().getStringExtra("roomID");
        senderId = getIntent().getStringExtra("senderID");
        this.score = 0;
        this.index = 0;
        this.size = 0;
        this.questions = new ArrayList<>();
        this.toastMethods = new ToastMethods(this);
    }

    private void submit(String answer,QuestionMD questionMD){

        if (TextUtils.equals(answer,questionMD.getAnswer())){
            Toast.makeText(this, "Nice!!", Toast.LENGTH_SHORT).show();
            score++;
        }else {
            Toast.makeText(this, "Wrong Answer", Toast.LENGTH_SHORT).show();
        }

        if (this.index != this.questions.size()-1){
            nextQuestion();
        }else {
            endGame();
        }
    }

    private void nextQuestion(){
        this.index++;
        binding.tvQuestion.setText(questions.get(this.index).getQuestion());
    }

    private void endGame(){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GamePlay_COLLECTION).document(roomId)
                .update(FirebaseAuth.getInstance().getUid(),score)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseFirestore.getInstance().collection(FirebaseConstants.GamePlay_COLLECTION)
                                .document(roomId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        long me = (long) documentSnapshot.get(FirebaseAuth.getInstance().getUid());
                                        long otherPlayer = (long) documentSnapshot.get(senderId);
                                        long winner = Math.max(me,otherPlayer);
                                        toastMethods.success("The Winner score is"+winner);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                toastMethods.error(e.getMessage());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMethods.error(e.getMessage());
            }
        });
        finish();
    }

}