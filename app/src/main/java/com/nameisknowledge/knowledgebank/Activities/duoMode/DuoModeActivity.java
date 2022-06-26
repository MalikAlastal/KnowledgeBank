package com.nameisknowledge.knowledgebank.Activities.duoMode;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.nameisknowledge.knowledgebank.Adapters.GamePlayAdapter;
import com.nameisknowledge.knowledgebank.Constants.UserConstants;
import com.nameisknowledge.knowledgebank.Dialogs.WinnerDialog;
import com.nameisknowledge.knowledgebank.Methods.ViewMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.ResponseMD;
import com.nameisknowledge.knowledgebank.ViewModelsFactory;
import com.nameisknowledge.knowledgebank.databinding.ActivityDuoModeBinding;

public class DuoModeActivity extends AppCompatActivity {
    private final String TAG = "DuoModeActivity";
    private String currentQuestionAnswer;
    private ActivityDuoModeBinding binding;
    private GamePlayAdapter inputAdapter,answerAdapter;
    private DuoActivityViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDuoModeBinding.inflate(getLayoutInflater());
        ViewMethods.setLocale(this , "ar");
        setContentView(binding.getRoot());

        String roomID = getIntent().getStringExtra("roomID");

        viewModel = new ViewModelProvider(this,new ViewModelsFactory(roomID)).get(DuoActivityViewModel.class);

        setUpRv();

        viewModel.question.observe(this,question->{
            binding.tvQuestion.setText(question);
        });

        viewModel.realAnswer.observe(this,answer->{
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

        viewModel.winnerName.observe(this,winner->{
            viewModel.setTheWinner(winner);
            WinnerDialog.newInstance(winner).show(getSupportFragmentManager(), "Winner Dialog");
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