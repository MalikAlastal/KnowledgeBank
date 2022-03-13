package com.nameisknowledge.knowledgebank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.lazydatepicker.LazyDatePicker;
import com.nameisknowledge.knowledgebank.Constants.DurationConstants;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Constants.UserConstants;
import com.nameisknowledge.knowledgebank.Methods.AnimationMethods;
import com.nameisknowledge.knowledgebank.Methods.ViewMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.ActivityLoginBinding;

import java.util.Calendar;
import java.util.Date;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prepareActivity();

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ViewMethods.isTextInputEmpty(binding.edEmail , binding.edPassword)){
                    return;
                }

                String email = ViewMethods.getText(binding.edEmail);
                String password = ViewMethods.getText(binding.edPassword);

                startLoading(binding.progressLogin);

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email , password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(getBaseContext(),MainActivity.class));
                        stopLoading();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        stopLoading();
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
                                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    stopLoading();
                                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            stopLoading();
                        }
                    });
                }
                else {
                    Toast.makeText(getBaseContext(), "user null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareForLogin();
            }
        });
    }

    private void prepareActivity(){
        ViewMethods.goneView(binding.edRePasswordLayout , binding.edUsernameLayout , binding.edBirthdateLayout
                , binding.rbGenderLayout, binding.btnRegister , binding.btnCancel
                , binding.progressLogin , binding.progressRegister);

        auth = FirebaseAuth.getInstance() ;
        firestore = FirebaseFirestore.getInstance() ;

        state = login_state ;
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR , calendar.get(Calendar.YEAR) - UserConstants.MIN_AGE);
        binding.dpBirthdate.setMaxDate(calendar.getTime());

        binding.progressLogin.setSmoothProgressDrawableColors(new int[]{getResources().getColor(R.color.light_main_color) , getResources().getColor(R.color.dark_main_color)});
        binding.progressRegister.setSmoothProgressDrawableColors(new int[]{getResources().getColor(R.color.light_main_color) , getResources().getColor(R.color.dark_main_color)});
    }

    private void prepareForRegister() {

        // Animations
        AnimationMethods.slideOutRight(DurationConstants.DURATION_SHORT , binding.btnLogin);

        ViewMethods.visibleView(binding.edRePasswordLayout , binding.edUsernameLayout , binding.edBirthdateLayout
                , binding.rbGenderLayout , binding.btnRegister , binding.btnCancel);
        ViewMethods.goneView(binding.btnPrepareRegister);
        AnimationMethods.flipInX(DurationConstants.DURATION_SHORT , binding.edRePasswordLayout , binding.edUsernameLayout , binding.edBirthdateLayout , binding.rbGenderLayout);

        AnimationMethods.bounceInDown(DurationConstants.DURATION_SHORT , binding.btnRegister , binding.btnCancel);

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
        String avatar = "" ;

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

}