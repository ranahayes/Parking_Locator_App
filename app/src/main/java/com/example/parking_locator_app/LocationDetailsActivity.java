package com.example.parking_locator_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
                // Will eventually implement navigation logic (e.g., open Google Maps for navigation)
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement deletion logic (e.g., remove location from the list)
            }
        });
    }
}
