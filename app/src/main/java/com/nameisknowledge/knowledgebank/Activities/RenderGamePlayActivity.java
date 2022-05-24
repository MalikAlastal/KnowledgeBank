package com.nameisknowledge.knowledgebank.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.ModelClasses.GamePlay2MD;
import com.nameisknowledge.knowledgebank.ModelClasses.GamePlayMD;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.ModelClasses.ResponseMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.Services.CounterService;
import com.nameisknowledge.knowledgebank.databinding.ActivityRenderGamePlayBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RenderGamePlayActivity extends AppCompatActivity {
    private ActivityRenderGamePlayBinding binding;
    private String senderID;
    private int resSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRenderGamePlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        senderID = getIntent().getStringExtra("senderId");

        if (senderID == null){
            responsesListener();
            CounterService.startAction(this, new GenericListener<Void>() {
                @Override
                public void getData(Void unused) {
                    Toast.makeText(RenderGamePlayActivity.this, "Time Out!!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        if (senderID!=null){
            generateQuestions(this, new GenericListener<GamePlayMD>() {
                @Override
                public void getData(GamePlayMD gamePlayMD) {
                    generateGamePlay(getApplicationContext(), gamePlayMD, new GenericListener<String>() {
                        @Override
                        public void getData(String s) {
                            generateResponse(getApplicationContext(),s);
                        }
                    });
                }
            });
        }
    }


    private void generateQuestions(Context context, GenericListener<GamePlayMD> listener){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.QUESTIONS_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int max = task.getResult().size();
                    List<Integer> questionsIndex = new ArrayList<>();

                    for (int i=0;i<max;i++){
                        questionsIndex.add(i);
                    }

                    Map<String,Integer> map = new HashMap<>();
                    map.put(FirebaseAuth.getInstance().getUid(),0);
                    map.put(senderID,0);

                    listener.getData(new GamePlayMD(map,questionsIndex));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateGamePlay(Context context, GamePlayMD gamePlayMD, GenericListener<String> listener){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION).add(gamePlayMD).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                listener.getData(documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateResponse(Context context,String roomID){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.RESPONSES_COLLECTION).document(senderID)
                .collection(FirebaseConstants.CONTAINER_COLLECTION)
                .add(new ResponseMD(roomID,FirebaseAuth.getInstance().getUid())).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        context.startActivity(new Intent(context, DuoModeActivity.class).
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra("roomID",documentSnapshot.getString("roomID"))
                                .putExtra("senderID",senderID));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Error",e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void responsesListener(){
        FirebaseFirestore.getInstance().collection(FirebaseConstants.RESPONSES_COLLECTION)
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(FirebaseConstants.CONTAINER_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (value != null) {
                                if (resSize == 0){
                                    resSize = value.getDocumentChanges().size();
                                }
                                if (value.getDocumentChanges().size() == 1 && value.getDocumentChanges().get(0).getType() != DocumentChange.Type.REMOVED && resSize != 1) {
                                    startActivity(new Intent(getApplicationContext(), DuoModeActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            .putExtra("roomID", value.getDocumentChanges().get(0).getDocument().getString("roomID"))
                                            .putExtra("senderID",value.getDocumentChanges().get(0).getDocument().getString("userID")));
                                    finish();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}