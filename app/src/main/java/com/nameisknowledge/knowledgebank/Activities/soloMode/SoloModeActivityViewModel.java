package com.nameisknowledge.knowledgebank.Activities.soloMode;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nameisknowledge.knowledgebank.FireBaseRepository;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;

import java.util.Random;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class SoloModeActivityViewModel extends ViewModel {
    private final String[] letters = {
            "أ", "ب", "ت", "ث", "ج", "ح","خ", "د","ذ","ر","ز","س","ش", "ص", "ض", "ط","ظ","ع", "غ","ف", "ق","م","ل","ك","ن", "ه","و","ي"
    };
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final FireBaseRepository fireBaseRepository;
    public final MutableLiveData<String> question = new MutableLiveData<>();
    public final MutableLiveData<String> realAnswer = new MutableLiveData<>();
    public final MutableLiveData<String> longAnswer = new MutableLiveData<>();
    public final MutableLiveData<String> emptyAnswer = new MutableLiveData<>();


    public SoloModeActivityViewModel() {
        this.fireBaseRepository = FireBaseRepository.getInstance();
        fireBaseRepository.generateRandomQuestionObservable().subscribe(generateRandomQuestionObserver());
    }

    public void submit(String answer,String input){
        if (answer.equals(input)){
            fireBaseRepository.generateRandomQuestionObservable().subscribe(generateRandomQuestionObserver());
        }
    }


    private SingleObserver<QuestionMD> generateRandomQuestionObserver(){
        return new SingleObserver<QuestionMD>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull QuestionMD questionMD) {
                question.setValue(questionMD.getQuestion());
                realAnswer.setValue(clearAnswerSpaces(questionMD.getAnswer()));
                longAnswer.setValue(makeAnswerLonger(clearAnswerSpaces(questionMD.getAnswer())));
                emptyAnswer.setValue(makeStringEmpty(clearAnswerSpaces(questionMD.getAnswer())));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //
            }
        };
    }

    public String makeStringEmpty(String s) {
        char[] array = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            array[i] = ' ';
        }
        return String.valueOf(array);
    }

    private String clearAnswerSpaces(String answer) {
        StringBuilder formatAnswer = new StringBuilder();
        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) != ' ') {
                formatAnswer.append(answer.charAt(i));
            }
        }
        return formatAnswer.toString();
    }

    private String randomTheAnswer(String string) {
        char[] array = string.toCharArray();
        for (int i = 0; i < array.length; i++) {
            int randomIndexToSwap = new Random().nextInt(array.length);
            char temp = array[randomIndexToSwap];
            array[randomIndexToSwap] = array[i];
            array[i] = temp;
        }
        return String.valueOf(array);
    }

    private String makeAnswerLonger(String answer) {
        StringBuilder finalAnswer = new StringBuilder();
        finalAnswer.append(answer);
        for (int i = answer.length(); i < answer.length() + 4; i++) {
            finalAnswer.append(letters[new Random().nextInt(((letters.length - 1)) + 1)]);
        }
        return randomTheAnswer(finalAnswer.toString());
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
