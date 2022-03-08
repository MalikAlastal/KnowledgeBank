package com.nameisknowledge.knowledgebank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.Constants.DurationConstants;
import com.nameisknowledge.knowledgebank.Methods.AnimationMethods;
import com.nameisknowledge.knowledgebank.Methods.ViewMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.databinding.ActivityLoginBinding;

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
                UserMD user = getEnteredData();

//                FirebaseAuth.getInstance().signInWithEmailAndPassword(user.getEmail() , user.getPassword()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                    @Override
//                    public void onSuccess(AuthResult authResult) {
//                        startActivity(new Intent(getBaseContext(),MainActivity.class));
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
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
                , binding.rbGenderLayout , binding.btnRegister , binding.btnCancel);

        auth = FirebaseAuth.getInstance() ;
        firestore = FirebaseFirestore.getInstance() ;

        state = login_state ;
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

        if (!ViewMethods.isTextInputEmpty(binding.edEmail , binding.edPassword , binding.edRePassword ,binding.edUsername)){

        }

        UserMD user = new UserMD();



        return user ;
    }
}