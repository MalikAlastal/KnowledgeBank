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
import com.nameisknowledge.knowledgebank.Adapters.MainBannerAdapter;
import com.nameisknowledge.knowledgebank.Adapters.PagerAdapter;
import com.nameisknowledge.knowledgebank.Adapters.UsersAdapter;
import com.nameisknowledge.knowledgebank.Constants.DurationConstants;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Fragments.BlankFragment;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.Methods.ToastMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.ModeMD;
import com.nameisknowledge.knowledgebank.ModelClasses.QuestionMD;
import com.nameisknowledge.knowledgebank.ModelClasses.RequestMD;
import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.Services.RequestsService;
import com.nameisknowledge.knowledgebank.databinding.ActivityMainBinding;
import com.zhpan.bannerview.BannerViewPager;
import com.zhpan.bannerview.constants.PageStyle;
import com.zhpan.indicator.enums.IndicatorSlideMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ToastMethods toastMethods ;
    List<UserMD> userMDs ;
    UsersAdapter usersAdapter  ;

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
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(this));

        RequestsService.startActionFoo(this);

        toastMethods = new ToastMethods(this);
        userMDs = new ArrayList<>();


       usersAdapter =  new UsersAdapter(userMDs, new GenericListener<String>() {
            @Override
            public void getData(String uid) {
                sendRequest(uid);
            }
        });

       binding.rvUsers.setAdapter(usersAdapter);
       prepareBannerViewPager();
    }

    private void prepareBannerViewPager(){
        MainBannerAdapter bannerAdapter = new MainBannerAdapter();

        binding.bannerViewPager.setAdapter(bannerAdapter)
                .setLifecycleRegistry(getLifecycle())
                .setPageStyle(PageStyle.MULTI_PAGE_OVERLAP)
                .setScrollDuration(DurationConstants.DURATION_SO_SHORT)
                .setRevealWidth(5 , 5)
                .setPageMargin(getResources().getDimensionPixelOffset(R.dimen._85sdp))
                .setAutoPlay(false)
                .setCanLoop(false)
                .setIndicatorVisibility(View.GONE)
                .setOnPageClickListener(new BannerViewPager.OnPageClickListener() {
                    @Override
                    public void onPageClick(View clickedView, int position) {
                        if(binding.bannerViewPager.getCurrentItem() != position){
                            binding.bannerViewPager.setCurrentItem(position);
                        }
                        else {
                            switch (position){
                                case 0 :
                                soloModeListener(); break;
                                case 1 :
                                duoModeListener(); break;
                                case 2 :
                                mapModeListener(); break;
                            }
                        }
                    }
                });;

        List<ModeMD> modes = new ArrayList<>();

        modes.add(new ModeMD(R.string.mode_solo , R.drawable.ic_solo_mode , getResources().getColor(R.color.dark_main_color)));
        modes.add(new ModeMD(R.string.duo_mode , R.drawable.ic_duo_mode , getResources().getColor(R.color.dark_main_color)));
        modes.add(new ModeMD(R.string.map_mode , R.drawable.ic_map_mode , getResources().getColor(R.color.dark_main_color)));

        binding.bannerViewPager.create(modes);

        binding.bannerViewPager.setCurrentItem(1 , true);
    }

    private void soloModeListener(){
        Intent goSoloActivity = new Intent(this , SoloModeActivity.class);
        startActivity(goSoloActivity);
    }

    private void duoModeListener(){
        Intent goSoloActivity = new Intent(this , DuoModeActivity.class);
        startActivity(goSoloActivity);
    }

    private void mapModeListener(){
        toastMethods.warning("هذا المود غير متاح حاليا");
    }
    private void sendRequest(String uid){

        FirebaseFirestore.getInstance().collection(FirebaseConstants.REQUESTS_COLLECTION)
                .document(uid).collection(FirebaseConstants.CONTAINER_COLLECTION)
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
}