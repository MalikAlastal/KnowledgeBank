package com.nameisknowledge.knowledgebank;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nameisknowledge.knowledgebank.databinding.ActivityMainBinding;
import com.nameisknowledge.knowledgebank.databinding.DialogAddQuestionBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        FirebaseFirestore.getInstance().collection("Requests").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null){
                    if (value!=null && value.exists()){
                        Log.d("MyID","My Id"+FirebaseAuth.getInstance().getUid());
                        Log.d("test","ReqName :"+value.getString("ReqName")+" | "+"RequestId :"+value.getString("ReqId")+" | "+"ResponseId :"+value.getString("ResId"));
                    }
                }else {
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // to get count of documents if collection
        FirebaseFirestore.getInstance().collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //Toast.makeText(MainActivity.this,task.getResult().size()+"", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //add question
        binding.show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "My Id"+FirebaseAuth.getInstance().getUid(), Toast.LENGTH_SHORT).show();
//                AddQuestionDialog changePasswordDialog = AddQuestionDialog.newInstance();
//                changePasswordDialog.show(MainActivity.this.getSupportFragmentManager(), null);
            }
        });


        binding.showUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,UsersActivity.class));
            }
        });


        // listener of changes in collection or document
        FirebaseFirestore.getInstance().collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null){
                    assert value != null;
                    Toast.makeText(MainActivity.this, value.getString("fullName"), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        // update data in document if fireStore
        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()).update(
                        "fullName",binding.edt.getText().toString()
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this,"Done!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}