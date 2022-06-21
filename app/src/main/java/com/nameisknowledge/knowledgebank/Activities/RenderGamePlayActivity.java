package com.nameisknowledge.knowledgebank.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.ModelClasses.EmitterQuestion;
import com.nameisknowledge.knowledgebank.ModelClasses.GamePlayMD;
import com.nameisknowledge.knowledgebank.ModelClasses.ResponseMD;
import com.nameisknowledge.knowledgebank.Services.CounterService;
import com.nameisknowledge.knowledgebank.databinding.ActivityRenderGamePlayBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RenderGamePlayActivity extends AppCompatActivity {
    private final String TAG = "RenderGamePlayActivity";
    private ActivityRenderGamePlayBinding binding;
    private String senderID;
    private final List<EmitterQuestion> indexes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRenderGamePlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        senderID = getIntent().getStringExtra("senderId");

        if (senderID == null) {
            responsesListener();
            CounterService.startAction(this, unused -> {
                if (binding != null) {
                    Toast.makeText(RenderGamePlayActivity.this, "Time Out!!", Toast.LENGTH_SHORT).show();
                }
                finish();
            });
        }

        if (senderID != null) {
            generateQuestions()
                    .doOnComplete(()->{
                        Map<String,Integer> playersIds = new HashMap<>();
                        playersIds.put(FirebaseAuth.getInstance().getUid(),0);
                        playersIds.put(senderID,0);
                        generateGamePlay(new GamePlayMD(playersIds,indexes));
                    }).subscribe(emitterQuestions -> indexes.addAll(Arrays.asList(emitterQuestions)));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
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
                            .collection("QuestionsContainer")                            .get()
                            .addOnSuccessListener(documentSnapshots -> {
                                List<Integer> list = new ArrayList<>();
                                for (int i = 0; i < 3; i++) {
                                    int x = new Random().nextInt(documentSnapshots.getDocuments().size());                                    if (list.contains(x)) {
                                        while (list.contains(x)) {
                                            x = new Random().nextInt(documentSnapshots.getDocuments().size());                                        }
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
                            .collection("QuestionsContainer")                                .get()
                            .addOnSuccessListener(documentSnapshots -> {
                                List<Integer> list = new ArrayList<>();
                                for (int i = 0; i < 3; i++) {
                                    int x = new Random().nextInt(documentSnapshots.getDocuments().size());                                    if (list.contains(x)) {
                                        while (list.contains(x)) {
                                            x = new Random().nextInt(documentSnapshots.getDocuments().size());                                        }
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


    private void generateGamePlay(GamePlayMD gamePlayMD) {
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                    .add(gamePlayMD)
                    .addOnSuccessListener(documentReference -> {
                       emitter.onNext(documentReference.getId());
                    });
        }).subscribe(this::generateResponse);
    }

    private void generateResponse(String roomID) {
        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.RESPONSES_COLLECTION)
                .document(senderID)
                .collection(FirebaseConstants.CONTAINER_COLLECTION)
                .add(new ResponseMD(roomID, FirebaseAuth.getInstance().getUid()))
                .addOnSuccessListener(documentReference -> documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    startActivity(new Intent(this, DuoModeActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra("roomID", documentSnapshot.getString("roomID"))
                            .putExtra("senderID", senderID));
                    finish();
                }));
    }


    /*
    private void responsesListener() {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.RESPONSES_COLLECTION)
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection(FirebaseConstants.CONTAINER_COLLECTION)
                .addSnapshotListener((value, error) -> {
                    if (error == null) {
                        if (value != null) {
                            if (resSize == 0) {
                                resSize = value.getDocumentChanges().size();
                            }
                            if (value.getDocumentChanges().size() == 1 && value.getDocumentChanges().get(0).getType() != DocumentChange.Type.REMOVED && resSize != 1) {
                                startActivity(new Intent(getApplicationContext(), DuoModeActivity.class)
                                        .putExtra("roomID", value.getDocumentChanges().get(0).getDocument().getString("roomID"))
                                        .putExtra("senderID", value.getDocumentChanges().get(0).getDocument().getString("userID")));
                                finish();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
     */

    private void responsesListener() {
        Observable.create((ObservableOnSubscribe<List<DocumentChange>>) emitter -> {
            FirebaseFirestore.getInstance().collection(FirebaseConstants.RESPONSES_COLLECTION)
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .collection(FirebaseConstants.CONTAINER_COLLECTION)
                    .addSnapshotListener((value, error) -> {
                        Log.d(TAG, Objects.requireNonNull(value).getDocumentChanges().size() + " size");
                        emitter.onNext(Objects.requireNonNull(value).getDocumentChanges());
                    });
        }).filter(documentChanges -> documentChanges.size() == 1 && !(documentChanges.get(0).getDocument().toObject(ResponseMD.class).getRoomID().isEmpty()))
                .map(documentChanges -> documentChanges.get(0).getDocument().toObject(ResponseMD.class))
                .subscribe(responseMD -> {
                    Log.d(TAG, responseMD.getRoomID());
                    startActivity(new Intent(getApplicationContext(), DuoModeActivity.class)
                            .putExtra("roomID", responseMD.getRoomID())
                            .putExtra("senderID", responseMD.getUserID()));
                    finish();
                });
    }

}
