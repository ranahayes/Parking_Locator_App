package com.example.parking_locator_app;

import androidx.fragment.app.FragmentActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button saveLocationButton;
    private Button findCarButton;
    private TextView statusBar;

    // Constants
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the buttons and status bar text view
        saveLocationButton = findViewById(R.id.save_location_button);
        findCarButton = findViewById(R.id.find_location_button);
        statusBar = findViewById(R.id.status_bar);

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Set up the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up the button listeners
        setUpButtonListeners();
    }

    private void setUpButtonListeners() {
        saveLocationButton.setOnClickListener(view -> {
            // Save current location logic
            statusBar.setText(getString(R.string.location_saved));
            // More code to handle saving the location
        });

        findCarButton.setOnClickListener(view -> {
            // Find car logic
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Map Configurations here
        configureMap();
    }

    private void configureMap() {
        // Check if we have permission to access the fine location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            statusBar.setText(getString(R.string.location_permission_needed));
            // Consider calling ActivityCompat#requestPermissions here to request the missing permissions
        }
    }
}
