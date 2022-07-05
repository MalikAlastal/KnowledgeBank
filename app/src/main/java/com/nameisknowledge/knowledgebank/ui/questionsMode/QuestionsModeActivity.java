package com.nameisknowledge.knowledgebank.ui.questionsMode;

import static com.nameisknowledge.knowledgebank.methods.StringFactory.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.nameisknowledge.knowledgebank.adapters.GamePlayAdapter;
import com.nameisknowledge.knowledgebank.constants.DurationConstants;
import com.nameisknowledge.knowledgebank.constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.constants.IntentConstants;
import com.nameisknowledge.knowledgebank.constants.UserConstants;
import com.nameisknowledge.knowledgebank.dialogs.AddQuestionDialog;
import com.nameisknowledge.knowledgebank.dialogs.WinnerDialog;
import com.nameisknowledge.knowledgebank.methods.AnimationMethods;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.ViewModelsFactory;
import com.nameisknowledge.knowledgebank.databinding.ActivityQuastionsModeBinding;
import com.nameisknowledge.knowledgebank.methods.HelpMethods;
import com.nameisknowledge.knowledgebank.modelClasses.questions.Question;

public class QuestionsModeActivity extends AppCompatActivity {
    private static final String TAG = "QuestionsModeActivity";
    private ActivityQuastionsModeBinding binding;
    private GamePlayAdapter inputAdapter,answerAdapter;
    private QuestionsModeViewModel viewModel;
    private String currentQuestionAnswer;
    MediaPlayer clickSound;
    MediaPlayer swingSound;
    MediaPlayer popSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuastionsModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String roomID = getIntent().getStringExtra(IntentConstants.ROOM_ID_KEY);
        String mode = getIntent().getStringExtra(IntentConstants.MODE_KEY);

        clickSound = MediaPlayer.create(this, R.raw.button_clicked);
        swingSound = MediaPlayer.create(this, R.raw.swing);
        popSound = MediaPlayer.create(this, R.raw.pop_sound);

        setUpRv();

        ViewModelsFactory viewModelsFactory = new ViewModelsFactory(roomID,FirebaseConstants.GAME_PLAY_2_COLLECTION);
        viewModelsFactory.setMode(mode);

        viewModel = new ViewModelProvider(this,viewModelsFactory).get(QuestionsModeViewModel.class);

        viewModel.setPoints(UserConstants.getCurrentUser(this).getAreaAttackPoints());

        AddQuestionDialog.newInstance(roomID).show(getSupportFragmentManager(),"Add Questions Dialog");

        viewModel.isQuestionsAdded.observe(this,done->{
            binding.prg.setVisibility(View.GONE);
            viewModel.start();
        });

        viewModel.question.observe(this,this::setDataForUi);

        viewModel.playerScore.observe(this,score->{
            binding.playerScoreTxt.setText(score);
        });

        viewModel.enemyScore.observe(this,score->{
            binding.enemyScoreTxt.setText(score);
        });

        viewModel.userPoints.observe(this, points -> {
            binding.pointsTxt.setText(String.valueOf(points));
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


        viewModel.winner.observe(this,winner->{
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

    private void setDataForUi(Question question){
        binding.tvQuestion.setText(question.getQuestion());
        currentQuestionAnswer = clearAnswerSpaces(question.getAnswer());
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