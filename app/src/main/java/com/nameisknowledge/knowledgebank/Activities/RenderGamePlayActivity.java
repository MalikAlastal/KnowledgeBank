package com.nameisknowledge.knowledgebank.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.nameisknowledge.knowledgebank.Activities.duoMode.DuoModeActivity;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Constants.UserConstants;
import com.nameisknowledge.knowledgebank.ModelClasses.EmitterQuestion;
import com.nameisknowledge.knowledgebank.ModelClasses.GamePlayMD;
import com.nameisknowledge.knowledgebank.ModelClasses.ResponseMD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RenderGamePlayActivity extends AppCompatActivity {
    private final String TAG = "RenderGamePlayActivity";
    private ActivityRenderGamePlayBinding binding;
    private String senderName;
    private ListenerRegistration registration;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final List<EmitterQuestion> indexes = new ArrayList<>();
    private CollectionReference container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRenderGamePlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        senderName = getIntent().getStringExtra("senderName");

        if (senderName == null) {
            compositeDisposable.add(
                    responsesListener()
                            .filter(documentChanges -> documentChanges.size() == 1
                                    && !(documentChanges.get(0).getDocument().toObject(ResponseMD.class).getRoomID().isEmpty())
                                    && documentChanges.get(0).getType() == DocumentChange.Type.ADDED)
                            .map(documentChanges -> documentChanges.get(0).getDocument().toObject(ResponseMD.class))
                            .subscribe(responseMD -> {
                                startActivity(new Intent(this, DuoModeActivity.class).putExtra("responseMD", responseMD));
                                finish();
                            }));

            compositeDisposable.add(
                    timer()
                            .subscribe(() -> {
                                Toast.makeText(this, "time out", Toast.LENGTH_SHORT).show();
                                finish();
                            })
            );
        }

        if (senderName != null) {
            // here we generate questions indexes
            compositeDisposable.add(
                    generateQuestions()
                            // here after we added indexes to the list we  will generate the gamePlay
                            .doOnComplete(() -> {
                                // we setUp gamePlay components {Map<PLayerId,hisScore > ,questionsIndexes}
                                // here we pass values to the method to generate the gamePlay
                                compositeDisposable.add(
                                        generateGamePlay(new GamePlayMD(indexes,UserConstants.getCurrentUser(this).getUsername(),senderName))
                                                .subscribe(roomId -> {
                                                    // after we generate the gamePlay here we want to get the gamePlay (roomId) to send id to
                                                    // the other player (user) by his response to start the game
                                                    compositeDisposable.add(
                                                            generateResponse(roomId)
                                                                    // after we generate the response and sent it to the other player
                                                                    // will finish this activity and start gameActivity
                                                                    .subscribe(responseMD -> {
                                                                        startActivity(new Intent(this, DuoModeActivity.class)
                                                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                                .putExtra("responseMD", responseMD));
                                                                        finish();
                                                                    }));
                                                }));
                            })
                            // here we get all index generated from the zip operator the we add to indexes list
                            .subscribe(emitterQuestions -> indexes.addAll(Arrays.asList(emitterQuestions))));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.compositeDisposable.clear();
        if (this.registration != null) {
            this.registration.remove();
        }
    }

    private Observable<EmitterQuestion[]> generateQuestions() {
        return Observable.zip(
                Observable.create((ObservableOnSubscribe<EmitterQuestion>) emitter -> {
                    FirebaseFirestore.getInstance()
                            .collection("Questions")
                            .document("Medium")
                            .collection("QuestionsContainer")
                            .get()
                            .addOnSuccessListener(documentSnapshots -> {
                                List<Integer> list = new ArrayList<>();
                                for (int i = 0; i < 3; i++) {
                                    int x = new Random().nextInt(documentSnapshots.getDocuments().size());
                                    if (list.contains(x)) {
                                        while (list.contains(x)) {
                                            x = new Random().nextInt(documentSnapshots.getDocuments().size());
                                        }
                                    }
                                    list.add(x);
                                    emitter.onNext(new EmitterQuestion("Medium", list.get(i)));
                                }
                                emitter.onComplete();
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, e.getMessage());
                            });
                }),
                Observable.create((ObservableOnSubscribe<EmitterQuestion>) emitter -> {
                    FirebaseFirestore.getInstance()
                            .collection("Questions")
                            .document("Easy")
                            .collection("QuestionsContainer")
                            .get()
                            .addOnSuccessListener(documentSnapshots -> {
                                List<Integer> list = new ArrayList<>();
                                for (int i = 0; i < 3; i++) {
                                    int x = new Random().nextInt(documentSnapshots.getDocuments().size());
                                    if (list.contains(x)) {
                                        while (list.contains(x)) {
                                            x = new Random().nextInt(documentSnapshots.getDocuments().size());
                                        }
                                    }
                                    list.add(x);
                                    emitter.onNext(new EmitterQuestion("Easy", list.get(i)));
                                }
                                emitter.onComplete();
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, e.getMessage());
                            });
                }),
                Observable.create((ObservableOnSubscribe<EmitterQuestion>) emitter -> {
                    FirebaseFirestore.getInstance()
                            .collection("Questions")
                            .document("Hard")
                            .collection("QuestionsContainer")
                            .get()
                            .addOnSuccessListener(documentSnapshots -> {
                                List<Integer> list = new ArrayList<>();
                                for (int i = 0; i < 3; i++) {
                                    int x = new Random().nextInt(documentSnapshots.getDocuments().size());
                                    if (list.contains(x)) {
                                        while (list.contains(x)) {
                                            x = new Random().nextInt(documentSnapshots.getDocuments().size());
                                        }
                                    }
                                    list.add(x);
                                    emitter.onNext(new EmitterQuestion("Hard", list.get(i)));
                                }
                                emitter.onComplete();
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, e.getMessage());
                            });
                }),
                (easy, medium, hard) -> new EmitterQuestion[]{easy, medium, hard})
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    private Single<String> generateGamePlay(GamePlayMD gamePlayMD) {
        return Single.create((SingleOnSubscribe<String>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .add(gamePlayMD)
                    .addOnSuccessListener(documentReference -> {
                        emitter.onSuccess(documentReference.getId());
                    });})
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    private Single<ResponseMD> generateResponse(String roomID) {
        return Single.create((SingleOnSubscribe<ResponseMD>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.RESPONSES_COLLECTION)
                    .document(senderName)
                    .collection(FirebaseConstants.CONTAINER_COLLECTION)
                    .add(new ResponseMD(roomID,senderName))
                    .addOnSuccessListener(response -> {
                        response.get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    emitter.onSuccess(documentSnapshot.toObject(ResponseMD.class));
                                });
                    });
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    private @NonNull Observable<List<DocumentChange>> responsesListener() {
        return Observable.create((ObservableOnSubscribe<List<DocumentChange>>) emitter -> {
            container = FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.RESPONSES_COLLECTION)
                    .document(UserConstants.getCurrentUser(this).getUsername())
                    .collection(FirebaseConstants.CONTAINER_COLLECTION);
                registration = container.addSnapshotListener((value, error) -> {
                emitter.onNext(Objects.requireNonNull(value).getDocumentChanges());
            });})
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    private Completable timer() {
        return Completable.timer(7, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
