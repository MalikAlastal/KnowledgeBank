package com.nameisknowledge.knowledgebank.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.Activities.renderGamePlay.RenderGamePlayActivity;
import com.nameisknowledge.knowledgebank.Adapters.UsersAdapter;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Constants.UserConstants;
import com.nameisknowledge.knowledgebank.ModelClasses.NotificationMD;
import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.Retroift.Data;
import com.nameisknowledge.knowledgebank.Retroift.NotificationData;
import com.nameisknowledge.knowledgebank.Retroift.PushNotification;
import com.nameisknowledge.knowledgebank.Retroift.RetrofitInstance;
import com.nameisknowledge.knowledgebank.databinding.ActivitySendPlayRequestBinding;

public class SendPlayRequestActivity extends AppCompatActivity {
    ActivitySendPlayRequestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendPlayRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String mode = getIntent().getStringExtra("mode");
        String senderId = UserConstants.getCurrentUser(this).getUid();
        String senderName = UserConstants.getCurrentUser(this).getUsername();

        UsersAdapter adapter = new UsersAdapter(user -> {
            sendMessage(new NotificationMD(user.getNotificationToken(),senderName,senderId,mode));
            startActivity(new Intent(this,RenderGamePlayActivity.class));
            finish();
        });

        binding.rv.setHasFixedSize(true);
        binding.rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.rv.setAdapter(adapter);

        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.USERS_COLLECTION).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    adapter.setUsers(queryDocumentSnapshots.toObjects(UserMD.class));
                }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void sendMessage(NotificationMD notification) {
        NotificationData notificationData = new NotificationData("Play Request","");
        Data data = new Data(notification.getSenderName(),notification.getSenderId(),notification.getMode());
        PushNotification pushNotification = new PushNotification(notificationData,notification.getTargetToken(),data);
        RetrofitInstance.getInstance().sentNot(pushNotification);
    }
}