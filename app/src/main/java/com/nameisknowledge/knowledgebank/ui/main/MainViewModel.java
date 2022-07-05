package com.nameisknowledge.knowledgebank.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nameisknowledge.knowledgebank.FireBaseRepository;
import com.nameisknowledge.knowledgebank.modelClasses.UserMD;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;

public class MainViewModel extends ViewModel {
    private final FireBaseRepository fireBaseRepository;
    private List<UserMD> list = new ArrayList<>();
    public final MutableLiveData<List<UserMD>> users = new MutableLiveData<>();
    public final MutableLiveData<UserMD> user = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainViewModel() {
        fireBaseRepository = FireBaseRepository.getInstance();
    }

    public void getPlayers(String mode){
        list = new ArrayList<>();
        fireBaseRepository.getHighRankedPlayers(mode)
                .flatMap((Function<String, ObservableSource<UserMD>>) id -> fireBaseRepository.getUserById(id,3))
                .subscribe(getHighRankedPlayers());
    }

    public void getUser(String id){
        fireBaseRepository.getUserById(id)
                .subscribe(getUserByIdObserver());
    }

    private Observer<UserMD> getHighRankedPlayers(){
        return new Observer<UserMD>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@NonNull UserMD userMD) {
                list.add(userMD);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }

            @Override
            public void onComplete() {
                users.setValue(list);
            }
        };
    }

    private SingleObserver<UserMD> getUserByIdObserver(){
        return new SingleObserver<UserMD>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull UserMD userMD) {
                user.setValue(userMD);
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
        compositeDisposable.dispose();
    }
}
