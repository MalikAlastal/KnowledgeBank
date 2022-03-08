package com.nameisknowledge.knowledgebank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.databinding.ActivityLoginBinding;

import java.util.Calendar;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    public final static String[] keys = {"email","password"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    checkEmpty(setDataForKeyAndValue(keys,new EditText[]{binding.edEmail,binding.edPassword}));
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.edEmail.getText().toString(),binding.edPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            startActivity(new Intent(getBaseContext(),MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void checkEmpty(KeyAndValue[] keyAndValues) throws Exception {
        for (KeyAndValue keyAndValue:keyAndValues){
            if (keyAndValue.getValue().getText().toString().isEmpty()){
                throw new Exception(keyAndValue.getKey()+"Is Empty");
            }
        }
    }

    private KeyAndValue[] setDataForKeyAndValue(String[] keys,EditText[] values){
        KeyAndValue[] keyAndValues = new KeyAndValue[keys.length];
        for (int i=0;i<values.length;i++){
            keyAndValues[i] = new KeyAndValue(keys[i],values[i]);
        }
        return keyAndValues;
    }

    private UserMD setData() throws Exception {
        checkEmpty(setDataForKeyAndValue(keys,new EditText[]{binding.edEmail,binding.edPassword}));
        return new UserMD("as",binding.edEmail.getText().toString(),binding.edPassword.getText().toString(),"","", Calendar.getInstance().getTime(),Calendar.getInstance().getTime());
    }

    class KeyAndValue {
        private String key;
        private EditText value;

        public KeyAndValue(String key, EditText value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public EditText getValue() {
            return value;
        }

        public void setValue(EditText value) {
            this.value = value;
        }
    }
}