package com.nameisknowledge.knowledgebank.ui.attackArea;

import static com.nameisknowledge.knowledgebank.methods.StringFactory.clearAnswerSpaces;
import static com.nameisknowledge.knowledgebank.methods.StringFactory.makeAnswerLonger;
import static com.nameisknowledge.knowledgebank.methods.StringFactory.makeStringEmpty;
import static com.nameisknowledge.knowledgebank.methods.StringFactory.toInputsList;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

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
import com.nameisknowledge.knowledgebank.adapters.GamePlayAdapter;
import com.nameisknowledge.knowledgebank.constants.DurationConstants;
import com.nameisknowledge.knowledgebank.constants.UserConstants;
import com.nameisknowledge.knowledgebank.dialogs.AreaAttackedDialog;
import com.nameisknowledge.knowledgebank.listeners.GenericListener;
import com.nameisknowledge.knowledgebank.methods.AnimationMethods;
import com.nameisknowledge.knowledgebank.methods.ToastMethods;
import com.nameisknowledge.knowledgebank.methods.ViewMethods;
import com.nameisknowledge.knowledgebank.modelClasses.MapAreaMD;
import com.nameisknowledge.knowledgebank.modelClasses.questions.MapFireBaseQuestionMD;
import com.nameisknowledge.knowledgebank.modelClasses.UserMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.ViewModelsFactory;
import com.nameisknowledge.knowledgebank.databinding.ActivityAttackAreaBinding;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AttackAreaActivity extends AppCompatActivity implements GenericListener<Boolean>{

    ActivityAttackAreaBinding binding ;
    public static final String AREA_KEY = "AREA_KEY";
    private MapAreaMD area ;
    ToastMethods toastMethods ;
    List<MapFireBaseQuestionMD> questionList ;
    GamePlayAdapter inputAdapter ;
    GamePlayAdapter answerAdapter ;
    MediaPlayer clickSound;
    MediaPlayer swingSound;
    MediaPlayer popSound;
    boolean isActivityVisible;
    private RewardedAd rewardedAd;
    private InterstitialAd interstitialAd;
    private String currentQuestionAnswer;
    private int answeredQuestionsCount;
    private AttackAreaViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttackAreaBinding.inflate(getLayoutInflater());
        ViewMethods.setLocale(this , "ar");
        setContentView(binding.getRoot());
        prepareActivity();

        viewModel.updatedUser.observe(this,user->{
            UserConstants.setCurrentUser(user,this);
        });

        viewModel.question.observe(this, this::setUiData);

        viewModel.playerAnsweredMoreThanOwner.observe(this,integer -> {
            this.answeredQuestionsCount = integer;
            toastMethods.success("congrats");
            viewModel.setOwner(area.getAreaName(),UserConstants.getCurrentUser(this),answeredQuestionsCount);
        });

        viewModel.allQuestionsAnswered.observe(this,user->{
            endGame(area,UserConstants.getCurrentUser(this),true);
            viewModel.setOwner(area.getAreaName(),UserConstants.getCurrentUser(this),answeredQuestionsCount);
        });

        binding.btnEndGame.setOnClickListener(view -> {
            endGame(area,UserConstants.getCurrentUser(this), answeredQuestionsCount >= area.getOwnerAnsweredQuestionsCount());
        });

    }

    private void setUiData(MapFireBaseQuestionMD question){
        binding.tvQuestion.setText(question.getQuestion());
        this.currentQuestionAnswer = clearAnswerSpaces(question.getAnswer());
        changeQuestionAnimation();
        inputAdapter.clearArray();
        inputAdapter.setAnswer(makeAnswerLonger(clearAnswerSpaces(question.getAnswer())));
        answerAdapter.clearArray();
        answerAdapter.setAnswer(toInputsList(makeStringEmpty(clearAnswerSpaces(question.getAnswer()))));
    }

    private void prepareActivity(){
        area = (MapAreaMD) getIntent().getSerializableExtra(AREA_KEY);
        viewModel = new ViewModelProvider(this,new ViewModelsFactory(area)).get(AttackAreaViewModel.class);

        toastMethods = new ToastMethods();

        toastMethods.info(area.getAreaName());
        questionList = area.getQuestionList();

        clickSound = MediaPlayer.create(this , R.raw.button_clicked);
        swingSound = MediaPlayer.create(this , R.raw.swing);
        popSound = MediaPlayer.create(this , R.raw.pop_sound);

        binding.tvAreaName.setText(area.getAreaName());

        isActivityVisible = true ;

        toastMethods.info(questionList.get(0).getCategory());

        changeProgress(binding.playerProgressLine,0);
        changeProgress(binding.ownerProgressLine,(int) (((double)area.getOwnerAnsweredQuestionsCount()/(double)questionList.size()) *100.00));

        try {
            binding.imgPlayer.setImageResource(Integer.parseInt(UserConstants.getCurrentUser(this).getAvatarRes()));
            binding.imgOwner.setImageResource(Integer.parseInt(area.getOwnerUser().getAvatarRes()));

        }
        catch (Exception e){

        }

        prepareRecyclers();
        prepareAds();
        viewModel.updatedAttackPoints(UserConstants.getCurrentUser(this).getUid(),(UserConstants.getCurrentUser(this).getAreaAttackPoints()-1));
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
        binding.rvAnswer.setHasFixedSize(true);
        binding.rvAnswer.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        binding.rvInput.setHasFixedSize(true);
        binding.rvInput.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        answerAdapter = new GamePlayAdapter(false, inputsMD -> {
            if (inputsMD.getLetter() != ' ') {
                inputAdapter.setChar(inputsMD);
            }
            buttonClickedSound();
        });

        inputAdapter = new GamePlayAdapter(true, inputsMD -> answerAdapter.checkEmpty(list -> {
            if (list.size() != 0) {
                inputAdapter.setEmpty(inputsMD.getIndex(), inputsMD);
                answerAdapter.addChar(inputsMD);
            }
            viewModel.submitAnswer(currentQuestionAnswer, answerAdapter.getAnswer());
            buttonClickedSound();
        }));

        binding.rvAnswer.setAdapter(answerAdapter);
        binding.rvInput.setAdapter(inputAdapter);

    }


    private void changeQuestionAnimation() {
        swingSound.start();
        AnimationMethods.slideOutLeft(DurationConstants.DURATION_SO_SHORT,
                animator -> AnimationMethods.slideInRight(DurationConstants.DURATION_SO_SHORT, binding.cardQuestion, binding.rvInput),
                binding.cardQuestion, binding.rvInput);
    }



    private void endGame(MapAreaMD area,UserMD user,boolean isWinner){
        AreaAttackedDialog.newInstance(area,user,isWinner).show(getSupportFragmentManager(),"");
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
                viewModel.updatedAttackPoints(UserConstants.getCurrentUser(getBaseContext()).getUid(),(UserConstants.getCurrentUser(getApplicationContext()).getAreaAttackPoints()+UserConstants.DOUBLE_REWARD_AREA_ATTACK_POINTS));
            }
        });
        }else {
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
            endGame(area ,UserConstants.getCurrentUser(getBaseContext()),false);
        }
        isActivityVisible = true ;
    }

}

