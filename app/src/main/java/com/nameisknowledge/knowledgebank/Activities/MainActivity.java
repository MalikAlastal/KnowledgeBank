package com.nameisknowledge.knowledgebank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nameisknowledge.knowledgebank.Adapters.UsersAdapter;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.ModelClasses.RequestMD;
import com.nameisknowledge.knowledgebank.ModelClasses.UserMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.Services.RequestsService;
import com.nameisknowledge.knowledgebank.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RequestsService.startActionFoo(this);

        binding.rv.setHasFixedSize(true);
        binding.rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        FirebaseFirestore.getInstance().collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                List<UserMD> userMDS = new ArrayList<>();
                for (QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots){
                    userMDS.add(queryDocumentSnapshot.toObject(UserMD.class));
                }

                binding.rv.setAdapter(new UsersAdapter(userMDS, new GenericListener<String>() {
                    @Override
                    public void getData(String Uid) {
                        FirebaseFirestore.getInstance().collection("Requests").document(Uid).collection("Container")
                                .add(new RequestMD(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(MainActivity.this, "Request Send", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.clickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SoloModeActivity.class));
            }
        });

    }


//    public void createNotification(){
//        String channelId = "message" ;
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this , channelId) ;
//        builder.setSmallIcon(R.drawable.ic_timer).setContent(getMyLayout());
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE) ;
//        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
//            NotificationChannel channel = new NotificationChannel(channelId , "Message" , NotificationManager.IMPORTANCE_DEFAULT);
//            manager.createNotificationChannel(channel);
//        }
//        manager.notify(0 , builder.build());
//    }
//
//    public RemoteViews getMyLayout(){
//        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews =
//                new RemoteViews(getApplicationContext().getPackageName() , R.layout.notification_layout) ;
//        Intent i = new Intent(getBaseContext() , DuoModeActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext() ,10 , i ,0);
//        remoteViews.setOnClickPendingIntent(R.id.notText , pendingIntent);
//        remoteViews.setTextViewText(R.id.notText , "Timer Finished");
//        return remoteViews ;
//    }
}