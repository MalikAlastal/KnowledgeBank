package com.nameisknowledge.knowledgebank.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.ModelClasses.EmitterQuestion;
import com.nameisknowledge.knowledgebank.ModelClasses.GamePlayMD;
import com.nameisknowledge.knowledgebank.ModelClasses.ResponseMD;
import com.nameisknowledge.knowledgebank.Services.CounterService;
import com.nameisknowledge.knowledgebank.databinding.ActivityRenderGamePlayBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RenderGamePlayActivity extends AppCompatActivity {
    private static final String TAG = "RenderGamePlayActivity";
    private ActivityRenderGamePlayBinding binding;
    private String senderID;
    private int resSize;
    private final List<EmitterQuestion> duplicatedQuestions = new ArrayList<>();
    private final List<Integer> indexes = new ArrayList<>();

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
            generateQuestions(gamePlayMD ->{
                generateGamePlay(this, gamePlayMD, s -> {
                    generateResponse(this, s);
                });
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void generateQuestions(GenericListener<GamePlayMD> listener) {
        Observable.zip(
                Observable.create((ObservableOnSubscribe<EmitterQuestion>) emitter -> {
                    FirebaseFirestore.getInstance()
                            .collection("Questions")
                            .whereEqualTo("category", "Sciences")
                            .get()
                            .addOnSuccessListener(documentSnapshots -> {
                                List<Integer> list = new ArrayList<>();
                                for (int i = 0; i < 4; i++) {
                                    int x = new Random().nextInt(((documentSnapshots.getDocuments().size() - 1)) + 1);
                                    if (list.contains(x)) {
                                        while (list.contains(x)) {
                                            x = new Random().nextInt(((documentSnapshots.getDocuments().size() - 1)) + 1);
                                        }
                                    }
                                    list.add(x);
                                    emitter.onNext(new EmitterQuestion("Sciences", list.get(i)));
                                }
                                emitter.onComplete();
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, e.getMessage());
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }),
                Observable.create((ObservableOnSubscribe<EmitterQuestion>) emitter -> {
                    FirebaseFirestore.getInstance()
                            .collection("Questions")
                            .whereEqualTo("category", "Geography")
                            .get()
                            .addOnSuccessListener(documentSnapshots -> {
                                List<Integer> list = new ArrayList<>();
                                for (int i = 0; i < 4; i++) {
                                    int x = new Random().nextInt(((documentSnapshots.getDocuments().size() - 1)) + 1);
                                    if (list.contains(x)) {
                                        while (list.contains(x)) {
                                            x = new Random().nextInt(((documentSnapshots.getDocuments().size() - 1)) + 1);
                                        }
                                    }
                                    list.add(x);
                                    emitter.onNext(new EmitterQuestion("Geography", list.get(i)));
                                }
                                emitter.onComplete();
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, e.getMessage());
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }),
                (a, b) -> new EmitterQuestion[]{a, b})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    Log.d(TAG,duplicatedQuestions.size()+" dup");
                    Observable.fromIterable(duplicatedQuestions)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(Schedulers.computation())
                            .subscribe(emitterQuestion -> {
                                FirebaseFirestore.getInstance()
                                        .collection("Questions")
                                        .whereEqualTo("category", emitterQuestion.getTag())
                                        .get()
                                        .addOnSuccessListener(documentSnapshots -> {
                                            int x = new Random().nextInt(((documentSnapshots.getDocuments().size() - 1)) + 1);
                                            while (indexes.contains(x)) {
                                                x = new Random().nextInt(((documentSnapshots.getDocuments().size() - 1)) + 1);
                                            }
                                            indexes.add(x);
                                            Log.d(TAG,x+"x");
                                            if (indexes.size() == 8){
                                                // here we go !!
                                                Map<String,Integer> map = new HashMap<>();
                                                map.put(FirebaseAuth.getInstance().getUid(),0);
                                                map.put(FirebaseAuth.getInstance().getUid(),0);
                                                listener.getData(new GamePlayMD(map,indexes));
                                            }
                                        })
                                        .addOnFailureListener(e->{
                                            Log.d(TAG,e.getMessage());
                                        });
                            });
                })
                .subscribe(emitterQuestions -> {
                    for (EmitterQuestion j : emitterQuestions) {
                        Log.d(TAG,j.getIndex()+"");
                        if (!indexes.contains(j.getIndex())) {
                            indexes.add(j.getIndex());
                        } else {
                            duplicatedQuestions.add(j);
                        }
                    }
                });
    }

    private void generateGamePlay(Context context, GamePlayMD gamePlayMD, GenericListener<String> listener) {
        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.GAME_PLAY_COLLECTION)
                .add(gamePlayMD)
                .addOnSuccessListener(documentReference -> listener.getData(documentReference.getId()))
                .addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void generateResponse(Context context, String roomID) {
        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.RESPONSES_COLLECTION)
                .document(senderID)
                .collection(FirebaseConstants.CONTAINER_COLLECTION)
                .add(new ResponseMD(roomID, FirebaseAuth.getInstance().getUid()))
                .addOnSuccessListener(documentReference -> documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    context.startActivity(new Intent(context, DuoModeActivity.class)
                            .putExtra("roomID", documentSnapshot.getString("roomID"))
                            .putExtra("senderID", senderID));
                    finish();
                }).addOnFailureListener(e -> Log.d("Error", e.getMessage()))).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
    }


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
}