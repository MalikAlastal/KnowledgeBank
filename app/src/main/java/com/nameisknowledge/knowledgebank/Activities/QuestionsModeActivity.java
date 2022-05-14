package com.nameisknowledge.knowledgebank.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.ActivityQuastionsModeBinding;

public class QuestionsModeActivity extends AppCompatActivity {
    private ActivityQuastionsModeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuastionsModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}