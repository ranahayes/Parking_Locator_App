package com.example.parking_locator_app;

import androidx.fragment.app.FragmentActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

// Additional imports for GPS and Room database
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

// Room database imports
import androidx.room.Room;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button saveLocationButton;
    private Button findCarButton;
    private TextView statusBar;
    private FusedLocationProviderClient fusedLocationClient;

    // Database objects
    private AppDatabase db;
    private LocationDao locationDao;

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

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Room database
        db = AppDatabase.getDatabase(getApplicationContext());
        locationDao = db.locationDao();

        locationDao = db.locationDao();

        // Check for location permissions
        checkLocationPermission();

        // Set up the map fragment
        setUpMapFragment();

        // Set up the button listeners
        setUpButtonListeners();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void setUpMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setUpButtonListeners() {
        saveLocationButton.setOnClickListener(view -> saveCurrentLocation());
        findCarButton.setOnClickListener(view -> viewSavedLocations());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check permission and enable My Location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Request permission
        }

        // Additional setup for the map

        mMap = googleMap;
        // Additional setup for the map
    }


    private void saveCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            // Saving location to the database directly
                            saveLocationToDatabase(location.getLatitude(), location.getLongitude());
                        }
                    });
        }
    }


    private void viewSavedLocations() {
        Intent intent = new Intent(MainActivity.this, SavedLocationsListActivity.class);
        startActivity(intent);
    }



    // Method to save location to the database
    private void saveLocationToDatabase(double latitude, double longitude) {
        AsyncTask.execute(() -> {
            // Create a SavedLocation object with default values
            long currentTime = System.currentTimeMillis();
            String defaultName = "Location " + currentTime;
            String defaultAddress = "Addr " + currentTime;
            String defaultNotes = "Notes: " + currentTime;

            SavedLocation savedLocation = new SavedLocation(
                    0,
                    defaultName,
                    latitude,
                    longitude,
                    defaultAddress,
                    currentTime,
                    defaultNotes
            );


            // Insert location into database
            locationDao.insert(savedLocation);
            runOnUiThread(() -> {
                Intent intent = new Intent(MainActivity.this, SavedLocationsListActivity.class);
                startActivity(intent);
            });

        });
    }
}
