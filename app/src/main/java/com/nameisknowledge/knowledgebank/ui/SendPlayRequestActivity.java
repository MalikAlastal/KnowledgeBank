package com.nameisknowledge.knowledgebank.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.constants.IntentConstants;
import com.nameisknowledge.knowledgebank.databinding.ActivitySendPlayRequestBinding;
import com.nameisknowledge.knowledgebank.ui.renderGamePlay.RenderGamePlayActivity;
import com.nameisknowledge.knowledgebank.adapters.UsersAdapter;
import com.nameisknowledge.knowledgebank.constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.constants.UserConstants;
import com.nameisknowledge.knowledgebank.modelClasses.NotificationMD;
import com.nameisknowledge.knowledgebank.modelClasses.UserMD;
import com.nameisknowledge.knowledgebank.retroift.Data;
import com.nameisknowledge.knowledgebank.retroift.NotificationData;
import com.nameisknowledge.knowledgebank.retroift.PushNotification;
import com.nameisknowledge.knowledgebank.retroift.RetrofitInstance;

import java.util.List;

public class SendPlayRequestActivity extends AppCompatActivity {
    ActivitySendPlayRequestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendPlayRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String mode = getIntent().getStringExtra(IntentConstants.MODE_KEY);
        String senderId = UserConstants.getCurrentUser(this).getUid();
        String senderName = UserConstants.getCurrentUser(this).getUsername();

        Toast.makeText(this, mode, Toast.LENGTH_SHORT).show();

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
        NotificationData notificationData = new NotificationData("Play Request","","OPEN_ACTIVITY");
        Data data = new Data(notification.getSenderName(),notification.getSenderId(),notification.getMode());
        PushNotification pushNotification = new PushNotification(notificationData,notification.getTargetToken(),data);
        RetrofitInstance.getInstance().sentNot(pushNotification);
    }
}