package com.example.parking_locator_app;

import androidx.fragment.app.FragmentActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

// Additional imports for GPS and Room database
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import android.app.AlertDialog;


// Room database imports
import androidx.room.Room;

import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button saveLocationButton;
    private Button findCarButton;
    private Button viewSavedLocationsButton;
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
        View statusBar = findViewById(R.id.status_bar);
        findCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatestSavedLocation(new OnLatestLocationFetched() {
                    @Override
                    public void onLocationFetched(SavedLocation latestLocation) {
                        double latitude = latestLocation.getLatitude();
                        double longitude = latestLocation.getLongitude();

                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");


                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        } else {
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
        fetchAndDisplaySavedLocations();
    }
    private void saveCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                openSaveLocationDialog(location.getLatitude(), location.getLongitude());
                            }
                        }
                    });
        }
    }

    private void openSaveLocationDialog(final double latitude, final double longitude) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Save Location");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_save_location, null);
        builder.setView(dialogView);

        final EditText editTextLocationName = dialogView.findViewById(R.id.editTextLocationName);
        final EditText editTextLocationNotes = dialogView.findViewById(R.id.editTextLocationNotes);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String locationName = editTextLocationName.getText().toString();
                String locationNotes = editTextLocationNotes.getText().toString();
                saveLocationToDatabase(latitude, longitude, locationName, locationNotes);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void displaySavedLocationsOnMap(List<SavedLocation> locations) {
        if (mMap != null) {
            BitmapDescriptor carIcon = BitmapDescriptorFactory.fromResource(R.drawable.car_icon);
            for (SavedLocation location : locations) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(location.getName()));
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(location.getName())
                        .icon(carIcon));

            }
        }
    }
    private void fetchAndDisplaySavedLocations() {
        // Fetch locations from the database and update the map
        AsyncTask.execute(() -> {
            List<SavedLocation> locations = locationDao.getAllLocations();
            runOnUiThread(() -> {
                if (mMap != null) {
                    mMap.clear();
                    displaySavedLocationsOnMap(locations);
                }
            });
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
    private void saveLocationToDatabase(double latitude, double longitude, String name, String notes) {
        AsyncTask.execute(() -> {
            // Create a SavedLocation object with default values
            long currentTime = System.currentTimeMillis();
            String defaultAddress = "Addr " + currentTime;

            String defaultName;
            SavedLocation savedLocation = new SavedLocation(
                    0,
                    name,
                    latitude,
                    longitude,
                    defaultAddress,
                    currentTime,
                    notes
            );


            // Insert location into database
            locationDao.insert(savedLocation);
            runOnUiThread(this::fetchAndDisplaySavedLocations);
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Location saved successfully", Toast.LENGTH_SHORT).show();
            });



        });
    }
}
