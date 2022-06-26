package com.nameisknowledge.knowledgebank;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Constants.UserConstants;
import com.nameisknowledge.knowledgebank.ModelClasses.EmitterQuestion;
import com.nameisknowledge.knowledgebank.ModelClasses.GamePlayMD;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.ModelClasses.ResponseMD;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FireBaseRepository {
    private final ResponseMD responseMD;
    private static FireBaseRepository instance;

    public static FireBaseRepository getInstance(ResponseMD responseMD) {
        if (instance == null) {
            instance = new FireBaseRepository(responseMD);
        }
        return instance;
    }

    private FireBaseRepository(ResponseMD responseMD) {
        this.responseMD = responseMD;
    }

    public void setTheWinner(String winner) {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(responseMD.getRoomID())
                .update("winner", winner);
    }

    public void setTheScore(String player) {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .document(responseMD.getRoomID())
                .update("players" + "." + player, FieldValue.increment(10));
    }

    public Single<String> endTheGameObservable(){
        return Single.create((SingleOnSubscribe<String>) emitter->{
            FirebaseFirestore.getInstance().collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .document(responseMD.getRoomID())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        long playerScore = (long) documentSnapshot.get("players" + "." + UserConstants.getCurrentUser(MyApplication.getContext()).getUsername());
                        long enemy = (long) documentSnapshot.get("players" + "." + responseMD.getSenderName());
                        if (playerScore > enemy){
                            emitter.onSuccess(UserConstants.getCurrentUser(MyApplication.getContext()).getUsername());
                        }else {
                            emitter.onSuccess(responseMD.getSenderName());
                        }
                    }).addOnFailureListener(emitter::onError);
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    public Completable questionAnsweredObservable(int number) {
        return Completable.create(emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .document(responseMD.getRoomID())
                    .update("currentQuestion",number)
                    .addOnSuccessListener(unused -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<EmitterQuestion>> getQuestionsIndexesObservable() {
        return Single.create((SingleOnSubscribe<List<EmitterQuestion>>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .document(responseMD.getRoomID())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        emitter.onSuccess(Objects.requireNonNull(documentSnapshot.toObject(GamePlayMD.class)).getIndex());
                    }).addOnFailureListener(emitter::onError);
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
}
