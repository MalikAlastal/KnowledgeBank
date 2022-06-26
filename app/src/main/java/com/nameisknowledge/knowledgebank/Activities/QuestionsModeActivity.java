package com.nameisknowledge.knowledgebank.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.nameisknowledge.knowledgebank.Dialogs.AddQuestionDialog;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.ActivityQuastionsModeBinding;

import java.util.List;

public class QuestionsModeActivity extends AppCompatActivity {
    private ActivityQuastionsModeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuastionsModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        String roomID = intent.getStringExtra("roomID");
        String senderID = intent.getStringExtra("senderID");
        AddQuestionDialog.newInstance(senderID, roomID, new GenericListener<List<QuestionMD>>() {
            @Override
            public void getData(List<QuestionMD> questionMDS) {
                Toast.makeText(QuestionsModeActivity.this, questionMDS.size()+"", Toast.LENGTH_SHORT).show();
            }
        }).show(getSupportFragmentManager(),"Dialog");

    }
}