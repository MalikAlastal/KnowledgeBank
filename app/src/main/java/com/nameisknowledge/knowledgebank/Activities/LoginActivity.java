package com.nameisknowledge.knowledgebank.Activities;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.Adapters.AvatarsBannerAdapter;
import com.nameisknowledge.knowledgebank.Constants.DurationConstants;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Constants.UserConstants;
import com.nameisknowledge.knowledgebank.Methods.AnimationMethods;
import com.nameisknowledge.knowledgebank.Methods.ToastMethods;
import com.nameisknowledge.knowledgebank.Methods.ViewMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.AvatarMD;
import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.ActivityLoginBinding;
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
    private String state ;
    private final String login_state = "login_state" ;
    private final String register_state = "register_state" ;


    FirebaseAuth auth ;
    FirebaseFirestore firestore ;


    ToastMethods toastMethods ;

    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor ;

    List<AvatarMD> avatars ;
    AvatarsBannerAdapter bannerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prepareActivity();

//        if (auth.getCurrentUser()!=null){
//            startActivity(new Intent(getBaseContext(),MainActivity.class));
//            finish();
//        }

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ViewMethods.isTextInputEmpty(binding.edEmail , binding.edPassword)){
                    return;
                }

                String email = ViewMethods.getText(binding.edEmail);
                String password = ViewMethods.getText(binding.edPassword);

                startLoading(binding.progressLogin);

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        firestore.collection(FirebaseConstants.USERS_COLLECTION)
                                .document(Objects.requireNonNull(authResult.getUser()).getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                UserMD currentUser = documentSnapshot.toObject(UserMD.class);

                                if (currentUser==null)
                                    return;

                                saveCurrentUserData(currentUser);
                                startActivity(new Intent(getBaseContext(),MainActivity.class));
                                stopLoading();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                stopLoading();
                                toastMethods.error(  "حدث خطأ أثناء البحث على معلوماتك" + e.getMessage());
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        stopLoading();
                        toastMethods.error(e.getMessage());
                    }
                });

            }
        });


        binding.btnPrepareRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareForRegister();
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserMD user = getEnteredData();
                if (user!=null){
                    startLoading(binding.progressRegister);

                    auth.createUserWithEmailAndPassword(user.getEmail() , user.getPassword()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            user.setUid(Objects.requireNonNull(authResult.getUser()).getUid());

                            firestore.collection(FirebaseConstants.USERS_COLLECTION).document(user.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    stopLoading();
                                    ViewMethods.clearEditText(binding.edUsername, binding.edRePassword);
                                    binding.dpBirthdate.clear();
                                    binding.rbGenderLayout.setPosition(0 , true);
                                    prepareForLogin();
                                    toastMethods.success("success");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    stopLoading();
                                    toastMethods.error(e.getMessage());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toastMethods.error(e.getMessage());
                            stopLoading();
                        }
                    });
                }
                else {
                }
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareForLogin();
            }
        });

        binding.rbGenderLayout.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
               if (position==0){
                   syncAvatarsWithGender(UserConstants.GENDER_MALE);
               }
               else if (position==1){
                   syncAvatarsWithGender(UserConstants.GENDER_FEMALE);
               }
            }
        });
    }

    private void prepareActivity(){
        ViewMethods.goneView(binding.edRePasswordLayout , binding.edUsernameLayout , binding.edBirthdateLayout
                , binding.rbGenderLayout, binding.btnRegister , binding.btnCancel
                , binding.progressLogin , binding.progressRegister );

        auth = FirebaseAuth.getInstance() ;
        firestore = FirebaseFirestore.getInstance() ;

        state = login_state ;
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR , calendar.get(Calendar.YEAR) - UserConstants.MIN_AGE);
        binding.dpBirthdate.setMaxDate(calendar.getTime());


        toastMethods= new ToastMethods(this);
        avatars = new ArrayList<>();
        bannerAdapter = new AvatarsBannerAdapter();

        binding.progressLogin.setSmoothProgressDrawableColors(new int[]{getResources().getColor(R.color.light_main_color) , getResources().getColor(R.color.dark_main_color)});
        binding.progressRegister.setSmoothProgressDrawableColors(new int[]{getResources().getColor(R.color.light_main_color) , getResources().getColor(R.color.dark_main_color)});

        String currentEmail = auth.getCurrentUser().getEmail();
        binding.edEmail.setText(currentEmail);

        binding.edPassword.setText(UserConstants.getCurrentUser(this).getPassword());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit() ;
        prepareAvatars();
    }

    private void prepareForRegister() {
        // Animations
        AnimationMethods.slideOutRight(DurationConstants.DURATION_SHORT , binding.btnLogin);

        ViewMethods.visibleView(binding.edRePasswordLayout , binding.edUsernameLayout , binding.edBirthdateLayout
                , binding.rbGenderLayout , binding.btnRegister , binding.btnCancel , binding.bvpAvatars);
        ViewMethods.goneView(binding.btnPrepareRegister);
        AnimationMethods.flipInX(DurationConstants.DURATION_SHORT , binding.edRePasswordLayout , binding.edUsernameLayout , binding.edBirthdateLayout , binding.rbGenderLayout);

        AnimationMethods.bounceInDown(DurationConstants.DURATION_SHORT , binding.btnRegister , binding.btnCancel);
        AnimationMethods.slideInDown(DurationConstants.DURATION_SHORT , binding.bvpAvatars);

        changeState();
    }

    private void prepareForLogin(){

        // Animations
        AnimationMethods.slideInRight(DurationConstants.DURATION_SHORT, binding.btnLogin);
        AnimationMethods.flipOutX(DurationConstants.DURATION_SO_SHORT, new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                ViewMethods.goneView(binding.edRePasswordLayout , binding.edUsernameLayout , binding.edBirthdateLayout
                        , binding.rbGenderLayout, binding.btnRegister , binding.btnCancel);
                AnimationMethods.bounceInUp(DurationConstants.DURATION_SHORT , binding.btnPrepareRegister);
                ViewMethods.visibleView(binding.btnPrepareRegister);
            }
        }, binding.edRePasswordLayout , binding.edUsernameLayout , binding.edBirthdateLayout, binding.rbGenderLayout);

        AnimationMethods.slideOutUp(DurationConstants.DURATION_SHORT, new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                ViewMethods.goneView(binding.bvpAvatars);
            }
        }
        , binding.bvpAvatars);
        changeState();
    }

    private void changeState(){
        if (state.equals(login_state)){
            state=register_state ;}

        else if(state.equals(register_state)){
            state = login_state ;
        }
    }

    private UserMD getEnteredData(){

        Date birthdate  = binding.dpBirthdate.getDate();

        if (ViewMethods.isTextInputEmpty(binding.edEmail , binding.edPassword , binding.edRePassword , binding.edUsername))
            return null ;

        else if (birthdate == null){
            binding.dpBirthdate.shake();
            return null ;
        }

        String gender = UserConstants.GENDER_MALE ;

        if (binding.rbGenderLayout.getPosition()==1)
            gender = UserConstants.GENDER_FEMALE ;

        String email = ViewMethods.getText(binding.edEmail);
        String password = ViewMethods.getText(binding.edPassword);
        String rePassword = ViewMethods.getText(binding.edRePassword);
        String username = ViewMethods.getText(binding.edUsername);

        if (!password.equals(rePassword)){
            ViewMethods.editTextEmptyError(binding.edPassword , binding.edRePassword);
            return null ;
        }

        Date creationDate = Calendar.getInstance().getTime();

        int avatarRes = avatars.get(binding.bvpAvatars.getCurrentItem()).getAvatarRes();


        String avatar = String.valueOf(avatarRes) ;

        UserMD user = new UserMD(username ,email , password , gender , avatar , birthdate , creationDate) ;

        return user ;
    }

    private void startLoading(SmoothProgressBar progress){
        ViewMethods.enableView(false , binding.edEmail , binding.edPassword ,  binding.edRePassword , binding.edUsername
                , binding.dpBirthdate , binding.rbGenderLayout, binding.btnRegister , binding.btnCancel
                , binding.btnLogin , binding.btnPrepareRegister);

        ViewMethods.visibleView(progress);
    }

    private void stopLoading(){
        ViewMethods.enableView(true , binding.edEmail , binding.edPassword ,  binding.edRePassword , binding.edUsername
                , binding.dpBirthdate , binding.rbGenderLayout, binding.btnRegister , binding.btnCancel
                , binding.btnLogin , binding.btnPrepareRegister);

        ViewMethods.goneView(binding.progressRegister , binding.progressLogin);
    }
    private void saveCurrentUserData(UserMD user){
        editor.putString(UserConstants.CURRENT_UID , user.getUid());
        editor.putString(UserConstants.CURRENT_AVATAR , user.getAvatarRes());
        editor.putString(UserConstants.CURRENT_GENDER , user.getGender());
        editor.putString(UserConstants.CURRENT_PASSWORD , user.getPassword());
        editor.putString(UserConstants.CURRENT_EMAIL , user.getEmail());
        editor.putString(UserConstants.CURRENT_USERNAME , user.getUsername());
        editor.putLong(UserConstants.CURRENT_BIRTHDATE , user.getBirthdate().getTime());
        editor.putLong(UserConstants.CURRENT_CREATION_DATE , user.getCreationDate().getTime());

        editor.apply();
    }

    private void prepareAvatars(){

        avatars.add(new AvatarMD(R.drawable.avatar_man_1 , UserConstants.GENDER_MALE , "الداهية"));
        avatars.add(new AvatarMD(R.drawable.avatar_man_2 , UserConstants.GENDER_MALE , "اللعيب"));
        avatars.add(new AvatarMD(R.drawable.avatar_man_3 , UserConstants.GENDER_MALE , "جنتل مان"));
        avatars.add(new AvatarMD(R.drawable.avatar_man_4 , UserConstants.GENDER_MALE , "الهاكر"));
        avatars.add(new AvatarMD(R.drawable.avatar_man_5 , UserConstants.GENDER_MALE , "البروفيسور"));
        avatars.add(new AvatarMD(R.drawable.avatar_woman_1 , UserConstants.GENDER_FEMALE , "سمية"));
        avatars.add(new AvatarMD(R.drawable.avatar_woman_2 , UserConstants.GENDER_FEMALE , "ياسمين"));
        avatars.add(new AvatarMD(R.drawable.avatar_woman_3 , UserConstants.GENDER_FEMALE , "أزهار"));

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
                        if(binding.bvpAvatars.getCurrentItem() != position){
                            binding.bvpAvatars.setCurrentItem(position);
                        }
                    }
                });


        binding.bvpAvatars.create(avatars);
        binding.bvpAvatars.setCurrentItem(1);
        ViewMethods.invisibleView(binding.bvpAvatars);
    }

    private void syncAvatarsWithGender(String gender){

        String currentGender = avatars.get(binding.bvpAvatars.getCurrentItem()).getAvatarGender();
        if (currentGender.equals(gender))
            return;

        int avatarsSize = avatars.size() ;
        for (int i =0 ; i<avatarsSize ; i++){
            String avatarGender = avatars.get(i).getAvatarGender();

            if (avatarGender.equals(gender)){
                binding.bvpAvatars.setCurrentItem(i);
                return;
            }
        }
    }
}