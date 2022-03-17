package com.nameisknowledge.knowledgebank.BroadCastRecivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nameisknowledge.knowledgebank.Activities.DuoModeActivity;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.ModelClasses.ResponseMD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationBroadCast extends BroadcastReceiver {
    private String senderID;

    public NotificationBroadCast(String sID) {
        this.senderID = sID;
    }

    public NotificationBroadCast() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        senderID = intent.getStringExtra("senderID");
        FirebaseFirestore.getInstance().collection(FirebaseConstants.QUESTIONS_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int max = task.getResult().size();
                    List<Integer> questionsIndex = new ArrayList<>();

                    for (int i=0;i<max;i++){
                        questionsIndex.add(i);
                    }

                    Map<String,Object> map = new HashMap<>();
                    map.put("questionsIndex",questionsIndex);
                    map.put(FirebaseAuth.getInstance().getUid(),0);
                    map.put(senderID,0);

                    FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION).add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            FirebaseFirestore.getInstance().collection(FirebaseConstants.RESPONSES_COLLECTION).document(senderID).collection(FirebaseConstants.CONTAINER_COLLECTION).add(new ResponseMD(documentReference.getId())).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            context.startActivity(new Intent(context, DuoModeActivity.class).
                                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).
                                                    putExtra("roomID",documentSnapshot.getString("roomID")));
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
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}