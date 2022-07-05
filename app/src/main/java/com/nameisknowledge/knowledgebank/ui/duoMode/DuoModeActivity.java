package com.nameisknowledge.knowledgebank.ui.duoMode;

import static com.nameisknowledge.knowledgebank.methods.StringFactory.*;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.nameisknowledge.knowledgebank.adapters.GamePlayAdapter;
import com.nameisknowledge.knowledgebank.constants.DurationConstants;
import com.nameisknowledge.knowledgebank.constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.constants.IntentConstants;
import com.nameisknowledge.knowledgebank.constants.UserConstants;
import com.nameisknowledge.knowledgebank.dialogs.WinnerDialog;
import com.nameisknowledge.knowledgebank.methods.AnimationMethods;
import com.nameisknowledge.knowledgebank.methods.HelpMethods;
import com.nameisknowledge.knowledgebank.methods.ViewMethods;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.ViewModelsFactory;
import com.nameisknowledge.knowledgebank.databinding.ActivityDuoModeBinding;
import com.nameisknowledge.knowledgebank.modelClasses.questions.FireBaseQuestionMD;

public class DuoModeActivity extends AppCompatActivity {
    private final String TAG = "DuoModeActivity";
    private String currentQuestionAnswer,hint;
    private ActivityDuoModeBinding binding;
    private GamePlayAdapter inputAdapter, answerAdapter;
    private boolean isHintUsed;
    private DuoModeViewModel viewModel;
    MediaPlayer clickSound;
    MediaPlayer swingSound;
    MediaPlayer popSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDuoModeBinding.inflate(getLayoutInflater());
        ViewMethods.setLocale(this, "ar");
        setContentView(binding.getRoot());

        String roomID = getIntent().getStringExtra(IntentConstants.ROOM_ID_KEY);
        String mode = getIntent().getStringExtra(IntentConstants.MODE_KEY);

        clickSound = MediaPlayer.create(this, R.raw.button_clicked);
        swingSound = MediaPlayer.create(this, R.raw.swing);
        popSound = MediaPlayer.create(this, R.raw.pop_sound);

        ViewModelsFactory viewModelsFactory = new ViewModelsFactory(roomID, FirebaseConstants.GAME_PLAY_COLLECTION);
        viewModelsFactory.setMode(mode);

        viewModel = new ViewModelProvider(this, viewModelsFactory).get(DuoModeViewModel.class);

        viewModel.setPoints(UserConstants.getCurrentUser(this).getAreaAttackPoints());

        setUpRv();


        viewModel.question.observe(this,this::setDataForUi);

        viewModel.playerScore.observe(this, score -> {
            binding.playerScoreTxt.setText(score);
        });

        viewModel.enemyScore.observe(this, score -> {
            binding.enemyScoreTxt.setText(score);
        });

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

        viewModel.winner.observe(this, winner -> {
            WinnerDialog.newInstance(winner).show(getSupportFragmentManager(), "Winner Dialog");
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

    private void setDataForUi(FireBaseQuestionMD question){
        binding.tvQuestion.setText(question.getQuestion());
        this.currentQuestionAnswer = clearAnswerSpaces(question.getAnswer());
        this.hint = question.getHint();
        binding.playerNameTxt.setText(viewModel.getPlayer().getPlayerName());
        binding.enemyNameTxt.setText(viewModel.getEnemy().getPlayerName());
        changeQuestionAnimation();
        inputAdapter.clearArray();
        inputAdapter.setAnswer(makeAnswerLonger(clearAnswerSpaces(question.getAnswer())));
        answerAdapter.clearArray();
        answerAdapter.setAnswer(toInputsList(makeStringEmpty(clearAnswerSpaces(question.getAnswer()))));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!viewModel.isGameFinished()) viewModel.setTheWinner(viewModel.getEnemy());
    }
}