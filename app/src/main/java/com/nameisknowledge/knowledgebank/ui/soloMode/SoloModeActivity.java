package com.nameisknowledge.knowledgebank.ui.soloMode;


import static com.nameisknowledge.knowledgebank.methods.StringFactory.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.nameisknowledge.knowledgebank.adapters.GamePlayAdapter;
import com.nameisknowledge.knowledgebank.constants.DurationConstants;
import com.nameisknowledge.knowledgebank.constants.UserConstants;
import com.nameisknowledge.knowledgebank.dialogs.WinnerDialog;
import com.nameisknowledge.knowledgebank.methods.AnimationMethods;
import com.nameisknowledge.knowledgebank.methods.ViewMethods;
import com.nameisknowledge.knowledgebank.modelClasses.InputsMD;
import com.nameisknowledge.knowledgebank.modelClasses.UserMD;
import com.nameisknowledge.knowledgebank.modelClasses.questions.FireBaseQuestionMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.ActivitySoloModeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;

public class SoloModeActivity extends AppCompatActivity {
    private ActivitySoloModeBinding binding;
    private String currentQuestionAnswer;
    private SoloModeViewModel viewModel;
    private GamePlayAdapter inputAdapter, answerAdapter;
    private List<String> hints = new ArrayList<>();
    private final List<Integer> repeatedCharsIndexes = new ArrayList<>();
    private int repeatedCharsCurrentIndex, hintsIndex;
    MediaPlayer clickSound;
    MediaPlayer swingSound;
    MediaPlayer popSound;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewMethods.setLocale(this, "ar");
        binding = ActivitySoloModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SoloModeViewModel.class);

        UserMD userMD = UserConstants.getCurrentUser(this);

        viewModel.setThePoints(userMD.getAreaAttackPoints());

        clickSound = MediaPlayer.create(this, R.raw.button_clicked);
        swingSound = MediaPlayer.create(this, R.raw.swing);
        popSound = MediaPlayer.create(this, R.raw.pop_sound);


        setUpRv();

        viewModel.question.observe(this, this::setUiData);

        viewModel.userPoints.observe(this, points -> {
            enableHelpMethods(points != 0);
            binding.pointsTxt.setText(String.valueOf(points));
        });

        binding.delete.setOnClickListener(view -> {
            deleteChar();
            viewModel.updatePoints("delete");
        });

        binding.hint.setOnClickListener(view -> {
            showHint();
            viewModel.updatePoints("hint");
        });

        binding.showChar.setOnClickListener(view -> {
            showChar();
            viewModel.updatePoints("showChar");
        });

        binding.btnEndGame.setOnClickListener(view -> {
            viewModel.finishTheGame(userMD.getUid());
        });

        viewModel.updatedUser.observe(this, user -> {
            UserConstants.setCurrentUser(user, this);
            finish();
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

        inputAdapter = new GamePlayAdapter(true, inputsMD -> {
            answerAdapter.checkEmpty(list -> {
                if (list.size() != 0) {
                    inputAdapter.setEmpty(inputsMD.getIndex(), inputsMD);
                    answerAdapter.addChar(inputsMD);
                }
                viewModel.submit(currentQuestionAnswer, answerAdapter.getAnswer());
                buttonClickedSound();
            });
        });

        binding.rvAnswer.setAdapter(answerAdapter);
        binding.rvInput.setAdapter(inputAdapter);
    }

    private void setUiData(FireBaseQuestionMD question) {
        this.hints = question.getHints();
        binding.tvQuestion.setText(question.getQuestion());
        this.currentQuestionAnswer = clearAnswerSpaces(question.getAnswer());
        changeQuestionAnimation();
        inputAdapter.clearArray();
        inputAdapter.setAnswer(makeAnswerLonger(clearAnswerSpaces(question.getAnswer())));
        answerAdapter.clearArray();
        answerAdapter.setAnswer(toInputsList(makeStringEmpty(clearAnswerSpaces(question.getAnswer()))));
    }

    private void deleteChar() {
        for (InputsMD inputsMD : inputAdapter.getMyList()) {
            if (inputsMD.isAdded()) {
                inputAdapter.setEmpty(inputsMD.getIndex(), inputsMD);
                break;
            }
        }
    }

    private void showChar() {
        for (int i = 0; i < inputAdapter.getMyList().size(); i++) {
            // to check if the char is NotAdded;
            if (!inputAdapter.getMyList().get(i).isAdded() && inputAdapter.getMyList().get(i).getLetter() != ' ') {
                // to check if the char is exists in real answer (answerArray)
                if (toCharList(answerAdapter.getMyList()).contains(inputAdapter.getMyList().get(i).getLetter())) {
                    // if If it is twice in the answer
                    if (getCountOfChar(inputAdapter.getMyList().get(i).getLetter()) > 1) {
                        // then get the last index
                        int index = repeatedCharsIndexes.get(repeatedCharsCurrentIndex);
                        inputAdapter.setEmpty(inputAdapter.getMyList().get(i).getIndex(), inputAdapter.getMyList().get(i));
                        answerAdapter.setChar(new InputsMD(currentQuestionAnswer.charAt(index), index).setShown(true));
                        repeatedCharsCurrentIndex++;
                    }
                } else {
                    // then get the first index
                    int index = toCharList(cutString(currentQuestionAnswer)).indexOf(inputAdapter.getMyList().get(i).getLetter());
                    inputAdapter.setEmpty(inputAdapter.getMyList().get(i).getIndex(), inputAdapter.getMyList().get(i));
                    answerAdapter.setChar(new InputsMD(currentQuestionAnswer.charAt(index), index).setShown(true));
                }
                break;
            }
        }
        viewModel.submit(currentQuestionAnswer, answerAdapter.getAnswer());
    }

    private int getCountOfChar(char chr) {
        int count = 0;
        for (int i = 0; i < currentQuestionAnswer.length(); i++) {
            if (currentQuestionAnswer.charAt(i) == chr) {
                if (count > 0) {
                    repeatedCharsIndexes.add(i);
                }
                count++;
            }
        }
        return count;
    }

    private List<Character> toCharList(List<InputsMD> list) {
        List<Character> characterList = new ArrayList<>();
        for (InputsMD inputsMD : list) {
            characterList.add(inputsMD.getLetter());
        }
        return characterList;
    }

    private void showHint() {
        if (hintsIndex != hints.size() - 1) {
            if (hints.get(hintsIndex) != null) {
                WinnerDialog.newInstance(hints.get(hintsIndex)).show(getSupportFragmentManager(), " ");
                hintsIndex++;
            }
        } else {
            Toast.makeText(this, "no hints", Toast.LENGTH_SHORT).show();
        }
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

    private void enableHelpMethods(boolean enable) {
        enableItems(false);
        if (enable){
            disposable = Completable.timer(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        Toast.makeText(this, "يمكنك استعمال مساعدة مرة اخرى", Toast.LENGTH_SHORT).show();
                        enableItems(true);
                    });
        }
    }

    private void enableItems(boolean enable){
        binding.hint.setEnabled(enable);
        binding.showChar.setEnabled(enable);
        binding.delete.setEnabled(enable);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }
}