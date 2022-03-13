package com.nameisknowledge.knowledgebank.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nameisknowledge.knowledgebank.Activities.MainActivity;

import java.util.Objects;

public class RequestsService extends Service {
    public RequestsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseFirestore.getInstance().collection("Requests").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection("container").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null){
                    if (value!=null){
                        if (value.getDocumentChanges().size() == 1 && (value.getDocumentChanges().get(0).getType()!= DocumentChange.Type.MODIFIED || value.getDocumentChanges().get(0).getType()!= DocumentChange.Type.REMOVED)){
                            Log.d("docId",value.getDocumentChanges().get(0).getDocument().getId());
                        }
                    }
                }else {
                    Toast.makeText(RequestsService.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseFirestore.getInstance().collection("Response").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection("container").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null){
                    if (value!=null){
                        if (value.getDocumentChanges().size() == 1 && value.getDocumentChanges().get(0).getType()!= DocumentChange.Type.REMOVED){
                            Toast.makeText(RequestsService.this,value.getDocumentChanges().get(0).getDocument().getString("roomId"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(RequestsService.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}