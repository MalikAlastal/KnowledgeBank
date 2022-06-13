package com.nameisknowledge.knowledgebank.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.mapbox.maps.extension.observable.eventdata.CameraChangedEventData;
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.delegates.listeners.OnCameraChangeListener;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;
import com.nameisknowledge.knowledgebank.Constants.FirebaseConstants;
import com.nameisknowledge.knowledgebank.Listeners.GenericListener;
import com.nameisknowledge.knowledgebank.Methods.ToastMethods;
import com.nameisknowledge.knowledgebank.ModelClasses.MapAreaMD;
import com.nameisknowledge.knowledgebank.R;
import com.nameisknowledge.knowledgebank.databinding.ActivityMapModeBinding;
import com.nameisknowledge.knowledgebank.databinding.MapAnnotationAreaBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.jvm.functions.Function1;


public class MapModeActivity extends AppCompatActivity {

    ActivityMapModeBinding binding ;
    ActivityResultLauncher<String[]> locationArl ;
    private FusedLocationProviderClient locationProvider;
    Activity activity ;
    ToastMethods toastMethods ;

    LocationManager locationManager ;

    FirebaseFirestore firestore ;

    List<MapAreaMD> areas  ;

    boolean isLocationFound ;
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
        areas = new ArrayList<>();
        isLocationFound = false ;

//        List<MapQuestionMD> questionList = new ArrayList<>() ;
//        questionList.add(new MapQuestionMD("What is name of this area" , "الوسطى1" , 1));
//        questionList.add(new MapQuestionMD("What is name of this area" , "الوسطى2" , 2));
//        questionList.add(new MapQuestionMD("What is name of this area" , "الوسطى3" , 3));
//        questionList.add(new MapQuestionMD("What is name of this area" , "الوسطى4" , 4));
//
//        MapAreaMD areaMD = new MapAreaMD("الوسطى" ,  35.363371 ,32.4072699 , null , 0 , questionList );
//
//        firestore.collection(FirebaseConstants.MAP_AREAS_COLLECTION).document(areaMD.getAreaName()).set(areaMD);

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
                                    if (isLocationFound){
                                        return;
                                    }
                                    Point point = Point.fromLngLat(location.getLongitude() , location.getLatitude()) ;
                                    moveCamera(point);
                                    isLocationFound = true ;
                                }
                            });
                        }
                    }
                });

        prepareMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMapAreas(new GenericListener<List<MapAreaMD>>() {
            @Override
            public void getData(List<MapAreaMD> mapAreas) {
                binding.mapView.getViewAnnotationManager().removeAllViewAnnotations();
                areas = mapAreas ;
                toastMethods.info(areas.size()+"");
                for (MapAreaMD area:mapAreas) {
                    addAreaView(area);
                }
            }
        });

        isLocationFound = false ;
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

        binding.mapView.getMapboxMap().addOnCameraChangeListener(new OnCameraChangeListener() {
            @Override
            public void onCameraChanged(@NonNull CameraChangedEventData cameraChangedEventData) {
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void findMyLocation(android.location.LocationListener locationListener) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , 2500 , 1 ,locationListener );
      //  locationListener.onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
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

        ViewAnnotationManager manager = binding.mapView.getViewAnnotationManager();

        ViewAnnotationOptions options = new ViewAnnotationOptions.Builder()
                .geometry(Point.fromLngLat(area.getAreaLng() , area.getAreaLat()))
                .associatedFeatureId(area.getAreaName())
                .visible(true)
                .build() ;

        View view = manager.addViewAnnotation(R.layout.map_annotation_area,options );

            MapAnnotationAreaBinding annotationBinding = MapAnnotationAreaBinding.bind(view);

            annotationBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    annotationBinding.buttonsLayout.setVisibility(View.VISIBLE);
                }
            });
            annotationBinding.tvAreaName.setText(area.getAreaName());

            annotationBinding.btnAttack.setOnClickListener(getAttackClickListener(area));
            if (area.getOwnerUser()!=null){
                annotationBinding.ownerInfoLayout.setVisibility(View.VISIBLE);
                annotationBinding.ivUserImage.setImageResource(Integer.parseInt(area.getOwnerUser().getAvatarRes()));
                annotationBinding.tvUserUsername.setText(area.getOwnerUser().getUsername());
            }
    }

    private void getMapAreas(GenericListener<List<MapAreaMD>> listener){
        firestore.collection(FirebaseConstants.MAP_AREAS_COLLECTION).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<MapAreaMD> areas = queryDocumentSnapshots.toObjects(MapAreaMD.class);
                        listener.getData(areas);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMethods.error("why ??");
            }
        });
    }

    private View.OnClickListener getAttackClickListener(MapAreaMD area){
        return view -> {
            Intent intent = new Intent(getBaseContext() , AttackAreaActivity.class);
            intent.putExtra(AttackAreaActivity.AREA_KEY , area);
            startActivity(intent);
        };
    }

    private void closeAllAnnotation(){
        for (MapAreaMD area:areas) {
            View annotation = binding.mapView.getViewAnnotationManager().getViewAnnotationByFeatureId(area.getAreaName());
            if (annotation == null){
                return;
            }
            MapAnnotationAreaBinding areaBinding = MapAnnotationAreaBinding.bind(annotation);

            areaBinding.buttonsLayout.setVisibility(View.GONE);
            toastMethods.info(areaBinding.tvAreaName.getText().toString());
        }
    }
}