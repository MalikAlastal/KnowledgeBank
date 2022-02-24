package com.nameisknowledge.knowledgebank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.databinding.ActivityRegisterBinding;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore fireStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //
        prepareActivity();
        //

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setDataForRegister(new User(binding.etFullName.getText().toString(),binding.etEmail.getText().toString(),binding.etPassword.getText().toString(),binding.etRePassword.getText().toString()));
                } catch (Exception e) {
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setDataForRegister(User user) throws Exception {
        checkMatching(user.getPassword(),user.getRePassword());
        auth.createUserWithEmailAndPassword(user.getEmail(),user.getPassword()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // here we set authUid as the Id for the User
                user.setId(Objects.requireNonNull(authResult.getUser()).getUid());
                // here we send Data to fire Store
                setUserDataForFireStore(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkMatching(String pass,String rePass) throws Exception {
        if (!TextUtils.equals(pass,rePass)){
            throw new Exception("Mismatching Passwords");
        }
    }

    // set Data for FireStore
    private void setUserDataForFireStore(User user){
        fireStore.collection("Users").document(user.getId()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(RegisterActivity.this, "Congrats!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // set Values
    private void prepareActivity(){
        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
    }
}