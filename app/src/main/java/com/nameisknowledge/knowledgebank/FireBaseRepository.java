package com.nameisknowledge.knowledgebank;


import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nameisknowledge.knowledgebank.constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.modelClasses.EmitterQuestion;
import com.nameisknowledge.knowledgebank.modelClasses.MapAreaMD;
import com.nameisknowledge.knowledgebank.modelClasses.PlayerScoreMD;
import com.nameisknowledge.knowledgebank.modelClasses.UserMD;
import com.nameisknowledge.knowledgebank.modelClasses.gamePlay.GamePlay;
import com.nameisknowledge.knowledgebank.modelClasses.gamePlay.QuestionsModeGamePlayMD;
import com.nameisknowledge.knowledgebank.modelClasses.gamePlay.DuoModeGamePlayMD;
import com.nameisknowledge.knowledgebank.modelClasses.PlayerMD;
import com.nameisknowledge.knowledgebank.modelClasses.questions.FireBaseQuestionMD;
import com.nameisknowledge.knowledgebank.modelClasses.ResponseMD;

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
    public synchronized static FireBaseRepository getInstance() {
        if (instance == null) {
            instance = new FireBaseRepository();
        }
        return instance;
    }

    // *************** DuoModeActivity stuff *************** ////
    public void setTheWinner(String winner,String roomID,String gamePlayCollection) {
        FirebaseFirestore.getInstance().collection(gamePlayCollection)
                .document(roomID)
                .update("winner", winner);
    }

    public void setFinalPlayerScore(String mode,String playerID){
        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.SCORES_COLLECTION)
                .document(playerID)
                .update(mode,FieldValue.increment(1));
    }

    public void setTheScore(String playerName,String roomID,String gamePlayCollection) {
        FirebaseFirestore.getInstance().collection(gamePlayCollection)
                .document(roomID)
                .update("scores"+"."+playerName, FieldValue.increment(1));
    }

    public Single<PlayerMD> finishTheGameObservable(PlayerMD player,PlayerMD enemy,String roomID,String gamePlayCollection) {
        return Single.create((SingleOnSubscribe<PlayerMD>) emitter -> {
            FirebaseFirestore.getInstance().collection(gamePlayCollection)
                    .document(roomID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        long playerScore = (long) documentSnapshot.get("scores" + "." + player.getPlayerName());
                        long enemyScore = (long) documentSnapshot.get("scores" + "." + enemy.getPlayerName());
                        if (playerScore > enemyScore) {
                            emitter.onSuccess(player);
                        } else {
                            emitter.onSuccess(enemy);
                        }
                    }).addOnFailureListener(emitter::onError);
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    public Completable questionAnsweredObservable(int number,String roomID,String gamePlayCollection) {
        return Completable.create(emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(gamePlayCollection)
                    .document(roomID)
                    .update("currentQuestion", number)
                    .addOnSuccessListener(unused -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<GamePlay> getGamePlayObservable(String roomID,String gamePlayCollection){
        return Single.create((SingleOnSubscribe<GamePlay>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(gamePlayCollection)
                    .document(roomID)
                    .get()
                    .addOnSuccessListener(gamePlay->{
                        if (gamePlayCollection.equals(FirebaseConstants.GAME_PLAY_2_COLLECTION)){
                            emitter.onSuccess(gamePlay.toObject(QuestionsModeGamePlayMD.class));
                        }else {
                            emitter.onSuccess(gamePlay.toObject(DuoModeGamePlayMD.class));
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<FireBaseQuestionMD> getQuestionObservable(EmitterQuestion emitterQuestion) {
        return Single.create((SingleOnSubscribe<FireBaseQuestionMD>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.QUESTIONS_COLLECTION)
                    .document(emitterQuestion.getTag())
                    .collection(FirebaseConstants.QUESTIONS_CONTAINER)
                    .document(String.valueOf(emitterQuestion.getIndex()))
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        FireBaseQuestionMD fireBaseQuestionMD = documentSnapshot.toObject(FireBaseQuestionMD.class);
                        emitter.onSuccess(fireBaseQuestionMD);
                    }).addOnFailureListener(emitter::onError);
        }).subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread());
    }
    // ********************************************************************* //

    //// *************** RenderActivity stuff *************** ////
    public Single<ResponseMD> generateResponseObservable(String roomID,String senderId,String mode) {
        return Single.create((SingleOnSubscribe<ResponseMD>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.RESPONSES_COLLECTION)
                    .document(senderId)
                    .collection(FirebaseConstants.CONTAINER_COLLECTION)
                    .add(new ResponseMD(roomID,mode))
                    .addOnSuccessListener(response -> {
                        response.get().addOnSuccessListener(documentSnapshot -> {
                           emitter.onSuccess(Objects.requireNonNull(documentSnapshot.toObject(ResponseMD.class)));
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

    public Single<String> generateGamePlay2Observable(PlayerMD player, PlayerMD enemy){
        return Single.create((SingleOnSubscribe<String>)  emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.GAME_PLAY_2_COLLECTION)
                    .add(new QuestionsModeGamePlayMD(player,enemy))
                    .addOnSuccessListener(documentReference -> emitter.onSuccess(documentReference.getId()))
                    .addOnFailureListener(emitter::onError);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<String> generateGamePlayObservable(DuoModeGamePlayMD duoModeGamePlayMD) {
        return Single.create((SingleOnSubscribe<String>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .add(duoModeGamePlayMD)
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

    //************SoloMode stuff*******************************************//
    public Single<FireBaseQuestionMD> generateRandomQuestionObservable(){
        return Single.create((SingleOnSubscribe<FireBaseQuestionMD>) emitter->{
            String[] levels = {"Hard","Easy","Medium"};
            int levelIndex = new Random().nextInt(levels.length);
            int questionIndex = new Random().nextInt(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getInt(levels[levelIndex],0));
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.QUESTIONS_COLLECTION)
                    .document(levels[levelIndex])
                    .collection(FirebaseConstants.QUESTIONS_CONTAINER)
                    .document(questionIndex+"")
                    .get()
                    .addOnSuccessListener(question->{
                        emitter.onSuccess(question.toObject(FireBaseQuestionMD.class));
                    });
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }
    // ********************************************************************* //

    //************Main Activity stuff*******************************************//
    public Observable<String> getHighRankedPlayers(String mode) {
        return Observable.create((ObservableOnSubscribe<List<PlayerScoreMD>>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.SCORES_COLLECTION)
                    .orderBy(mode, Query.Direction.DESCENDING)
                    .limit(3)
                    .get()
                    .addOnSuccessListener(documentSnapshots -> {
                        emitter.onNext(documentSnapshots.toObjects(PlayerScoreMD.class));
                        emitter.onComplete();
                    })
                    .addOnFailureListener(emitter::onError);
        }).flatMap(Observable::fromIterable).map(PlayerScoreMD::getId);
    }

    public Single<UserMD> getUserById(String id) {
        return Single.create((SingleOnSubscribe<UserMD>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.USERS_COLLECTION)
                    .document(id)
                    .get()
                    .addOnSuccessListener(documentSnapshots -> {
                        emitter.onSuccess(documentSnapshots.toObject(UserMD.class));
                    })
                    .addOnFailureListener(emitter::onError);
        }).subscribeOn(Schedulers.io());
    }

    // to get High ranked player (because i have to check playersList size = 3)
    public Observable<UserMD> getUserById(String id,int size) {
        return Observable.create((ObservableOnSubscribe<UserMD>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.USERS_COLLECTION)
                    .document(id)
                    .get()
                    .addOnSuccessListener(documentSnapshots -> {
                        emitter.onNext(documentSnapshots.toObject(UserMD.class));
                        if (size == 3){
                            emitter.onComplete();
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        }).subscribeOn(Schedulers.io());
    }

    // ********************************************************************* //
    //************ Login Activity stuff *******************************************//

    public Single<UserMD> loginObservable(String email,String pass){
        return Single.create((SingleOnSubscribe<String>) emitter -> {
            FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email,pass)
            .addOnSuccessListener(authResult -> emitter.onSuccess(Objects.requireNonNull(authResult.getUser()).getUid()))
            .addOnFailureListener(emitter::onError);
        }).flatMap(this::getUserById).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<String> registerObservable(UserMD userMD){
        return Single.create((SingleOnSubscribe<UserMD>) emitter -> {
            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(userMD.getEmail(),userMD.getPassword())
                    .addOnSuccessListener(authResult -> {
                        userMD.setUid(Objects.requireNonNull(authResult.getUser()).getUid());
                        emitter.onSuccess(userMD);
                    })
                    .addOnFailureListener(emitter::onError);
        }).flatMap(this::addNewUser).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    public void updateToken(String token){
        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.USERS_COLLECTION)
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .update("notificationToken", token);
    }

    public Single<String> addNewUser(UserMD userMD){
        return Single.create((SingleOnSubscribe<String>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.USERS_COLLECTION)
                    .document(userMD.getUid())
                    .set(userMD)
                    .addOnSuccessListener(us->emitter.onSuccess(userMD.getUid()))
                    .addOnFailureListener(emitter::onError);
        }).subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread());
    }

    public void setDefaultPlayerScore(String id){
        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.SCORES_COLLECTION)
                .document(id)
                .set(new PlayerScoreMD(id,0,0,0));
    }

    public void setDefaultResponse(String id){
        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.RESPONSES_COLLECTION)
                .document(id)
                .collection(FirebaseConstants.CONTAINER_COLLECTION)
                .add(new ResponseMD("",""));
    }

    // ********************************************************************* //
    //************ Map Mode Activity stuff *******************************************//

    public Single<List<MapAreaMD>> getMapAreasObservable(){
        return Single.create((SingleOnSubscribe<List<MapAreaMD>>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.MAP_AREAS_COLLECTION)
                    .get()
                    .addOnSuccessListener(documentSnapshots -> emitter.onSuccess(documentSnapshots.toObjects(MapAreaMD.class)))
                    .addOnFailureListener(emitter::onError);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<UserMD> updateAreaAttackPoints(String id,int points){
        return Single.create((SingleOnSubscribe<String>) emitter->{
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.USERS_COLLECTION)
                    .document(id)
                    .update("areaAttackPoints",FieldValue.increment(points))
                    .addOnSuccessListener(unused -> emitter.onSuccess(id))
                    .addOnFailureListener(emitter::onError);
        }).flatMap(this::getUserById).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<UserMD> updateAreaAttackPointsV2(String id,int points){
        return Single.create((SingleOnSubscribe<String>) emitter->{
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.USERS_COLLECTION)
                    .document(id)
                    .update("areaAttackPoints",points)
                    .addOnSuccessListener(unused -> emitter.onSuccess(id))
                    .addOnFailureListener(emitter::onError);
        }).flatMap(this::getUserById).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Integer> getAreaAttackPointsObservable(String id){
        return Single.create((SingleOnSubscribe<Integer>) emitter->{
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.USERS_COLLECTION)
                    .document(id)
                    .get()
                    .addOnSuccessListener(documentSnapshot ->{
                        emitter.onSuccess(Objects.requireNonNull(documentSnapshot.toObject(UserMD.class)).getAreaAttackPoints());
                    })
                    .addOnFailureListener(emitter::onError);
        }).observeOn(AndroidSchedulers.mainThread());
    }

    // ********************************************************************* //
    //************ Attack Activity stuff *******************************************//

    public Single<UserMD> setAreaOwner(String areaName,UserMD user,int ownerAnsweredQuestionsCount){
        return Single.create((SingleOnSubscribe<UserMD>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.MAP_AREAS_COLLECTION)
                    .document(areaName)
                    .update("ownerUser",user,"ownerAnsweredQuestionsCount",ownerAnsweredQuestionsCount)
                    .addOnSuccessListener(unused->emitter.onSuccess(user))
                    .addOnFailureListener(emitter::onError);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}