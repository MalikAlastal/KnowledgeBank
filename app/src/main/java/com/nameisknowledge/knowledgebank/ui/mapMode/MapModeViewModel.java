package com.nameisknowledge.knowledgebank.ui.mapMode;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nameisknowledge.knowledgebank.FireBaseRepository;
import com.nameisknowledge.knowledgebank.modelClasses.MapAreaMD;
import com.nameisknowledge.knowledgebank.modelClasses.UserMD;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class MapModeViewModel extends ViewModel {
    private final FireBaseRepository fireBaseRepository;
    public final MutableLiveData<List<MapAreaMD>> mapAreas = new MutableLiveData<>();
    public final MutableLiveData<UserMD> updatedUser = new MutableLiveData<>();
    public final MutableLiveData<Integer> attackPoints = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MapModeViewModel() {
        fireBaseRepository = FireBaseRepository.getInstance();
    }

    public void getMapAreas(){
        fireBaseRepository.getMapAreasObservable()
                .subscribe(getMapAreasObserver());
    }

    public void updatedAttackPoints(String id,int points){
        fireBaseRepository.updateAreaAttackPoints(id,points)
                .subscribe(updatedUserObserver());
    }

    public void getAreaAttackPoints(String id){
        fireBaseRepository.getAreaAttackPointsObservable(id)
                .subscribe(getAreaAttackPointsObserver());
    }


    private SingleObserver<List<MapAreaMD>> getMapAreasObserver(){
        return new SingleObserver<List<MapAreaMD>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull List<MapAreaMD> mapAreaMDS) {
                mapAreas.setValue(mapAreaMDS);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }
        };
    }

    private SingleObserver<Integer> getAreaAttackPointsObserver(){
        return new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Integer points) {
                attackPoints.setValue(points);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }
        };
    }

    private SingleObserver<UserMD> updatedUserObserver(){
        return new SingleObserver<UserMD>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull UserMD userMD) {
                updatedUser.setValue(userMD);
                attackPoints.setValue(userMD.getAreaAttackPoints());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }
        };
    }
}
