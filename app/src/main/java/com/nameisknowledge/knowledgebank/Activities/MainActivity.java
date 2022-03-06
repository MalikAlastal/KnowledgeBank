package com.nameisknowledge.knowledgebank.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.TestDialog;
import com.nameisknowledge.knowledgebank.databinding.ActivityMainBinding;

import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;

        binding.clickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            }
        });

        FirebaseFirestore.getInstance().collection("Requests").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection("container").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null){
                    if (value!=null){
                        if (value.getDocumentChanges().size() == 1 && (value.getDocumentChanges().get(0).getType()!= DocumentChange.Type.MODIFIED || value.getDocumentChanges().get(0).getType()!= DocumentChange.Type.REMOVED)){
                            Log.d("docId",value.getDocumentChanges().get(0).getDocument().getId());
                            TestDialog newFragment = TestDialog.newInstance(value.getDocumentChanges().get(0).getDocument().getReference(),value.getDocumentChanges().get(0).getDocument().getString("ReqUid"));
                            newFragment.show(getSupportFragmentManager(), "missiles");
                        }
                    }
                }else {
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseFirestore.getInstance().collection("Response").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection("container").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null){
                    if (value!=null){
                        if (value.getDocumentChanges().size() == 1 && value.getDocumentChanges().get(0).getType()!= DocumentChange.Type.REMOVED){
                            startActivity(new Intent(MainActivity.this,GameActivity.class).putExtra("RoomId",value.getDocumentChanges().get(0).getDocument().getString("RoomId")));
                        }
                    }
                }else {
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    
}