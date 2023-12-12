
package com.example.parking_locator_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;


public class SavedLocationsListActivity extends AppCompatActivity implements DeletionCallback {

    @Override
    public void onDeletionComplete() {
        fetchSavedLocations();
    }


    private List<SavedLocation> savedLocations = new ArrayList<>();
    private LocationsAdapter locationsAdapter;
    private LocationRepository locationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_locations_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_saved_locations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationsAdapter = new LocationsAdapter(savedLocations);
        recyclerView.setAdapter(locationsAdapter);

        // Initialize location repository
        locationRepository = new LocationRepository(getApplication());

        // Fetch and display saved locations
        fetchSavedLocations();

        // Set click listener for items
        locationsAdapter.setOnItemClickListener(new LocationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SavedLocation clickedLocation = savedLocations.get(position);

                // Create an Intent to open the LocationDetailsActivity and pass the location details
                Intent intent = new Intent(SavedLocationsListActivity.this, LocationDetailsActivity.class);
                intent.putExtra("latitude", clickedLocation.getLatitude());
                intent.putExtra("longitude", clickedLocation.getLongitude());
                intent.putExtra("locationId", clickedLocation.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSavedLocations();
    }

    private void fetchSavedLocations() {
        locationRepository.getAllLocations(new LocationRepository.OnLocationsReceivedListener() {
            @Override
            public void onLocationsReceived(List<SavedLocation> locations) {
                Log.d("LocationFetch", "Number of locations fetched: " + locations.size());
                savedLocations.clear();
                savedLocations.addAll(locations);
                locationsAdapter.notifyDataSetChanged();
            }
        });
    }
}
