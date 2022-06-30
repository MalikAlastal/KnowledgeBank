package com.nameisknowledge.knowledgebank.ui.renderGamePlay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.nameisknowledge.knowledgebank.databinding.ActivityRenderGamePlayBinding;
import com.nameisknowledge.knowledgebank.ui.duoMode.DuoModeActivity;
import com.nameisknowledge.knowledgebank.ui.questionsMode.QuestionsModeActivity;
import com.nameisknowledge.knowledgebank.ViewModelsFactory;

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
        String mode = getIntent().getStringExtra("mode");

        RenderGamePlayViewModel viewModel = new ViewModelProvider(this, new ViewModelsFactory(senderName,senderId,mode)).get(RenderGamePlayViewModel.class);

        viewModel.init();

        // if the user who is intent to this activity is the sender
        viewModel.responseListener.observe(this, response -> {
            switch (response.getMode()){
                case "DuoMode":
                    startActivity(new Intent(this, DuoModeActivity.class).putExtra("roomID", response.getRoomID()).putExtra("mode",response.getMode()));
                    break;
                case "QuestionsMode":
                    startActivity(new Intent(this, QuestionsModeActivity.class).putExtra("roomID", response.getRoomID()).putExtra("mode",response.getMode()));
                    break;
            }
            finish();
        });

        // if the user who is intent to this activity is the receiver
        viewModel.responseObj.observe(this, response -> {
            switch (response.getMode()){
                case "DuoMode":
                    startActivity(new Intent(this, DuoModeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("roomID", response.getRoomID()).putExtra("mode",response.getMode()));
                    break;
                case "QuestionsMode":
                    startActivity(new Intent(this, QuestionsModeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("roomID", response.getRoomID()).putExtra("mode",response.getMode()));
                    break;
            }
            finish();
        });

        viewModel.timeOut.observe(this,timeOut->{
            Toast.makeText(this, timeOut, Toast.LENGTH_SHORT).show();
            finish();
        });

    }


}