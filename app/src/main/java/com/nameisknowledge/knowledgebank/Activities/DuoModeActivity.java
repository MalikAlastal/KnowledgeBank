package com.nameisknowledge.knowledgebank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.databinding.ActivityDuoModeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DuoModeActivity extends AppCompatActivity {
    private ActivityDuoModeBinding binding;
    private String roomId;
    private List<QuestionMD> questions;
    private int index,size,score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDuoModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        roomId = getIntent().getStringExtra("roomID");

        initialValues();

        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION).document(roomId).get()
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
        FirebaseFirestore.getInstance().collection(FirebaseConstants.QUESTIONS_COLLECTION)
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
        this.score = 0;
        this.index = 0;
        this.size = 0;
        this.questions = new ArrayList<>();
    }

    private void submit(String answer,QuestionMD questionMD){

        if (TextUtils.equals(answer,questionMD.getAnswer())){
            Toast.makeText(this, "Nice!!", Toast.LENGTH_SHORT).show();

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
        //Toast.makeText(this, "your score is: "+this.score+"/"+this.questions.size(), Toast.LENGTH_SHORT).show();
        finish();
    }

}