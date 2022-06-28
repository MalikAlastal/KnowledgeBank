package com.nameisknowledge.knowledgebank.Activities.questionsMode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.nameisknowledge.knowledgebank.Activities.duoMode.DuoModeViewModel;
import com.nameisknowledge.knowledgebank.Adapters.GamePlayAdapter;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Dialogs.AddQuestionDialog;
import com.nameisknowledge.knowledgebank.Dialogs.WinnerDialog;
import com.nameisknowledge.knowledgebank.ModelClasses.gamePlay.QuestionsModeGamePlayMD;
import com.nameisknowledge.knowledgebank.ViewModelsFactory;
import com.nameisknowledge.knowledgebank.databinding.ActivityQuastionsModeBinding;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;

public class QuestionsModeActivity extends AppCompatActivity {
    private static final String TAG = "QuestionsModeActivity";
    private ActivityQuastionsModeBinding binding;
    private GamePlayAdapter inputAdapter,answerAdapter;
    private QuestionsModeViewModel viewModel;
    private String currentQuestionAnswer;
    private Disposable disposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuastionsModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String roomID = getIntent().getStringExtra("roomID");

        setUpRv();

        viewModel = new ViewModelProvider(this,new ViewModelsFactory(roomID, FirebaseConstants.GAME_PLAY_2_COLLECTION)).get(QuestionsModeViewModel.class);

        AddQuestionDialog.newInstance(roomID).show(getSupportFragmentManager(),"Add Questions Dialog");

        disposable = viewModel.isQuestionsAdded().subscribe(()->{
            binding.prg.setVisibility(View.GONE);
            viewModel.start();
        });

        viewModel.question.observe(this,question->{
            binding.tvQuestion.setText(question);
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
            viewModel.submitAnswer(currentQuestionAnswer,answerAdapter.getAnswer());
        }));

        binding.rvAnswer.setAdapter(answerAdapter);
        binding.rvInput.setAdapter(inputAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}