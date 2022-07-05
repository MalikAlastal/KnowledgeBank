package com.nameisknowledge.knowledgebank.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.auth.User;
import com.nameisknowledge.knowledgebank.constants.IntentConstants;
import com.nameisknowledge.knowledgebank.databinding.ActivityMainBinding;
import com.nameisknowledge.knowledgebank.methods.ViewMethods;
import com.nameisknowledge.knowledgebank.ui.mapMode.MapModeActivity;
import com.nameisknowledge.knowledgebank.ui.SendPlayRequestActivity;
import com.nameisknowledge.knowledgebank.ui.soloMode.SoloModeActivity;
import com.nameisknowledge.knowledgebank.adapters.ModesBannerAdapter;
import com.nameisknowledge.knowledgebank.adapters.UsersAdapter;
import com.nameisknowledge.knowledgebank.constants.DurationConstants;
import com.nameisknowledge.knowledgebank.constants.UserConstants;
import com.nameisknowledge.knowledgebank.modelClasses.ModeMD;
import com.nameisknowledge.knowledgebank.modelClasses.NotificationMD;
import com.nameisknowledge.knowledgebank.modelClasses.UserMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.retroift.Data;
import com.nameisknowledge.knowledgebank.retroift.NotificationData;
import com.nameisknowledge.knowledgebank.retroift.PushNotification;
import com.nameisknowledge.knowledgebank.retroift.RetrofitInstance;
import com.zhpan.bannerview.BannerViewPager;
import com.zhpan.bannerview.constants.PageStyle;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private String mode;
    private UsersAdapter usersAdapter;
    private MainViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prepareActivity();

        viewModel.users.observe(this,users->{
            usersAdapter.setUsers(users);
        });

        viewModel.user.observe(this,user->{
            setUserData(user);
            UserConstants.setCurrentUser(user,this);
        });

    }

    private void prepareActivity() {

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.getUser(UserConstants.getCurrentUser(this).getUid());

        binding.rvUsers.setHasFixedSize(true);
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(this));

        usersAdapter = new UsersAdapter(userMD -> {
            String senderId = UserConstants.getCurrentUser(this).getUid();
            String senderName = UserConstants.getCurrentUser(this).getUsername();
            sendMessage(new NotificationMD(userMD.getNotificationToken(),senderName,senderId,mode));
        });

        binding.rvUsers.setAdapter(usersAdapter);

        prepareModes();

        binding.ivUserImage.setMinimumWidth(binding.layoutUserDetails.getMinimumWidth());
    }

    private void prepareModes() {
        ModesBannerAdapter bannerAdapter = new ModesBannerAdapter();

        binding.bvpModes.setAdapter(bannerAdapter)
                .setLifecycleRegistry(getLifecycle())
                .setPageStyle(PageStyle.MULTI_PAGE_OVERLAP)
                .setScrollDuration(DurationConstants.DURATION_SO_SHORT)
                .setRevealWidth(5, 5)
                .setPageMargin(getResources().getDimensionPixelOffset(R.dimen._85sdp))
                .registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        mode = getMode(position);
                       viewModel.getPlayers(mode);
                    }
                })
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

    private String getMode(int position) {
        String mode = "";
        switch (position){
            case 0:
                mode = IntentConstants.SOLO_MODE_KEY;
                break;
            case 1:
                mode = IntentConstants.DUO_MODE_KEY;
                break;
            case 2:
                mode = IntentConstants.MAPS_MODE_KEY;
                break;
            case 3:
                mode = IntentConstants.QUESTIONS_MODE_KEY;
                break;
        }
        return mode;
    }

    private void soloModeListener() {
        Intent goSoloActivity = new Intent(this, SoloModeActivity.class).putExtra(IntentConstants.MODE_KEY,IntentConstants.SOLO_MODE_KEY);
        startActivity(goSoloActivity);
    }

    private void duoModeListener() {
        Intent goSoloActivity = new Intent(this, SendPlayRequestActivity.class).putExtra(IntentConstants.MODE_KEY,IntentConstants.DUO_MODE_KEY);
        startActivity(goSoloActivity);
    }

    private void questionsModeListener() {
        Intent goSoloActivity = new Intent(this, SendPlayRequestActivity.class).putExtra(IntentConstants.MODE_KEY,IntentConstants.QUESTIONS_MODE_KEY);
        startActivity(goSoloActivity);
    }

    private void mapModeListener() {
        Intent goSoloActivity = new Intent(this, MapModeActivity.class).putExtra(IntentConstants.MODE_KEY,IntentConstants.MAPS_MODE_KEY);
        startActivity(goSoloActivity);
    }

    private void sendMessage(NotificationMD notification) {
        NotificationData notificationData = new NotificationData("Play Request", "hello","OPEN_ACTIVITY");
        Data data = new Data(notification.getSenderName(),notification.getSenderId(),notification.getMode());
        PushNotification pushNotification = new PushNotification(notificationData,notification.getTargetToken(),data);
        RetrofitInstance.getInstance().sentNot(pushNotification);
    }

    private void setUserData(UserMD user){
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


    @Override
    protected void onStart() {
        super.onStart();
        viewModel.getUser(UserConstants.getCurrentUser(this).getUid());
    }

}