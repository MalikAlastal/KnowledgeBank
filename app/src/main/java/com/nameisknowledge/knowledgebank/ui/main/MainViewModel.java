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
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;

public class MainViewModel extends ViewModel {
    private final FireBaseRepository fireBaseRepository;
    private final List<UserMD> list = new ArrayList<>();
    public final MutableLiveData<List<UserMD>> users = new MutableLiveData<>();
    private Disposable disposable;


    public MainViewModel() {
        fireBaseRepository = FireBaseRepository.getInstance();
    }

    public void getPlayers(String mode){
        fireBaseRepository.getHighRankedPlayers(mode)
                .flatMap((Function<String, ObservableSource<UserMD>>) id -> fireBaseRepository.getUserById(id,list.size()))
                .subscribe(getHighRankedPlayers());
    }

    private Observer<UserMD> getHighRankedPlayers(){
        return new Observer<UserMD>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
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
                disposable.dispose();
            }
        };
    }
}
