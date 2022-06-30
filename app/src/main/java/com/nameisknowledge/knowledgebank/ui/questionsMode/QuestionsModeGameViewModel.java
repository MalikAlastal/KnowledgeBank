package com.nameisknowledge.knowledgebank.ui.questionsMode;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.nameisknowledge.knowledgebank.modelClasses.gamePlay.GamePlay;
import com.nameisknowledge.knowledgebank.modelClasses.gamePlay.QuestionsModeGamePlayMD;
import com.nameisknowledge.knowledgebank.modelClasses.questions.LocalQuestionMD;
import com.nameisknowledge.knowledgebank.baseViewModels.MultiPlayersGameViewModel;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class QuestionsModeGameViewModel extends MultiPlayersGameViewModel<LocalQuestionMD,LocalQuestionMD> {

    private ListenerRegistration isQuestionsAddedRegistration;
    public final MutableLiveData<String> isQuestionsAdded = new MutableLiveData<>();

    public QuestionsModeGameViewModel(String mode, String roomID, String gamePlayCollection) {
        super(mode, roomID, gamePlayCollection);
        isQuestionsAddedObservable().subscribe(isQuestionsAddedObserver());
    }

    public void start(){
        getFireBaseRepository()
                .getGamePlayObservable(getRoomID(),getGamePlayCollection())
                .subscribe(getGamePlayObserver());
    }

    @Override
    public void nextQuestion() {
        questionIndex++;
        question.setValue(questions.get(questionIndex));
    }

    @Override
    public SingleObserver<GamePlay> getGamePlayObserver() {
        return new SingleObserver<GamePlay>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                gamePlayDisposable = d;
            }

            @Override
            public void onSuccess(@NonNull GamePlay game) {
                QuestionsModeGamePlayMD gamePlay = (QuestionsModeGamePlayMD) game;
                setPlayers(gamePlay.getPlayers());
                questions = gamePlay.getQuestions().get(getEnemy().getPlayerID());
                gameFlowObservable(getGamePlayCollection()).subscribe(gameFlowObserver());
                question.setValue(questions.get(questionIndex));
                gamePlayDisposable.dispose();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }
        };
    }


    public Completable isQuestionsAddedObservable() {
        return Completable.create(emitter -> {
            isQuestionsAddedRegistration = FirebaseFirestore.getInstance()
                    .collection(getGamePlayCollection())
                    .document(getRoomID())
                    .addSnapshotListener((value, error) -> {
                        assert value != null;
                        QuestionsModeGamePlayMD gamePlay = value.toObject(QuestionsModeGamePlayMD.class);
                        if (Objects.requireNonNull(gamePlay).getIsQuestionsAdded() == 2) {
                            emitter.onComplete();
                        }
                    });
        });
    }

    public CompletableObserver isQuestionsAddedObserver(){
        return new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                getCompositeDisposable().add(d);
            }

            @Override
            public void onComplete() {
                isQuestionsAdded.setValue("");
                isQuestionsAddedRegistration.remove();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }
        };
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        isQuestionsAddedRegistration.remove();
        winnerListenerRegistration.remove();
        gameFlowListenerRegistration.remove();
        getCompositeDisposable().dispose();
    }
}
