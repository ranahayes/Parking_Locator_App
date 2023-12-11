package com.example.parking_locator_app;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    void insert(SavedLocation location);

    @Query("SELECT * FROM saved_locations")
    List<SavedLocation> getAllLocations();

    @Query("DELETE FROM saved_locations WHERE id = :id")
    void deleteById(int id);


    @Update
    void update(SavedLocation location);

    @Query("SELECT * FROM saved_locations WHERE name LIKE :name")
    List<SavedLocation> findLocationsByName(String name);
}