package com.nameisknowledge.knowledgebank.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.maps.Style;
import com.nameisknowledge.knowledgebank.databinding.ActivityMapModeBinding;



public class MapModeActivity extends AppCompatActivity {

    ActivityMapModeBinding binding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mapView.getMapboxMap().loadStyleUri(Style.LIGHT);


    }
}