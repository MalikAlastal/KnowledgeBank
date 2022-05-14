package com.nameisknowledge.knowledgebank.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.mapbox.geojson.Point;
import com.mapbox.maps.Style;
import com.nameisknowledge.knowledgebank.Methods.ToastMethods;
import com.nameisknowledge.knowledgebank.databinding.ActivityMapModeBinding;

import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MapModeActivity extends AppCompatActivity {

    ActivityMapModeBinding binding ;
    ActivityResultLauncher<String[]> locationArl ;
    private FusedLocationProviderClient locationProvider;
    Activity activity ;
    ToastMethods toastMethods ;

    LocationManager locationManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       prepareActivity();
    }

    private void prepareActivity(){
        activity = this ;

        locationArl = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        if (result.get("android.permission.ACCESS_COARSE_LOCATION")&&result.get("android.permission.ACCESS_FINE_LOCATION")){
                            findMyLocation(new LocationListener() {
                                @Override
                                public void onLocationChanged(@NonNull Location location) {
                                    toastMethods.info(location.getLatitude()+"");
                                    binding.mapView.getMapboxMap().getFreeCameraOptions()
                                            .lookAtPoint(Point.fromLngLat(location.getLongitude() , location.getLatitude()) , location.getAltitude());
                                }
                            });
                        }
                    }
                });

        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        binding.mapView.getMapboxMap().loadStyleUri(Style.LIGHT, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                locationArl.launch(new String[]{"android.permission.ACCESS_FINE_LOCATION"
                        , "android.permission.ACCESS_COARSE_LOCATION"});
            }
        });

        getWindow().setStatusBarColor(Color.parseColor("#55000000"));
        toastMethods = new ToastMethods(this);

    }

    @SuppressLint("MissingPermission")
    private void findMyLocation(LocationListener locationListener) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , TimeUnit.MILLISECONDS.convert(10 , TimeUnit.MINUTES) , 10 ,locationListener );
    }
}