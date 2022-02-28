package com.nameisknowledge.knowledgebank;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nameisknowledge.knowledgebank.Activities.GameActivity;
import com.nameisknowledge.knowledgebank.Activities.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDialog extends DialogFragment {
    private static String sId;
    public static TestDialog newInstance(DocumentReference documentReference,String senderId) {
        TestDialog fragment = new TestDialog();
        Bundle args = new Bundle();
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

                                FirebaseFirestore.getInstance().collection("Questions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()){
                                            int max = task.getResult().size();
                                            List<Integer> questionsIndex = new ArrayList<>();
                                            for (int i=1;i<=max;i++){
                                                questionsIndex.add(i);
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
                                                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    MainActivity.context.startActivity(new Intent(MainActivity.context, GameActivity.class).putExtra("RoomId",documentSnapshot.getString("RoomId")));
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d("Error",e.getMessage());
                                                                }
                                                            });
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
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
