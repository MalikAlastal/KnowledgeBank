package com.nameisknowledge.knowledgebank.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.nameisknowledge.knowledgebank.Dialogs.AddQuestionDialog;
import com.nameisknowledge.knowledgebank.databinding.ActivityQuastionsModeBinding;

public class QuestionsModeActivity extends AppCompatActivity {
    private static final String TAG = "QuestionsModeActivity";
    private ActivityQuastionsModeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuastionsModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG,"onCreate");
        AddQuestionDialog.newInstance("", "", questionMDS -> {
        }).show(getSupportFragmentManager(),"abood");
    }

}