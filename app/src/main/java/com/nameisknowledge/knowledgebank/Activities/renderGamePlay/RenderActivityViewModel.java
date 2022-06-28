package com.nameisknowledge.knowledgebank.Activities.renderGamePlay;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Constants.UserConstants;
import com.nameisknowledge.knowledgebank.FireBaseRepository;
import com.nameisknowledge.knowledgebank.ModelClasses.EmitterQuestion;
import com.nameisknowledge.knowledgebank.ModelClasses.GamePlayMD;
import com.nameisknowledge.knowledgebank.ModelClasses.PlayerMD;
import com.nameisknowledge.knowledgebank.ModelClasses.ResponseMD;
import com.nameisknowledge.knowledgebank.MyApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RenderActivityViewModel extends ViewModel {
    private final String senderName,senderId;
    private final FireBaseRepository fireBaseRepository;
    private ListenerRegistration registration;
    private final List<EmitterQuestion> questions = new ArrayList<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<String> responseListener = new MutableLiveData<>();
    public final MutableLiveData<String> responseObj = new MutableLiveData<>();
    public final MutableLiveData<String> timeOut = new MutableLiveData<>();

    public RenderActivityViewModel(String senderName,String senderId) {
        this.senderName = senderName;
        this.senderId = senderId;
        this.fireBaseRepository = FireBaseRepository.getInstance();
    }

    public void init(){
        if (senderName!=null){
            fireBaseRepository.generateEmitterQuestionsObservable().subscribe(generateEmitterQuestionsObserver());
        }else {
            responsesListenerObservable(UserConstants.getCurrentUser(MyApplication.getContext()).getUid()).subscribe(responsesListenerObserver());
            timer();
        }
    }

    private SingleObserver<String> generateGamePlayObserve(){
        return new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull String s) {
                fireBaseRepository.generateResponseObservable(s,senderId).subscribe(generateResponseObserver());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        };
    }

    private Observer<EmitterQuestion[]> generateEmitterQuestionsObserver(){
        return new Observer<EmitterQuestion[]>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(EmitterQuestion @NonNull [] emitterQuestions) {
                questions.addAll(Arrays.asList(emitterQuestions));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }

            @Override
            public void onComplete() {
                fireBaseRepository
                        .generateGamePlayObservable(prepareGamePlay())
                        .subscribe(generateGamePlayObserve());
            }
        };
    }

    private SingleObserver<String> generateResponseObserver(){
        return new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull String roomID) {
                responseObj.setValue(roomID);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }
        };
    }

    public Observer<String> responsesListenerObserver(){
        return new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@NonNull String roomID) {
                responseListener.setValue(roomID);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private Observable<String> responsesListenerObservable(String player) {
        return Observable.create((ObservableOnSubscribe<List<DocumentChange>>) emitter -> {
            CollectionReference container = FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.RESPONSES_COLLECTION)
                    .document(player)
                    .collection(FirebaseConstants.CONTAINER_COLLECTION);
            registration = container.addSnapshotListener((value, error) -> {
                emitter.onNext(Objects.requireNonNull(value).getDocumentChanges());
            });}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .filter(documentChanges -> documentChanges.size() == 1
                && !(documentChanges.get(0).getDocument().toObject(ResponseMD.class).getRoomID().isEmpty())
                && documentChanges.get(0).getType() == DocumentChange.Type.ADDED)
                .map(documentChanges -> documentChanges.get(0).getDocument().toObject(ResponseMD.class).getRoomID());
    }


    public GamePlayMD prepareGamePlay(){
        PlayerMD player = new PlayerMD(UserConstants.getCurrentUser(MyApplication.getContext()).getUsername(),UserConstants.getCurrentUser(MyApplication.getContext()).getUid(),0);
        PlayerMD enemy = new PlayerMD(senderName,senderId,0);
        return new GamePlayMD(questions,player,enemy);
    }

    public void timer() {
        compositeDisposable.add(
        Completable.timer(13, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    timeOut.setValue("timeOut");
                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (registration!=null){
            registration.remove();
        }
        compositeDisposable.dispose();
    }
}
