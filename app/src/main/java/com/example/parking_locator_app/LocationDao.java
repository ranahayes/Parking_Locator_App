package com.example.parking_locator_app;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    void insert(SavedLocation location);

    @Query("SELECT * FROM saved_locations")
    List<SavedLocation> getAllLocations();

    // Additional DAO methods to be defined here
}

