package com.nameisknowledge.knowledgebank.ui.soloMode;


import static com.nameisknowledge.knowledgebank.methods.StringFactory.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import com.nameisknowledge.knowledgebank.ViewModelsFactory;
import com.nameisknowledge.knowledgebank.adapters.GamePlayAdapter;
import com.nameisknowledge.knowledgebank.constants.DurationConstants;
import com.nameisknowledge.knowledgebank.constants.IntentConstants;
import com.nameisknowledge.knowledgebank.constants.UserConstants;
import com.nameisknowledge.knowledgebank.dialogs.WinnerDialog;
import com.nameisknowledge.knowledgebank.methods.AnimationMethods;
import com.nameisknowledge.knowledgebank.methods.HelpMethods;
import com.nameisknowledge.knowledgebank.methods.ViewMethods;
import com.nameisknowledge.knowledgebank.modelClasses.questions.FireBaseQuestionMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.ActivitySoloModeBinding;

public class SoloModeActivity extends AppCompatActivity {
    private ActivitySoloModeBinding binding;
    private String currentQuestionAnswer, hint;
    private SoloModeViewModel viewModel;
    private GamePlayAdapter inputAdapter, answerAdapter;
    private MediaPlayer clickSound,swingSound,popSound;
    private boolean isHintUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewMethods.setLocale(this, "ar");
        binding = ActivitySoloModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String mode = getIntent().getStringExtra(IntentConstants.MODE_KEY);

        ViewModelsFactory viewModelsFactory = new ViewModelsFactory(UserConstants.getCurrentUser(this).getUid());
        viewModelsFactory.setMode(mode);

        viewModel = new ViewModelProvider(this,viewModelsFactory).get(SoloModeViewModel.class);

        viewModel.setPoints(UserConstants.getCurrentUser(this).getAreaAttackPoints());

        clickSound = MediaPlayer.create(this, R.raw.button_clicked);
        swingSound = MediaPlayer.create(this, R.raw.swing);
        popSound = MediaPlayer.create(this, R.raw.pop_sound);


        setUpRv();

        viewModel.question.observe(this, this::setUiData);

        viewModel.userPoints.observe(this, points -> {
            binding.pointsTxt.setText(String.valueOf(points));
            if (!isHintUsed) binding.hint.setEnabled(points>=5);
        });

        binding.delete.setOnClickListener(view -> {
            HelpMethods.deleteChar(viewModel.getPoints(),inputAdapter, answer->{
                if (answer.isEmpty()){
                    viewModel.updatePoints("delete");
                }else {
                    Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
                    binding.delete.setEnabled(false);
                }
            });
        });

        binding.hint.setOnClickListener(view -> {
            HelpMethods.showHint(viewModel.getPoints(),s->{
                if (s.isEmpty()){
                    isHintUsed = true;
                    WinnerDialog.newInstance(hint).show(getSupportFragmentManager(), " ");
                    viewModel.updatePoints("hint");
                    binding.hint.setEnabled(false);
                }else {
                    Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
                    binding.showChar.setEnabled(false);
                }
            });
        });

        binding.showChar.setOnClickListener(view -> {
            HelpMethods.showChar(viewModel.getPoints(),inputAdapter,answerAdapter,currentQuestionAnswer,answer->{
                if (answer.isEmpty()){
                    viewModel.updatePoints("showChar");
                    viewModel.submitAnswer(currentQuestionAnswer,answerAdapter.getAnswer());
                }else {
                    Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
                    binding.showChar.setEnabled(false);
                }
            });
        });

        binding.btnEndGame.setOnClickListener(view -> {
            viewModel.gameFinished = true;
            viewModel.finishTheGame();
            finish();
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        HelpMethods.emptyValues();
        if (!viewModel.isGameFinished()) viewModel.finishTheGame();
    }

    private void setUpRv() {
        binding.rvAnswer.setHasFixedSize(true);
        binding.rvAnswer.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        binding.rvInput.setHasFixedSize(true);
        binding.rvInput.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        answerAdapter = new GamePlayAdapter(false, inputsMD -> {
            if (inputsMD.getLetter() != ' ') {
                inputAdapter.setChar(inputsMD);
            }
            buttonClickedSound();
        });

        inputAdapter = new GamePlayAdapter(true, inputsMD -> {
            answerAdapter.checkEmpty(list -> {
                if (list.size() != 0) {
                    inputAdapter.setEmpty(inputsMD.getIndex(), inputsMD);
                    answerAdapter.addChar(inputsMD);
                }
                viewModel.submitAnswer(currentQuestionAnswer, answerAdapter.getAnswer());
                buttonClickedSound();
            });
        });

        binding.rvAnswer.setAdapter(answerAdapter);
        binding.rvInput.setAdapter(inputAdapter);
    }

    private void setUiData(FireBaseQuestionMD question) {
        this.hint = question.getHint();
        binding.tvQuestion.setText(question.getQuestion());
        this.currentQuestionAnswer = clearAnswerSpaces(question.getAnswer());
        changeQuestionAnimation();
        inputAdapter.clearArray();
        inputAdapter.setAnswer(makeAnswerLonger(clearAnswerSpaces(question.getAnswer())));
        answerAdapter.clearArray();
        answerAdapter.setAnswer(toInputsList(makeStringEmpty(clearAnswerSpaces(question.getAnswer()))));
    }

    private void changeQuestionAnimation() {
        swingSound.start();
        AnimationMethods.slideOutLeft(DurationConstants.DURATION_SO_SHORT,
                animator -> AnimationMethods.slideInRight(DurationConstants.DURATION_SO_SHORT, binding.cardQuestion, binding.rvInput),
                binding.cardQuestion, binding.rvInput);
    }

    private void buttonClickedSound() {
        if (clickSound.isPlaying()) {
            clickSound.seekTo(0);
        } else {
            clickSound.start();
        }
    }

}