package com.nameisknowledge.knowledgebank.ui.attackArea;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nameisknowledge.knowledgebank.FireBaseRepository;
import com.nameisknowledge.knowledgebank.modelClasses.MapAreaMD;
import com.nameisknowledge.knowledgebank.modelClasses.UserMD;
import com.nameisknowledge.knowledgebank.modelClasses.questions.MapFireBaseQuestionMD;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class AttackAreaViewModel extends ViewModel {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final FireBaseRepository fireBaseRepository;
    private final MapAreaMD mapArea;
    private int index, answeredQuestions;
    public final MutableLiveData<MapFireBaseQuestionMD> question = new MutableLiveData<>();
    public final MutableLiveData<Integer> playerAnsweredMoreThanOwner = new MutableLiveData<>();
    public final MutableLiveData<UserMD> updatedUser = new MutableLiveData<>();
    public final MutableLiveData<Integer> allQuestionsAnswered = new MutableLiveData<>();

    public AttackAreaViewModel(MapAreaMD mapAreaMD) {
        this.fireBaseRepository = FireBaseRepository.getInstance();
        this.mapArea = mapAreaMD;
        question.setValue(mapAreaMD.getQuestionList().get(index));
    }

    public void submitAnswer(String realAnswer, String input) {
        if (realAnswer.equals(input)) {
            nextQuestion();
        }
    }

    private void nextQuestion() {
        this.answeredQuestions++;
        // first check if the player answer all the questions
        if (index == mapArea.getQuestionList().size() - 1) {
            // note:(1 here is useless but we cant use live data with null values)
            allQuestionsAnswered.setValue(1);
            // second check if the player answered mode than the owner
        }  else if (index == (mapArea.getOwnerAnsweredQuestionsCount() - 1)) {
            playerAnsweredMoreThanOwner.setValue(answeredQuestions);
            // finally if non of above conditions is true then send the second question
        } else if (index != mapArea.getQuestionList().size() - 1) {
            index++;
            question.setValue(mapArea.getQuestionList().get(index));
        }
    }

    public void setOwner(String areaName, UserMD user, int questionsCount) {
        fireBaseRepository.setAreaOwner(areaName, user, questionsCount).subscribe();
    }

    public void updatedAttackPoints(String id, int points) {
        fireBaseRepository.updateAreaAttackPoints(id, points)
                .subscribe(updatedUserObserver());
    }


    private SingleObserver<UserMD> updatedUserObserver() {
        return new SingleObserver<UserMD>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull UserMD userMD) {
                updatedUser.setValue(userMD);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }
        };
    }

}
