package com.nameisknowledge.knowledgebank;


import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.ModelClasses.EmitterQuestion;
import com.nameisknowledge.knowledgebank.ModelClasses.GamePlayMD;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.ModelClasses.ResponseMD;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FireBaseRepository {
    private static FireBaseRepository instance;

    public static FireBaseRepository getInstance() {
        if (instance == null) {
            instance = new FireBaseRepository();
        }
        return instance;
    }

    // *************** DuoModeActivity stuff *************** ////
    public void setTheWinner(String winner,String roomID) {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(roomID)
                .update("winner", winner);
    }

    public void setTheScore(String playerName,String roomID) {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(roomID)
                .update("scores"+"."+playerName, FieldValue.increment(10));
    }

    public Single<String> endTheGameObservable(String player,String enemy,String roomID) {
        return Single.create((SingleOnSubscribe<String>) emitter -> {
            FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .document(roomID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        long playerScore = (long) documentSnapshot.get("scores" + "." + player);
                        long enemyScore = (long) documentSnapshot.get("scores" + "." + enemy);
                        if (playerScore > enemyScore) {
                            emitter.onSuccess(player);
                        } else {
                            emitter.onSuccess(enemy);
                        }
                    }).addOnFailureListener(emitter::onError);
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    public Completable questionAnsweredObservable(int number,String roomID) {
        return Completable.create(emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .document(roomID)
                    .update("currentQuestion", number)
                    .addOnSuccessListener(unused -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<GamePlayMD> getGamePlayObservable(String roomID){
        return Single.create((SingleOnSubscribe<GamePlayMD>)  emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .document(roomID)
                    .get()
                    .addOnSuccessListener(gamePlay->{
                        emitter.onSuccess(gamePlay.toObject(GamePlayMD.class));
                    })
                    .addOnFailureListener(emitter::onError);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<QuestionMD> getQuestionObservable(EmitterQuestion emitterQuestion) {
        return Single.create((SingleOnSubscribe<QuestionMD>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.QUESTIONS_COLLECTION)
                    .document(emitterQuestion.getTag())
                    .collection(FirebaseConstants.QUESTIONS_CONTAINER)
                    .document(String.valueOf(emitterQuestion.getIndex()))
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        QuestionMD questionMD = documentSnapshot.toObject(QuestionMD.class);
                        emitter.onSuccess(questionMD);
                    }).addOnFailureListener(emitter::onError);
        }).subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread());
    }
    // ********************************************************************* //

    //// *************** RenderActivity stuff *************** ////
    public Single<String> generateResponseObservable(String roomID,String senderId) {
        return Single.create((SingleOnSubscribe<String>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.RESPONSES_COLLECTION)
                    .document(senderId)
                    .collection(FirebaseConstants.CONTAINER_COLLECTION)
                    .add(new ResponseMD(roomID))
                    .addOnSuccessListener(response -> {
                        response.get().addOnSuccessListener(documentSnapshot -> {
                           emitter.onSuccess(Objects.requireNonNull(documentSnapshot.toObject(ResponseMD.class)).getRoomID());
                        });
                    });
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<EmitterQuestion[]> generateEmitterQuestionsObservable() {
        return Observable.zip(
                createRandomQuestionsIndexesObservable("Hard"),
                createRandomQuestionsIndexesObservable("Medium"),
                createRandomQuestionsIndexesObservable("Easy"),
                (easy, medium, hard) -> new EmitterQuestion[]{easy, medium, hard})
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public Single<String> generateGamePlayObservable(GamePlayMD gamePlayMD) {
        return Single.create((SingleOnSubscribe<String>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .add(gamePlayMD)
                    .addOnSuccessListener(documentReference -> {
                        emitter.onSuccess(documentReference.getId());
                    });
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    private Observable<EmitterQuestion> createRandomQuestionsIndexesObservable(String hardLeve) {
        return Observable.create((ObservableOnSubscribe<EmitterQuestion>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection("Questions")
                    .document(hardLeve)
                    .collection("QuestionsContainer")
                    .get()
                    .addOnSuccessListener(documentSnapshots -> {
                        List<Integer> list = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            int x = new Random().nextInt(documentSnapshots.getDocuments().size()+1);
                            if (list.contains(x)) {
                                while (list.contains(x)) {
                                    x = new Random().nextInt(documentSnapshots.getDocuments().size());
                                }
                            }
                            list.add(x);
                            emitter.onNext(new EmitterQuestion(hardLeve, list.get(i)));
                        }
                        emitter.onComplete();
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }
    // ********************************************************************* //

    public Single<QuestionMD> generateRandomQuestionObservable(){
        return Single.create((SingleOnSubscribe<QuestionMD>) emitter->{
            String[] levels = {"Hard","Easy","Medium"};
            int levelIndex = new Random().nextInt(levels.length+1);
            int questionIndex = new Random().nextInt(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getInt(levels[levelIndex],0)+1);
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.QUESTIONS_COLLECTION)
                    .document(levels[levelIndex])
                    .collection(FirebaseConstants.QUESTIONS_CONTAINER)
                    .document(questionIndex+"")
                    .get()
                    .addOnSuccessListener(question->{
                        emitter.onSuccess(question.toObject(QuestionMD.class));
                    });
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }
}