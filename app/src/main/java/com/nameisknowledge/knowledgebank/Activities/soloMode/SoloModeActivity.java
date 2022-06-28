package com.nameisknowledge.knowledgebank.Activities.soloMode;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nameisknowledge.knowledgebank.Adapters.GamePlayAdapter;
import com.nameisknowledge.knowledgebank.Methods.ViewMethods;
import com.nameisknowledge.knowledgebank.MyApplication;
import com.nameisknowledge.knowledgebank.databinding.ActivitySoloModeBinding;

public class SoloModeActivity extends AppCompatActivity {
    private ActivitySoloModeBinding binding;
    private String currentQuestionAnswer;
    private SoloModeActivityViewModel viewModel;
    private GamePlayAdapter inputAdapter,answerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewMethods.setLocale(this , "ar");
        binding = ActivitySoloModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SoloModeActivityViewModel.class);

        setUpRv();

        viewModel.question.observe(this,question->{
            binding.tvQuestion.setText(question);
        });

        viewModel.realAnswer.observe(this,answer ->{
            this.currentQuestionAnswer = answer;
        });

        viewModel.longAnswer.observe(this,longAnswer->{
            inputAdapter.clearArray();
            inputAdapter.setAnswer(longAnswer);
        });

        viewModel.emptyAnswer.observe(this,emptyAnswer->{
            answerAdapter.clearArray();
            answerAdapter.setAnswer(emptyAnswer);
        });


    }

    private void setUpRv(){
        binding.rvAnswer.setHasFixedSize(true);
        binding.rvAnswer.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        binding.rvInput.setHasFixedSize(true);
        binding.rvInput.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        answerAdapter = new GamePlayAdapter(false, inputsMD -> {
            if (inputsMD.getLetter() != ' '){
                inputAdapter.setChar(inputsMD);
            }
        });

        inputAdapter = new GamePlayAdapter(true, inputsMD -> answerAdapter.checkEmpty(list -> {
            if (list.size() != 0) {
                inputAdapter.setEmpty(inputsMD.getIndex(), inputsMD);
                answerAdapter.addChar(inputsMD);
            }
            viewModel.submit(currentQuestionAnswer,answerAdapter.getAnswer());
        }));

        binding.rvAnswer.setAdapter(answerAdapter);
        binding.rvInput.setAdapter(inputAdapter);
    }
}