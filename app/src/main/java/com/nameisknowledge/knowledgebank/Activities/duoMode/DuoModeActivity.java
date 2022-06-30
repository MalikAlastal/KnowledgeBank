package com.nameisknowledge.knowledgebank.Activities.duoMode;

import android.animation.Animator;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.daimajia.androidanimations.library.YoYo;
import com.nameisknowledge.knowledgebank.Adapters.GamePlayAdapter;
import com.nameisknowledge.knowledgebank.Constants.DurationConstants;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Dialogs.WinnerDialog;
import com.nameisknowledge.knowledgebank.Methods.AnimationMethods;
import com.nameisknowledge.knowledgebank.Methods.ViewMethods;
import com.nameisknowledge.knowledgebank.ViewModelsFactory;
import com.nameisknowledge.knowledgebank.databinding.ActivityDuoModeBinding;

import java.util.Observable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DuoModeActivity extends AppCompatActivity {
    private final String TAG = "DuoModeActivity";
    private String currentQuestionAnswer;
    private ActivityDuoModeBinding binding;
    private GamePlayAdapter inputAdapter,answerAdapter;
    private DuoModeViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDuoModeBinding.inflate(getLayoutInflater());
        ViewMethods.setLocale(this , "ar");
        setContentView(binding.getRoot());

        String mode = getIntent().getStringExtra("mode");
        String roomID = getIntent().getStringExtra("roomID");

        ViewModelsFactory viewModelsFactory = new ViewModelsFactory(roomID, FirebaseConstants.GAME_PLAY_COLLECTION);
        viewModelsFactory.setMode(mode);

        viewModel = new ViewModelProvider(this,viewModelsFactory).get(DuoModeViewModel.class);

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

        viewModel.playerScore.observe(this,score->{
            binding.playerScoreTxt.setText(score);
        });

        viewModel.enemyScore.observe(this,score->{
            binding.enemyScoreTxt.setText(score);
        });

        viewModel.playerName.observe(this,name->{
            binding.playerNameTxt.setText(name);
        });

        viewModel.enemyName.observe(this,name->{
            binding.enemyNameTxt.setText(name);
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
            viewModel.submitAnswer(currentQuestionAnswer,answerAdapter.getAnswer());
        }));

        binding.rvAnswer.setAdapter(answerAdapter);
        binding.rvInput.setAdapter(inputAdapter);
    }

}