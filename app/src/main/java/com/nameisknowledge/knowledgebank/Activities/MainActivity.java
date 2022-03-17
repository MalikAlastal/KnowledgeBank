package com.nameisknowledge.knowledgebank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nameisknowledge.knowledgebank.Adapters.PagerAdapter;
import com.nameisknowledge.knowledgebank.Adapters.UsersAdapter;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Fragments.BlankFragment;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.Methods.ToastMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.ModelClasses.RequestMD;
import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.Services.RequestsService;
import com.nameisknowledge.knowledgebank.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ToastMethods toastMethods ;
    List<UserMD> userMDs ;
    UsersAdapter usersAdapter  ;
    PagerAdapter pagerAdapter ;
    List<Fragment> fragments ;

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

        binding.btnSoloMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SoloModeActivity.class));
            }
        });

    }


    private void setQes(){
        for (int i=0;i<3;i++){
            FirebaseFirestore.getInstance().collection(FirebaseConstants.QUESTIONS_COLLECTION)
            .document(i+"")
            .set(new QuestionMD("How r u","hi",i))
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toastMethods.error(e.getMessage());
                }
            });
        }
    }

    private void prepareActivity(){
        binding.rvUsers.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new ZoomRecyclerLayout(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.rvUsers);
        binding.rvUsers.setNestedScrollingEnabled(false);


        binding.rvUsers.setLayoutManager(layoutManager);

        RequestsService.startActionFoo(this);

        toastMethods = new ToastMethods(this);
        userMDs = new ArrayList<>();


       usersAdapter =  new UsersAdapter(userMDs, new GenericListener<String>() {
            @Override
            public void getData(String Uid) {

                FirebaseFirestore.getInstance().collection(FirebaseConstants.REQUESTS_COLLECTION)
                        .document(Uid).collection(FirebaseConstants.CONTAINER_COLLECTION)
                        .add(new RequestMD(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                                ,FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                toastMethods.success("Request Send");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastMethods.error(e.getMessage());
                    }
                });
            }
        });

       binding.rvUsers.setAdapter(usersAdapter);

       fragments = new ArrayList<>();

       fragments.add(new BlankFragment());
       fragments.add(new BlankFragment());
       fragments.add(new BlankFragment());

       pagerAdapter = new PagerAdapter(this , fragments);
    }
}