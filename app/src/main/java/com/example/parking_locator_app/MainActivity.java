package com.example.parking_locator_app;

import androidx.fragment.app.FragmentActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

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
    private Button viewSavedLocationsButton;
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

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Room database
        db = AppDatabase.getDatabase(getApplicationContext());
        locationDao = db.locationDao();

        locationDao = db.locationDao();

        // Initialize the buttons and status bar text view
        saveLocationButton = findViewById(R.id.save_location_button);
        findCarButton = findViewById(R.id.find_location_button);
        findCarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getLatestSavedLocation(new OnLatestLocationFetched() {
                        @Override
                        public void onLocationFetched(SavedLocation latestLocation) {
                            double latitude = latestLocation.getLatitude();
                            double longitude = latestLocation.getLongitude();

                            // Create the Google Maps navigation URI with the correct format
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);

                            // Create an intent to open Google Maps for navigation
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                            // Set the package to ensure it opens in the Google Maps app
                            mapIntent.setPackage("com.google.android.apps.maps");

                            // Check if there is an app that can handle the intent
                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(mapIntent);
                            } else {
                                // If no app can handle the intent, you can handle it here
                                // For example, you can open a web-based map in a browser.
                            }
                        }
                    });
                }
            });

        viewSavedLocationsButton = findViewById(R.id.view_saved_locations_button);
        viewSavedLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSavedLocations();
            }
        });
        statusBar = findViewById(R.id.status_bar);


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
        // Save Location button implementation
        saveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCurrentLocation();
            }
        });
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
        runOnUiThread(() -> {
            TextView statusBar = findViewById(R.id.status_bar);
            statusBar.setVisibility(View.VISIBLE);
            statusBar.setText("Location saved successfully");
        });
    }


    private void viewSavedLocations() {
        Intent intent = new Intent(MainActivity.this, SavedLocationsListActivity.class);
        startActivity(intent);
    }
    @SuppressLint("StaticFieldLeak")
    private void getLatestSavedLocation(OnLatestLocationFetched listener) {
        new AsyncTask<Void, Void, SavedLocation>() {
            @Override
            protected SavedLocation doInBackground(Void... voids) {
                // Correctly fetching a single SavedLocation object
                return locationDao.getLatestLocation();
            }
            @Override
            protected void onPostExecute(SavedLocation latestLocation) {
                // This runs on the main thread
                if (latestLocation != null) {
                    listener.onLocationFetched(latestLocation);
                } else {
                    Toast.makeText(MainActivity.this, "No saved location found", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    public interface OnLatestLocationFetched {
        void onLocationFetched(SavedLocation location);
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
            // Location saved

        });
    }
}
