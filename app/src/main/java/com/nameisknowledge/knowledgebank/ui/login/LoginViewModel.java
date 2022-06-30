package com.nameisknowledge.knowledgebank.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.messaging.FirebaseMessaging;
import com.nameisknowledge.knowledgebank.FireBaseRepository;
import com.nameisknowledge.knowledgebank.modelClasses.UserMD;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class LoginViewModel extends ViewModel {
    private final FireBaseRepository fireBaseRepository;
    private String token;
    public final MutableLiveData<UserMD> loggedIn = new MutableLiveData<>();
    public final MutableLiveData<String> registered = new MutableLiveData<>();
    public final MutableLiveData<Throwable> error = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LoginViewModel() {
        getToken();
        fireBaseRepository = FireBaseRepository.getInstance();
    }

    public void login(String email, String pass) {
        fireBaseRepository.loginObservable(email, pass)
                .subscribe(loginObserver());
    }

    public void register(UserMD userMD) {
        userMD.setNotificationToken(token);
        fireBaseRepository.registerObservable(userMD)
                .subscribe(registerObserver());
    }

    private void getToken() {
        FirebaseMessaging.getInstance()
                .getToken()
                .addOnSuccessListener(token -> {
                    this.token = token;
                });
    }

    private SingleObserver<UserMD> loginObserver() {
        return new SingleObserver<UserMD>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull UserMD userMD) {
                fireBaseRepository.updateToken(token);
                loggedIn.setValue(userMD);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                error.setValue(e);
                //
            }
        };
    }

    private SingleObserver<String> registerObserver() {
        return new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull String id) {
                fireBaseRepository.setDefaultPlayerScore(id);
                fireBaseRepository.setDefaultResponse(id);
                registered.setValue(id);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                error.setValue(e);
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
