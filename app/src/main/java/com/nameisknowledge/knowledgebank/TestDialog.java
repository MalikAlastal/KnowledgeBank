package com.nameisknowledge.knowledgebank;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nameisknowledge.knowledgebank.Activities.GameActivity;

import java.util.HashMap;
import java.util.Map;

public class TestDialog extends DialogFragment {
    private static DocumentReference documentId;
    private static String sId;
    public static TestDialog newInstance(DocumentReference documentReference,String senderId) {
        TestDialog fragment = new TestDialog();
        Bundle args = new Bundle();
        documentId = documentReference;
        sId = senderId;
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Request")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        documentId.update("Request","accepted").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                FirebaseFirestore.getInstance().collection("Questions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()){
                                            int max = task.getResult().size();
                                            int[] questionsIndex = new int[max];
                                            for (int i=0;i<max;i++){
                                                questionsIndex[i] = getRandomNumber(0,max);
                                            }
                                            Map<String,Object> map = new HashMap<>();
                                            map.put("questionsIndex",questionsIndex);
                                            map.put(FirebaseAuth.getInstance().getUid(),0);
                                            map.put(sId,0);
                                            FirebaseFirestore.getInstance().collection("GamePlay").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Map<String,String> map1 = new HashMap<>();
                                                    map1.put("RoomId",documentReference.getId());
                                                    FirebaseFirestore.getInstance().collection("Response").document(sId).collection("container").add(map1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            requireContext().startActivity(new Intent(requireContext(), GameActivity.class).putExtra("RoomId",documentReference.getId()));
                                                            dismiss();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            dismiss();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        documentId.update("Request","canceled").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }




    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
