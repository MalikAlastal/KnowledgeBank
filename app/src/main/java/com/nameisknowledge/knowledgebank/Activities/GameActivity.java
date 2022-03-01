package com.nameisknowledge.knowledgebank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.ActivityGameBinding;

import java.util.Objects;

public class GameActivity extends AppCompatActivity {
    ActivityGameBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("GamePlay").document(getIntent().getStringExtra("RoomId"));

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                int[] data = (int[]) Objects.requireNonNull(documentSnapshot.getData()).get("questionsIndex");
                binding.txvQuestion.setText(String.valueOf(Objects.requireNonNull(data)[0]));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        
    }
}