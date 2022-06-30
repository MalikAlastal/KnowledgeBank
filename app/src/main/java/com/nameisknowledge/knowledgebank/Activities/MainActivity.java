package com.nameisknowledge.knowledgebank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nameisknowledge.knowledgebank.Activities.duoMode.DuoModeActivity;
import com.nameisknowledge.knowledgebank.Activities.questionsMode.QuestionsModeActivity;
import com.nameisknowledge.knowledgebank.Activities.renderGamePlay.RenderGamePlayActivity;
import com.nameisknowledge.knowledgebank.Activities.soloMode.SoloModeActivity;
import com.nameisknowledge.knowledgebank.Adapters.ModesBannerAdapter;
import com.nameisknowledge.knowledgebank.Adapters.UsersAdapter;
import com.nameisknowledge.knowledgebank.Constants.DurationConstants;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Constants.UserConstants;
import com.nameisknowledge.knowledgebank.Methods.ToastMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.ModeMD;
import com.nameisknowledge.knowledgebank.ModelClasses.NotificationMD;
import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.Retroift.Data;
import com.nameisknowledge.knowledgebank.Retroift.NotificationData;
import com.nameisknowledge.knowledgebank.Retroift.PushNotification;
import com.nameisknowledge.knowledgebank.Retroift.RetrofitInstance;
import com.nameisknowledge.knowledgebank.databinding.ActivityMainBinding;
import com.zhpan.bannerview.BannerViewPager;
import com.zhpan.bannerview.constants.PageStyle;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ToastMethods toastMethods;
    List<UserMD> userMDs;
    UsersAdapter usersAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prepareActivity();

        FirebaseFirestore.getInstance().collection(FirebaseConstants.USERS_COLLECTION).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                userMDs = queryDocumentSnapshots.toObjects(UserMD.class);
                usersAdapter.setUsers(userMDs);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMethods.error(e.getMessage());
            }
        });
    }

    private void prepareActivity() {
        binding.rvUsers.setHasFixedSize(true);
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(this));

        toastMethods = new ToastMethods(this);
        userMDs = new ArrayList<>();


        usersAdapter = new UsersAdapter(userMD -> {

        });

        binding.rvUsers.setAdapter(usersAdapter);
        prepareModes();

        binding.ivUserImage.setMinimumWidth(binding.layoutUserDetails.getMinimumWidth());

        UserMD user = UserConstants.getCurrentUser(this);

        binding.tvUserEmail.setText(user.getEmail());
        binding.tvUserUsername.setText(user.getUsername());

        if (user.getAvatarRes() != null && !user.getAvatarRes().equals("")) {
            binding.ivUserImage.setImageResource(Integer.parseInt(user.getAvatarRes()));
        } else {
            String userGender = user.getGender();
            if (userGender.equals(UserConstants.GENDER_MALE))
                binding.ivUserImage.setImageResource(R.drawable.avatar_man_1);
            else if (userGender.equals(UserConstants.GENDER_FEMALE))
                binding.ivUserImage.setImageResource(R.drawable.avatar_woman_1);
        }
    }

    private void prepareModes() {
        ModesBannerAdapter bannerAdapter = new ModesBannerAdapter();

        binding.bvpModes.setAdapter(bannerAdapter)
                .setLifecycleRegistry(getLifecycle())
                .setPageStyle(PageStyle.MULTI_PAGE_OVERLAP)
                .setScrollDuration(DurationConstants.DURATION_SO_SHORT)
                .setRevealWidth(5, 5)
                .setPageMargin(getResources().getDimensionPixelOffset(R.dimen._85sdp))
                .setAutoPlay(false)
                .setCanLoop(false)
                .setIndicatorVisibility(View.GONE)
                .setOnPageClickListener(new BannerViewPager.OnPageClickListener() {
                    @Override
                    public void onPageClick(View clickedView, int position) {
                        if (binding.bvpModes.getCurrentItem() != position) {
                            binding.bvpModes.setCurrentItem(position);
                        } else {
                            switch (position) {
                                case 0:
                                    soloModeListener();
                                    break;
                                case 1:
                                    duoModeListener();
                                    break;
                                case 3:
                                    questionsModeListener();
                                    break;
                                case 2:
                                    mapModeListener();
                                    break;
                            }
                        }
                    }
                });
        ;

        List<ModeMD> modes = new ArrayList<>();

        modes.add(new ModeMD(R.string.mode_solo, R.drawable.ic_solo_mode, getResources().getColor(R.color.dark_main_color)));
        modes.add(new ModeMD(R.string.duo_mode, R.drawable.ic_duo_mode, getResources().getColor(R.color.dark_main_color)));
        modes.add(new ModeMD(R.string.map_mode, R.drawable.ic_map_mode, getResources().getColor(R.color.dark_main_color)));
        modes.add(new ModeMD(R.string.questions_mode, R.drawable.ic_duo_mode, getResources().getColor(R.color.dark_main_color)));

        binding.bvpModes.create(modes);

        binding.bvpModes.setCurrentItem(1, false);
    }

    private void soloModeListener() {
        Intent goSoloActivity = new Intent(this, SoloModeActivity.class);
        startActivity(goSoloActivity);
    }

    private void duoModeListener() {
        Intent goSoloActivity = new Intent(this, SendPlayRequestActivity.class).putExtra("mode","DuoMode");
        startActivity(goSoloActivity);
    }

    private void questionsModeListener() {
        Intent goSoloActivity = new Intent(this, SendPlayRequestActivity.class).putExtra("mode","QuestionsMode");
        startActivity(goSoloActivity);
    }

    private void mapModeListener() {
        Intent goSoloActivity = new Intent(this, MapModeActivity.class);
        startActivity(goSoloActivity);
    }

    private void sendMessage(NotificationMD notification) {
        NotificationData notificationData = new NotificationData("Play Request", "hello");
        Data data = new Data(notification.getSenderName(),notification.getSenderId(),notification.getMode());
        PushNotification pushNotification = new PushNotification(notificationData,notification.getTargetToken(),data);
        RetrofitInstance.getInstance().sentNot(pushNotification);
    }

}