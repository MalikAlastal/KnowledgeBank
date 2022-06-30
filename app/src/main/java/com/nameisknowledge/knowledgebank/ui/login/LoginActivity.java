package com.nameisknowledge.knowledgebank.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.databinding.ActivityLoginBinding;
import com.nameisknowledge.knowledgebank.ui.main.MainActivity;
import com.nameisknowledge.knowledgebank.adapters.AvatarsBannerAdapter;
import com.nameisknowledge.knowledgebank.constants.DurationConstants;
import com.nameisknowledge.knowledgebank.constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.constants.UserConstants;
import com.nameisknowledge.knowledgebank.methods.AnimationMethods;
import com.nameisknowledge.knowledgebank.methods.ToastMethods;
import com.nameisknowledge.knowledgebank.methods.ViewMethods;
import com.nameisknowledge.knowledgebank.modelClasses.AvatarMD;
import com.nameisknowledge.knowledgebank.modelClasses.UserMD;
import com.nameisknowledge.knowledgebank.R;
import com.zhpan.bannerview.BannerViewPager;
import com.zhpan.bannerview.constants.PageStyle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    // حالة الواجهة : تسجيل دخول أو تسجيل مستخدم جديد
    private String state;
    private final String login_state = "login_state";
    private final String register_state = "register_state";
    ToastMethods toastMethods;
    List<AvatarMD> avatars;
    AvatarsBannerAdapter bannerAdapter;
    LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prepareActivity();


        viewModel.loggedIn.observe(this, userMD -> {
            UserConstants.setCurrentUser(userMD, getBaseContext());
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            stopLoading();
            finish();
        });

        viewModel.registered.observe(this, done -> {
            stopLoading();
            ViewMethods.clearEditText(binding.edUsername, binding.edRePassword);
            binding.dpBirthdate.clear();
            binding.rbGenderLayout.setPosition(0, true);
            prepareForLogin();
            toastMethods.success("success");
        });

        viewModel.error.observe(this, error -> {
            toastMethods.error(error.getMessage());
            stopLoading();
        });

        binding.btnLogin.setOnClickListener(view -> {
            if (ViewMethods.isTextInputEmpty(binding.edEmail, binding.edPassword)) {
                return;
            }

            String email = ViewMethods.getText(binding.edEmail);
            String password = ViewMethods.getText(binding.edPassword);

            startLoading(binding.progressLogin);

            viewModel.login(email, password);
        });

        binding.btnRegister.setOnClickListener(view -> {
            startLoading(binding.progressRegister);
            viewModel.register(Objects.requireNonNull(getEnteredData()));
        });

        binding.btnPrepareRegister.setOnClickListener(view -> prepareForRegister());

        binding.btnCancel.setOnClickListener(view -> prepareForLogin());

        binding.rbGenderLayout.setOnPositionChangedListener(position -> {
            if (position == 0) {
                syncAvatarsWithGender(UserConstants.GENDER_MALE);
            } else if (position == 1) {
                syncAvatarsWithGender(UserConstants.GENDER_FEMALE);
            }
        });
    }

    private void prepareActivity() {
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        getQuestionsLevelsSize();
        ViewMethods.goneView(binding.edRePasswordLayout, binding.edUsernameLayout, binding.edBirthdateLayout
                , binding.rbGenderLayout, binding.btnRegister, binding.btnCancel
                , binding.progressLogin, binding.progressRegister);

        state = login_state;
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - UserConstants.MIN_AGE);
        binding.dpBirthdate.setMaxDate(calendar.getTime());


        toastMethods = new ToastMethods();
        avatars = new ArrayList<>();
        bannerAdapter = new AvatarsBannerAdapter();

        binding.progressLogin.setSmoothProgressDrawableColors(new int[]{getResources().getColor(R.color.light_main_color), getResources().getColor(R.color.dark_main_color)});
        binding.progressRegister.setSmoothProgressDrawableColors(new int[]{getResources().getColor(R.color.light_main_color), getResources().getColor(R.color.dark_main_color)});

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            binding.edEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            binding.edPassword.setText(UserConstants.getCurrentUser(this).getPassword());
        }

        prepareAvatars();
    }

    private void prepareForRegister() {
        // Animations
        AnimationMethods.slideOutRight(DurationConstants.DURATION_SHORT, binding.btnLogin);

        ViewMethods.visibleView(binding.edRePasswordLayout, binding.edUsernameLayout, binding.edBirthdateLayout
                , binding.rbGenderLayout, binding.btnRegister, binding.btnCancel, binding.bvpAvatars);
        ViewMethods.goneView(binding.btnPrepareRegister);
        AnimationMethods.flipInX(DurationConstants.DURATION_SHORT, binding.edRePasswordLayout, binding.edUsernameLayout, binding.edBirthdateLayout, binding.rbGenderLayout);

        AnimationMethods.bounceInDown(DurationConstants.DURATION_SHORT, binding.btnRegister, binding.btnCancel);
        AnimationMethods.slideInDown(DurationConstants.DURATION_SHORT, binding.bvpAvatars);

        changeState();
    }

    private void prepareForLogin() {

        // Animations
        AnimationMethods.slideInRight(DurationConstants.DURATION_SHORT, binding.btnLogin);
        AnimationMethods.flipOutX(DurationConstants.DURATION_SO_SHORT, animator -> {
            ViewMethods.goneView(binding.edRePasswordLayout, binding.edUsernameLayout, binding.edBirthdateLayout
                    , binding.rbGenderLayout, binding.btnRegister, binding.btnCancel);
            AnimationMethods.bounceInUp(DurationConstants.DURATION_SHORT, binding.btnPrepareRegister);
            ViewMethods.visibleView(binding.btnPrepareRegister);
        }, binding.edRePasswordLayout, binding.edUsernameLayout, binding.edBirthdateLayout, binding.rbGenderLayout);

        AnimationMethods.slideOutUp(DurationConstants.DURATION_SHORT, animator -> ViewMethods.goneView(binding.bvpAvatars)
                , binding.bvpAvatars);
        changeState();
    }

    private void changeState() {
        if (state.equals(login_state)) {
            state = register_state;
        } else if (state.equals(register_state)) {
            state = login_state;
        }
    }

    private UserMD getEnteredData() {

        Date birthdate = binding.dpBirthdate.getDate();

        if (ViewMethods.isTextInputEmpty(binding.edEmail, binding.edPassword, binding.edRePassword, binding.edUsername))
            return null;

        else if (birthdate == null) {
            binding.dpBirthdate.shake();
            return null;
        }

        String gender = UserConstants.GENDER_MALE;

        if (binding.rbGenderLayout.getPosition() == 1)
            gender = UserConstants.GENDER_FEMALE;

        String email = ViewMethods.getText(binding.edEmail);
        String password = ViewMethods.getText(binding.edPassword);
        String rePassword = ViewMethods.getText(binding.edRePassword);
        String username = ViewMethods.getText(binding.edUsername);

        if (!password.equals(rePassword)) {
            ViewMethods.editTextEmptyError(binding.edPassword, binding.edRePassword);
            return null;
        }

        Date creationDate = Calendar.getInstance().getTime();

        int avatarRes = avatars.get(binding.bvpAvatars.getCurrentItem()).getAvatarRes();


        String avatar = String.valueOf(avatarRes);

        return new UserMD("", username, email, password, gender, avatar, "", birthdate, creationDate, UserConstants.DEFAULT_AREA_ATTACK_POINTS);
    }

    private void startLoading(SmoothProgressBar progress) {
        ViewMethods.enableView(false, binding.edEmail, binding.edPassword, binding.edRePassword, binding.edUsername
                , binding.dpBirthdate, binding.rbGenderLayout, binding.btnRegister, binding.btnCancel
                , binding.btnLogin, binding.btnPrepareRegister);

        ViewMethods.visibleView(progress);
    }

    private void stopLoading() {
        ViewMethods.enableView(true, binding.edEmail, binding.edPassword, binding.edRePassword, binding.edUsername
                , binding.dpBirthdate, binding.rbGenderLayout, binding.btnRegister, binding.btnCancel
                , binding.btnLogin, binding.btnPrepareRegister);

        ViewMethods.goneView(binding.progressRegister, binding.progressLogin);
    }

    private void prepareAvatars() {

        avatars.add(new AvatarMD(R.drawable.avatar_man_1, UserConstants.GENDER_MALE, "الداهية"));
        avatars.add(new AvatarMD(R.drawable.avatar_man_2, UserConstants.GENDER_MALE, "اللعيب"));
        avatars.add(new AvatarMD(R.drawable.avatar_man_3, UserConstants.GENDER_MALE, "جنتل مان"));
        avatars.add(new AvatarMD(R.drawable.avatar_man_4, UserConstants.GENDER_MALE, "الهاكر"));
        avatars.add(new AvatarMD(R.drawable.avatar_man_5, UserConstants.GENDER_MALE, "البروفيسور"));
        avatars.add(new AvatarMD(R.drawable.avatar_woman_1, UserConstants.GENDER_FEMALE, "سمية"));
        avatars.add(new AvatarMD(R.drawable.avatar_woman_2, UserConstants.GENDER_FEMALE, "ياسمين"));
        avatars.add(new AvatarMD(R.drawable.avatar_woman_3, UserConstants.GENDER_FEMALE, "أزهار"));

        binding.bvpAvatars.setAdapter(bannerAdapter)
                .setLifecycleRegistry(getLifecycle())
                .setPageStyle(PageStyle.MULTI_PAGE_OVERLAP)
                .setScrollDuration(DurationConstants.DURATION_SO_SHORT)
                .setRevealWidth(30, 30)
                .setPageMargin(getResources().getDimensionPixelOffset(R.dimen._85sdp))
                .setAutoPlay(false)
                .setCanLoop(false)
                .setIndicatorVisibility(View.GONE)
                .setOnPageClickListener(new BannerViewPager.OnPageClickListener() {
                    @Override
                    public void onPageClick(View clickedView, int position) {
                        if (binding.bvpAvatars.getCurrentItem() != position) {
                            binding.bvpAvatars.setCurrentItem(position);
                        }
                    }
                });


        binding.bvpAvatars.create(avatars);
        binding.bvpAvatars.setCurrentItem(1);
        ViewMethods.invisibleView(binding.bvpAvatars);
    }

    private void syncAvatarsWithGender(String gender) {

        String currentGender = avatars.get(binding.bvpAvatars.getCurrentItem()).getAvatarGender();
        if (currentGender.equals(gender))
            return;

        int avatarsSize = avatars.size();
        for (int i = 0; i < avatarsSize; i++) {
            String avatarGender = avatars.get(i).getAvatarGender();

            if (avatarGender.equals(gender)) {
                binding.bvpAvatars.setCurrentItem(i);
                return;
            }
        }
    }


    private void getQuestionsLevelsSize() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String[] levels = new String[]{"Hard", "Medium", "Easy"};
        for (String level : levels) {
            FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.QUESTIONS_COLLECTION)
                    .document(level)
                    .collection(FirebaseConstants.QUESTIONS_CONTAINER)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        editor.putInt(level, documentSnapshot.getDocuments().size());
                        editor.apply();
                    });
        }
    }
}