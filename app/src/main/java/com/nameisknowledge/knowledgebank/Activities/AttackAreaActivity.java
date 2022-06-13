package com.nameisknowledge.knowledgebank.Activities;

import android.animation.Animator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.Adapters.TestRvAdapter;
import com.nameisknowledge.knowledgebank.Constants.DurationConstants;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Constants.UserConstants;
import com.nameisknowledge.knowledgebank.Dialogs.AreaAttackedDialog;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.Methods.AnimationMethods;
import com.nameisknowledge.knowledgebank.Methods.ToastMethods;
import com.nameisknowledge.knowledgebank.Methods.ViewMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.MapAreaMD;
import com.nameisknowledge.knowledgebank.ModelClasses.MapQuestionMD;
import com.nameisknowledge.knowledgebank.ModelClasses.TestRvMD;
import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.ActivityAttackAreaBinding;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AttackAreaActivity extends AppCompatActivity implements GenericListener<Boolean>{

    ActivityAttackAreaBinding binding ;

    public static final String AREA_KEY = "AREA_KEY";

    private MapAreaMD area ;
    ToastMethods toastMethods ;

    List<MapQuestionMD> questionList ;
    TestRvAdapter inputAdapter ;
    TestRvAdapter answerAdapter ;

    MediaPlayer clickSound;
    MediaPlayer swingSound;
    MediaPlayer popSound;

    boolean isActivityVisible;

    public final String[] letters = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    };

    int currentQuestion = 0 ;
    int answeredQuestions = 0 ;

    FirebaseFirestore firestore ;

    private RewardedAd rewardedAd;
    private InterstitialAd interstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttackAreaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prepareActivity();
    }

    private void prepareActivity(){
        area = (MapAreaMD) getIntent().getSerializableExtra(AREA_KEY);
        toastMethods = new ToastMethods(this);

        toastMethods.info(area.getAreaName());
        questionList = area.getQuestionList();

        clickSound = MediaPlayer.create(this , R.raw.button_clicked);
        swingSound = MediaPlayer.create(this , R.raw.swing);
        popSound = MediaPlayer.create(this , R.raw.pop_sound);

        firestore = FirebaseFirestore.getInstance() ;

        binding.tvQuestion.setText(questionList.get(currentQuestion).getQuestion());
        binding.tvAreaName.setText(area.getAreaName());

        isActivityVisible = true ;

        for (MapQuestionMD question: questionList) {
            question.setAnswer(clearAnswerSpaces(question.getAnswer()));
        }


        changeProgress(binding.playerProgressLine,0);
        changeProgress(binding.ownerProgressLine,(int) (((double)area.getOwnerAnsweredQuestionsCount()/(double)questionList.size()) *100.00));

        try {
            binding.imgPlayer.setImageResource(Integer.parseInt(UserConstants.getCurrentUser(this).getAvatarRes()));
            binding.imgOwner.setImageResource(Integer.parseInt(area.getOwnerUser().getAvatarRes()));

        }
        catch (Exception e){

        }
        prepareListeners();
        prepareRecyclers();
        prepareAds();
        updateUserAttackAreaPoints(-1);
    }

    private void prepareListeners(){
        binding.btnEndGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endGame(area , UserConstants.getCurrentUser(getBaseContext()));
            }
        });
    }

    private void prepareAds(){
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd mRewardedAd) {
                        rewardedAd = mRewardedAd;
                        toastMethods.info("loaded");
                    }
                });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        AttackAreaActivity.this.interstitialAd = interstitialAd  ;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        AttackAreaActivity.this.interstitialAd = null;
                    }
                });
    }

    private void prepareRecyclers(){

        inputAdapter = new TestRvAdapter(updateInput(questionList.get(currentQuestion).getAnswer()), true, new GenericListener<TestRvMD>() {
            @Override
            public void getData(TestRvMD testRvMD) {
                answerAdapter.checkEmpty(new GenericListener<List<Integer>>() {
                    @Override
                    public void getData(List<Integer> integers) {
                        buttonClickedSound();
                        if (integers.size() !=0){
                            inputAdapter.setEmpty(testRvMD.getIndex(), testRvMD);
                            answerAdapter.addChar(testRvMD);
                        }

                        submit(mergeAnswerChars(answerAdapter.getMyList()));
                    }
                });
            }
        });

       answerAdapter = new TestRvAdapter(makeStringEmpty(questionList.get(currentQuestion).getAnswer()), false, new GenericListener<TestRvMD>() {
            @Override
            public void getData(TestRvMD testRvMD) {
                inputAdapter.setChar(testRvMD);
                buttonClickedSound();
            }
        });

       GridLayoutManager layoutManager =  new GridLayoutManager(this , 5 , GridLayoutManager.VERTICAL, false);

       ViewMethods.prepareRecycler(binding.rvAnswer , answerAdapter  , true ,layoutManager);
       ViewMethods.prepareRecycler(binding.rvInput , inputAdapter  , true , new GridLayoutManager(this , 5 , GridLayoutManager.VERTICAL, false));
    }

    private String makeStringEmpty(String s) {
        char[] array = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            array[i] = ' ';
        }
        return String.valueOf(array);
    }

    private String updateInput(String answer) {
        StringBuilder finalAnswer = new StringBuilder();
        finalAnswer.append(answer);
        for (int i = answer.length(); i < answer.length() + 4; i++) {
            finalAnswer.append(letters[new Random().nextInt(((letters.length - 1)) + 1)]);
        }
        return randomTheAnswer(finalAnswer.toString());
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

    private String clearAnswerSpaces(String answer){
        StringBuilder formatAnswer = new StringBuilder();
        for (int i = 0 ; i<answer.length() ; i++) {
            if (answer.charAt(i)!=' '){
                formatAnswer.append(answer.charAt(i));
            }
        }
        return formatAnswer.toString() ;
    }

    private void submit(String answer) {
        if (TextUtils.equals(answer, questionList.get(currentQuestion).getAnswer())) {
            currentQuestion++ ;
            answeredQuestions++ ;

            changeProgress(binding.playerProgressLine,(int) (((double)currentQuestion/(double)questionList.size())*100.00));

            if(currentQuestion<questionList.size()){
            nextQuestion(currentQuestion);
            }
            else {
                endGame(area,UserConstants.getCurrentUser(getApplicationContext()));
            }
        }
        else {
        }
    }

    private String mergeAnswerChars(List<TestRvMD> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i).getLetter());
        }
        return stringBuilder.toString();
    }

    private void nextQuestion(int nextQuestion){

        AnimationMethods.slideOutLeft(DurationConstants.DURATION_SO_SHORT, new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                inputAdapter.clearArray();
                answerAdapter.clearArray();
                inputAdapter.setAnswer(updateInput(questionList.get(nextQuestion).getAnswer()));
                answerAdapter.setAnswer(makeStringEmpty(questionList.get(nextQuestion).getAnswer()));
                binding.tvQuestion.setText(questionList.get(nextQuestion).getQuestion());

                swingSound.start();

                AnimationMethods.slideInRight(DurationConstants.DURATION_SO_SHORT ,binding.cardQuestion , binding.rvInput);
            }
        },binding.cardQuestion , binding.rvInput);


    }

    private void endGame(MapAreaMD area,UserMD user){
       if (answeredQuestions>=area.getOwnerAnsweredQuestionsCount()){
           setAreaOwner(area, user);
       }
       else {
           AreaAttackedDialog.newInstance(area , user  ,false).show(getSupportFragmentManager() , "");
       }
    }

    private void setAreaOwner(MapAreaMD area,UserMD user){
        MapAreaMD oldArea = copyArea(area);
        area.setOwnerUser(user);
        area.setOwnerAnsweredQuestionsCount(currentQuestion);

        firestore.collection(FirebaseConstants.MAP_AREAS_COLLECTION).document(area.getAreaName()).set(area)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        AreaAttackedDialog.newInstance(oldArea , user  ,true).show(getSupportFragmentManager() , "");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void getData(Boolean isRewardButtonClicked) {

        if (isRewardButtonClicked){
        if (rewardedAd==null){
            finish();
            return;
        }
        rewardedAd.show(this, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                updateUserAttackAreaPoints(UserConstants.DOUBLE_REWARD_AREA_ATTACK_POINTS);
            }
        });
        }
        else {
            interstitialAd.show(this);
        }
        finish();

    }

    public class ProgressHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            NumberProgressBar progressBar = (NumberProgressBar) msg.obj;
            progressBar.setProgress(msg.arg1);
        }
    }

    private void changeProgress(NumberProgressBar progressBar, int targetProgress){
        ProgressHandler handler  = new ProgressHandler();

        int currentProgress = progressBar.getProgress() ;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (targetProgress>=currentProgress){
                    for (int i = currentProgress  ; i<=targetProgress ; i++){
                        try {

                            Message message = new Message() ;

                            message.arg1 = i ;
                            message.obj = progressBar ;

                            handler.sendMessage(message);

                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    for (int i = currentProgress  ; i>=targetProgress ; i--){
                        try {
                            handler.sendEmptyMessage(i);
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private MapAreaMD copyArea(MapAreaMD area){
        return new MapAreaMD(area.getAreaName() , area.getAreaLng() , area.getAreaLat() , area.getOwnerUser() , area.getOwnerAnsweredQuestionsCount() , area.getQuestionList());
    }

    private void buttonClickedSound(){
        try {
            if (clickSound.isPlaying()){
                clickSound.seekTo(0);
            }
            else {
                clickSound.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    isActivityVisible = false ;
            }
        }, TimeUnit.SECONDS.toMillis(30));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isActivityVisible){
            endGame(area , UserConstants.getCurrentUser(getBaseContext()));
        }
        isActivityVisible = true ;
    }

    private void updateUserAttackAreaPoints(int points){
        UserMD currentUser = UserConstants.getCurrentUser(getBaseContext());
        currentUser.setAreaAttackPoints(currentUser.getAreaAttackPoints()+points);

        firestore.collection(FirebaseConstants.USERS_COLLECTION).document(currentUser.getUid()).set(currentUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
    private void updateUserInfo(){
        UserMD currentUser = UserConstants.getCurrentUser(getBaseContext());
        currentUser.setAreaAttackPoints(currentUser.getAreaAttackPoints());

        firestore.collection(FirebaseConstants.USERS_COLLECTION).document(currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserMD user = documentSnapshot.toObject(UserMD.class);

                        if (user==null){
                            return;
                        }

                        UserConstants.setCurrentUser(user , getBaseContext());
                        user = null ;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}

