package com.nameisknowledge.knowledgebank.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.Style;
import com.mapbox.maps.ViewAnnotationOptions;
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.Methods.ToastMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.MapAreaMD;
import com.nameisknowledge.knowledgebank.ModelClasses.MapQuestion;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.ActivityMapModeBinding;
import com.nameisknowledge.knowledgebank.databinding.MapAnnotationAreaBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import kotlin.jvm.functions.Function1;


public class MapModeActivity extends AppCompatActivity {

    ActivityMapModeBinding binding ;
    ActivityResultLauncher<String[]> locationArl ;
    private FusedLocationProviderClient locationProvider;
    Activity activity ;
    ToastMethods toastMethods ;

    LocationManager locationManager ;

    FirebaseFirestore firestore ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapModeBinding.inflate(getLayoutInflater());
        getWindow().setStatusBarColor(Color.parseColor("#55000000"));
        setContentView(binding.getRoot());

       prepareActivity();
    }

    private void prepareActivity(){
        activity = this ;

        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        toastMethods = new ToastMethods(this);
        firestore = FirebaseFirestore.getInstance() ;

        List<MapQuestion> questionList = new ArrayList<>() ;
        questionList.add(new MapQuestion("What is name of this area" , "KhanYonus1" , 1));
        questionList.add(new MapQuestion("What is name of this area" , "KhanYonus2" , 2));
        questionList.add(new MapQuestion("What is name of this area" , "KhanYonus3" , 3));
        questionList.add(new MapQuestion("What is name of this area" , "KhanYonus4" , 4));

        MapAreaMD areaMD = new MapAreaMD("KhanYonus" ,  34.328448 ,31.359106 , questionList);

        firestore.collection(FirebaseConstants.MAP_AREAS_COLLECTION).document(areaMD.getAreaName()).set(areaMD);


        locationArl = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        if (Boolean.TRUE.equals(result.get("android.permission.ACCESS_COARSE_LOCATION"))
                                && Boolean.TRUE.equals(result.get("android.permission.ACCESS_FINE_LOCATION"))){
                            toastMethods.info(",,,");
                            findMyLocation(new android.location.LocationListener() {
                                @Override
                                public void onLocationChanged(@NonNull Location location) {
                                    Point point = Point.fromLngLat(location.getLongitude() , location.getLatitude()) ;
                                    toastMethods.info(location.getLatitude()+"");
                                    moveCamera(point);
                                }
                            });
                        }
                    }
                });

        prepareMap();

        getMapAreas(new GenericListener<List<MapAreaMD>>() {
            @Override
            public void getData(List<MapAreaMD> mapAreas) {
                for (MapAreaMD area:mapAreas) {
                    addAreaView(area);
                }
            }
        });
    }

    private void prepareMap(){
        binding.mapView.getMapboxMap().loadStyleUri(Style.LIGHT, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                locationArl.launch(new String[]{"android.permission.ACCESS_FINE_LOCATION"
                        , "android.permission.ACCESS_COARSE_LOCATION"});
            }
        });

        binding.mapView.setMaximumFps(60);

    }

    @SuppressLint("MissingPermission")
    private void findMyLocation(android.location.LocationListener locationListener) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , TimeUnit.MILLISECONDS.convert(10 , TimeUnit.MINUTES) , 10 ,locationListener );
        locationListener.onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
    }

    private void moveCamera(Point point){
        binding.mapView.getMapboxMap().cameraAnimationsPlugin(new Function1<CameraAnimationsPlugin, Object>() {
            @Override
            public Object invoke(CameraAnimationsPlugin cameraAnimationsPlugin) {
                cameraAnimationsPlugin.easeTo(new CameraOptions.Builder().center(point).zoom(10d).build() ,
                        new MapAnimationOptions.Builder().duration(1500).build());
                return null;
            }
        });
    }

    private void addAreaView(MapAreaMD area){
        try {
        ViewAnnotationManager manager = binding.mapView.getViewAnnotationManager();
        View view = manager.addViewAnnotation(R.layout.map_annotation_area,
                new ViewAnnotationOptions.Builder().geometry(Point.fromLngLat(area.getAreaLng() , area.getAreaLat())).allowOverlap(true).build());

            MapAnnotationAreaBinding annotationBinding = MapAnnotationAreaBinding.bind(view);

            annotationBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    annotationBinding.buttonsLayout.setVisibility(View.VISIBLE);
                }
            });
            annotationBinding.tvAreaName.setText(area.getAreaName());
        }catch (Exception ignored){

        }
    }

    private void getMapAreas(GenericListener<List<MapAreaMD>> listener){
        firestore.collection(FirebaseConstants.MAP_AREAS_COLLECTION).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<MapAreaMD> areas = queryDocumentSnapshots.toObjects(MapAreaMD.class);
                        listener.getData(areas);
                        toastMethods.info("lksfdjlasf");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMethods.error("why ??");
            }
        });
    }
}