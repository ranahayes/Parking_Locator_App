package com.example.parking_locator_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LocationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);

        // Initialize views
        TextView locationDetailsText = findViewById(R.id.location_details_text);
        TextView locationInfoTextView = findViewById(R.id.location_info_text);
        Button navigateButton = findViewById(R.id.navigate_button);
        Button deleteButton = findViewById(R.id.delete_button);

        //Intent for locations detail activity
        Intent intent = getIntent();
        if (intent != null) {
            String locationDetails = intent.getStringExtra("key");
            // Display location details in the TextView
            locationInfoTextView.setText(locationDetails);
        }


        // Set click listeners for buttons
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementing navigation logic
                double latitude = intent.getDoubleExtra("latitude", 0.0);
                double longitude = intent.getDoubleExtra("longitude", 0.0);

                if (latitude == 0.0 || longitude == 0.0) {
                    Toast.makeText(LocationDetailsActivity.this, "Error: Invalid location data", Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementing deletion logic
                int locationId = intent.getIntExtra("locationId", -1);
                Log.d("LocationDetailsActivity", "Received location ID: " + locationId);
                if (locationId != -1) {
                    LocationRepository locationRepository = new LocationRepository(getApplication());
                    locationRepository.deleteLocationById(locationId);
                    Toast.makeText(LocationDetailsActivity.this, "Location Deleted", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after deletion
                } else {
                    Toast.makeText(LocationDetailsActivity.this, "Error: Location ID not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
