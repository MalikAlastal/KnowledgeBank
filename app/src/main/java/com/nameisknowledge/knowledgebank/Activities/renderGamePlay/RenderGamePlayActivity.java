package com.nameisknowledge.knowledgebank.Activities.renderGamePlay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.auth.User;
import com.nameisknowledge.knowledgebank.Activities.duoMode.DuoModeActivity;
import com.nameisknowledge.knowledgebank.Constants.UserConstants;
import com.nameisknowledge.knowledgebank.ViewModelsFactory;
import com.nameisknowledge.knowledgebank.databinding.ActivityRenderGamePlayBinding;

public class RenderGamePlayActivity extends AppCompatActivity {
    private final String TAG = "RenderGamePlayActivity";
    private ActivityRenderGamePlayBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRenderGamePlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        String senderName = getIntent().getStringExtra("senderName");
        String senderId = getIntent().getStringExtra("senderId");

        RenderActivityViewModel viewModel = new ViewModelProvider(this, new ViewModelsFactory(senderName,senderId)).get(RenderActivityViewModel.class);

        viewModel.init();

        // if the user who is intent to this activity is the sender
        viewModel.responseListener.observe(this, roomID -> {
            startActivity(new Intent(this, DuoModeActivity.class)
                    .putExtra("roomID", roomID));
            finish();
        });

        // if the user who is intent to this activity is the receiver
        viewModel.responseObj.observe(this, roomID -> {
            startActivity(new Intent(this, DuoModeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("roomID", roomID));
            finish();
        });

        viewModel.timeOut.observe(this,timeOut->{
            Toast.makeText(this, timeOut, Toast.LENGTH_SHORT).show();
            finish();
        });

    }


}
