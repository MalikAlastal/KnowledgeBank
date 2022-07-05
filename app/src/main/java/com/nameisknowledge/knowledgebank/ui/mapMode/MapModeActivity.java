package com.nameisknowledge.knowledgebank.ui.mapMode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.Style;
import com.mapbox.maps.ViewAnnotationOptions;
import com.mapbox.maps.extension.observable.eventdata.CameraChangedEventData;
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.delegates.listeners.OnCameraChangeListener;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;
import com.nameisknowledge.knowledgebank.databinding.ActivityMapModeBinding;
import com.nameisknowledge.knowledgebank.databinding.MapAnnotationAreaBinding;
import com.nameisknowledge.knowledgebank.ui.attackArea.AttackAreaActivity;
import com.nameisknowledge.knowledgebank.constants.UserConstants;
import com.nameisknowledge.knowledgebank.methods.ToastMethods;
import com.nameisknowledge.knowledgebank.methods.ViewMethods;
import com.nameisknowledge.knowledgebank.modelClasses.MapAreaMD;
import com.nameisknowledge.knowledgebank.modelClasses.UserMD;
import com.nameisknowledge.knowledgebank.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.jvm.functions.Function1;


public class MapModeActivity extends AppCompatActivity {

    ActivityMapModeBinding binding;
    ActivityResultLauncher<String[]> locationArl;
    private FusedLocationProviderClient locationProvider;
    ToastMethods toastMethods;

    LocationManager locationManager;

    List<MapAreaMD> areas;

    UserMD currentUser;

    private RewardedAd rewardedAd;
    private InterstitialAd interstitialAd;

    Activity activity;

    MapModeViewModel viewModel;

    boolean isLocationFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapModeBinding.inflate(getLayoutInflater());
        getWindow().setStatusBarColor(getResources().getColor(R.color.map_gray));
        ViewMethods.setLocale(this, "en");
        setContentView(binding.getRoot());

        prepareActivity();

        viewModel.updatedUser.observe(this, user -> {
            UserConstants.setCurrentUser(user, getBaseContext());
        });

        viewModel.attackPoints.observe(this,points->{
            currentUser.setAreaAttackPoints(points);
            binding.tvAreaAttackPoints.setText(String.valueOf(points));
        });

        binding.cardPointsRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardedAd!=null)
                rewardedAd.show(activity, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        viewModel.updatedAttackPoints(UserConstants.getCurrentUser(getBaseContext()).getUid(), (UserConstants.getCurrentUser(getApplicationContext()).getAreaAttackPoints()+UserConstants.REWARD_AREA_ATTACK_POINTS));
                    }
                });
            }
        });

    }

    private void prepareActivity() {
        viewModel = new ViewModelProvider(this).get(MapModeViewModel.class);

        viewModel.getAreaAttackPoints(UserConstants.getCurrentUser(this).getUid());

        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        toastMethods = new ToastMethods();
        areas = new ArrayList<>();
        isLocationFound = false;

        activity = this;


        currentUser = UserConstants.getCurrentUser(this);

        binding.tvAreaAttackPoints.setText(String.valueOf(currentUser.getAreaAttackPoints()));

        locationArl = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        if (Boolean.TRUE.equals(result.get("android.permission.ACCESS_COARSE_LOCATION"))
                                && Boolean.TRUE.equals(result.get("android.permission.ACCESS_FINE_LOCATION"))) {
                            findMyLocation(location -> {
                                if (isLocationFound) {
                                    return;
                                }
                                Point point = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                                moveCamera(point, 10d);
                                isLocationFound = true;
                            });
                        }
                    }
                });

        prepareMap();
        prepareAds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getMapAreas();
        viewModel.mapAreas.observe(this,mapAreas -> {
            binding.mapView.getViewAnnotationManager().removeAllViewAnnotations();
            areas = mapAreas ;
            toastMethods.info(areas.size()+"");
            for (MapAreaMD area:mapAreas) {
                addAreaView(area);
            }
        });
        
        binding.tvAreaAttackPoints.setText(String.valueOf(UserConstants.getCurrentUser(this).getAreaAttackPoints()));
        isLocationFound = false;
    }

    private void prepareMap() {
        binding.mapView.getMapboxMap().loadStyleUri(Style.LIGHT, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                locationArl.launch(new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"});
            }
        });
        binding.mapView.setMaximumFps(60);
        binding.mapView.getMapboxMap().addOnCameraChangeListener(new OnCameraChangeListener() {
            @Override
            public void onCameraChanged(@NonNull CameraChangedEventData cameraChangedEventData) {
            }
        });
    }

    private void prepareAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd mRewardedAd) {
                        rewardedAd = mRewardedAd;
                        toastMethods.info("loaded");
                    }
                });

    }

    @SuppressLint("MissingPermission")
    private void findMyLocation(android.location.LocationListener locationListener) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2500, 1, locationListener);
        //  locationListener.onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
    }

    private void moveCamera(Point point, double zoom) {
        binding.mapView.getMapboxMap().cameraAnimationsPlugin(new Function1<CameraAnimationsPlugin, Object>() {
            @Override
            public Object invoke(CameraAnimationsPlugin cameraAnimationsPlugin) {
                cameraAnimationsPlugin.easeTo(new CameraOptions.Builder().center(point).zoom(zoom).build(),
                        new MapAnimationOptions.Builder().duration(1500).build());
                return null;
            }
        });
    }

    private void addAreaView(MapAreaMD area) {

        ViewAnnotationManager manager = binding.mapView.getViewAnnotationManager();

        ViewAnnotationOptions options = new ViewAnnotationOptions.Builder()
                .geometry(Point.fromLngLat(area.getAreaLng(), area.getAreaLat()))
                //.associatedFeatureId(area.getAreaName())
                .visible(true)
                .build();

        View view = manager.addViewAnnotation(R.layout.map_annotation_area, options);

        MapAnnotationAreaBinding annotationBinding = MapAnnotationAreaBinding.bind(view);

        annotationBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (area.getQuestionList().size() == 0) {
                    return;
                }
                int buttonsVisibility = annotationBinding.buttonsLayout.getVisibility();

                if (buttonsVisibility == View.VISIBLE) {
                    annotationBinding.buttonsLayout.setVisibility(View.GONE);
                } else {
                    annotationBinding.buttonsLayout.setVisibility(View.VISIBLE);
                }

                moveCamera(Point.fromLngLat(area.getAreaLng(), area.getAreaLat()), 13d);
            }
        });
        annotationBinding.tvAreaName.setText(area.getAreaName());

        annotationBinding.btnAttack.setOnClickListener(getAttackClickListener(area));
        if (area.getOwnerUser() != null) {
            try {
                annotationBinding.ownerInfoLayout.setVisibility(View.VISIBLE);
                annotationBinding.ivUserImage.setImageResource(Integer.parseInt(area.getOwnerUser().getAvatarRes()));
                annotationBinding.tvUserUsername.setText(area.getOwnerUser().getUsername());
            } catch (Exception e) {
            }

        }
    }

    private View.OnClickListener getAttackClickListener(MapAreaMD area) {
        return view -> {
            if (currentUser.getAreaAttackPoints() > 0) {
                Intent intent = new Intent(getBaseContext(), AttackAreaActivity.class);
                intent.putExtra(AttackAreaActivity.AREA_KEY, area);
                startActivity(intent);
            } else {
                toastMethods.warning(getString(R.string.attack_points_error));
            }
        };
    }

    private void closeAllAnnotation() {
        for (MapAreaMD area : areas) {
            View annotation = binding.mapView.getViewAnnotationManager().getViewAnnotationByFeatureId(area.getAreaName());
            if (annotation == null) {
                return;
            }
            MapAnnotationAreaBinding areaBinding = MapAnnotationAreaBinding.bind(annotation);

            areaBinding.buttonsLayout.setVisibility(View.GONE);
            toastMethods.info(areaBinding.tvAreaName.getText().toString());
        }
    }
}