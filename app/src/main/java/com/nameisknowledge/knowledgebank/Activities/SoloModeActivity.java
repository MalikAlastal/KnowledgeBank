package com.nameisknowledge.knowledgebank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.databinding.ActivitySoloModeBinding;

import java.util.ArrayList;
import java.util.List;

public class SoloModeActivity extends AppCompatActivity {
    private ActivitySoloModeBinding binding;
    private FirebaseFirestore fireStore;
    private List<QuestionMD> questions;
    private int index,score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoloModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // set values to global variables;
        initialValues();

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = binding.edAnswer.getText().toString();
                submit(answer,questions.get(index));
            }
        });

    }

    private void initialValues(){
        index = 0;
        score = 0;
        fireStore = FirebaseFirestore.getInstance();
        getQuestions(new GenericListener<List<QuestionMD>>() {
            @Override
            public void getData(List<QuestionMD> questionMDS) {
                questions = questionMDS;
                binding.tvQuestion.setText(questions.get(index).getQuestion());
            }
        });
    }

    private void getQuestions(GenericListener<List<QuestionMD>> genericListener){

        //تعريف مصفوفة فارغة لتخزين الاسئلة فيها
        List<QuestionMD> questions = new ArrayList<>();

        // لجلب البيانات من الفايرستو
        fireStore.collection("Questions")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        //لتخزين البيانات (الاسئلة) في المصفوفة
                        for (QueryDocumentSnapshot querySnapshot:queryDocumentSnapshots){
                            questions.add(querySnapshot.toObject(QuestionMD.class));
                        }

                        // بعد الانتهاء من العملية نمرر المصفوفة التي تحتوي على الاسئلة عن طريق الليسنر
                        genericListener.getData(questions);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SoloModeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }});
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
        Toast.makeText(this, "your score is: "+this.score+"/"+this.questions.size(), Toast.LENGTH_SHORT).show();
        finish();
    }
}