package com.example.parking_locator_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class SavedLocationsListActivity extends AppCompatActivity {

    private List<SavedLocation> savedLocations = new ArrayList<>();
    private LocationsAdapter locationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_locations_list);

        // Dummy data for testing
        savedLocations.add(new SavedLocation("Home", 40.712776, -74.005974));
        savedLocations.add(new SavedLocation("Work", 34.052235, -118.243683));

        RecyclerView recyclerView = findViewById(R.id.recycler_view_saved_locations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationsAdapter = new LocationsAdapter(savedLocations);
        recyclerView.setAdapter(locationsAdapter);
    }
}

