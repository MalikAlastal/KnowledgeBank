package com.nameisknowledge.knowledgebank.ui.questionsMode;

import static com.nameisknowledge.knowledgebank.methods.StringFactory.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import com.nameisknowledge.knowledgebank.adapters.GamePlayAdapter;
import com.nameisknowledge.knowledgebank.constants.DurationConstants;
import com.nameisknowledge.knowledgebank.constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.dialogs.AddQuestionDialog;
import com.nameisknowledge.knowledgebank.dialogs.WinnerDialog;
import com.nameisknowledge.knowledgebank.methods.AnimationMethods;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.ViewModelsFactory;
import com.nameisknowledge.knowledgebank.databinding.ActivityQuastionsModeBinding;

import io.reactivex.rxjava3.disposables.Disposable;

public class QuestionsModeActivity extends AppCompatActivity {
    private static final String TAG = "QuestionsModeActivity";
    private ActivityQuastionsModeBinding binding;
    private GamePlayAdapter inputAdapter,answerAdapter;
    private QuestionsModeViewModel viewModel;
    private String currentQuestionAnswer;
    private Disposable disposable;
    MediaPlayer clickSound;
    MediaPlayer swingSound;
    MediaPlayer popSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuastionsModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String roomID = getIntent().getStringExtra("roomID");
        String mode = getIntent().getStringExtra("mode");

        clickSound = MediaPlayer.create(this, R.raw.button_clicked);
        swingSound = MediaPlayer.create(this, R.raw.swing);
        popSound = MediaPlayer.create(this, R.raw.pop_sound);

        setUpRv();

        ViewModelsFactory viewModelsFactory = new ViewModelsFactory(roomID, FirebaseConstants.GAME_PLAY_2_COLLECTION);
        viewModelsFactory.setMode(mode);

        viewModel = new ViewModelProvider(this,viewModelsFactory).get(QuestionsModeViewModel.class);

        AddQuestionDialog.newInstance(roomID).show(getSupportFragmentManager(),"Add Questions Dialog");

        disposable = viewModel.isQuestionsAdded().subscribe(()->{
            binding.prg.setVisibility(View.GONE);
            viewModel.start();
        });

        viewModel.question.observe(this, question -> {
            binding.tvQuestion.setText(question.getQuestion());
            this.currentQuestionAnswer = clearAnswerSpaces(question.getAnswer());
            changeQuestionAnimation();
            inputAdapter.clearArray();
            inputAdapter.setAnswer(makeAnswerLonger(clearAnswerSpaces(question.getAnswer())));
            answerAdapter.clearArray();
            answerAdapter.setAnswer(toInputsList(makeStringEmpty(clearAnswerSpaces(question.getAnswer()))));
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

        viewModel.winner.observe(this,winner->{
            viewModel.setTheWinner(winner);
            WinnerDialog.newInstance(winner.getPlayerName()).show(getSupportFragmentManager(), "Winner Dialog");
        });
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

        inputAdapter = new GamePlayAdapter(true, inputsMD -> answerAdapter.checkEmpty(list -> {
            if (list.size() != 0) {
                inputAdapter.setEmpty(inputsMD.getIndex(), inputsMD);
                answerAdapter.addChar(inputsMD);
            }
            viewModel.submitAnswer(currentQuestionAnswer, answerAdapter.getAnswer());
            buttonClickedSound();
        }));

        binding.rvAnswer.setAdapter(answerAdapter);
        binding.rvInput.setAdapter(inputAdapter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}