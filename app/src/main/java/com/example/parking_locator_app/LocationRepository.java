
package com.example.parking_locator_app;

import android.app.Application;
import android.os.AsyncTask;

import androidx.room.Room;

import java.util.List;

public class LocationRepository {
    private LocationDao locationDao;
    private List<SavedLocation> allLocations;

    public LocationRepository(Application application) {
        AppDatabase db = Room.databaseBuilder(application.getApplicationContext(),
                AppDatabase.class, "location_database").build();
        locationDao = db.locationDao();
        allLocations = locationDao.getAllLocations();
    }

    public void insert(SavedLocation location) {
        new insertAsyncTask(locationDao).execute(location);
    }

    private static class insertAsyncTask extends AsyncTask<SavedLocation, Void, Void> {
        private LocationDao asyncTaskDao;

        insertAsyncTask(LocationDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SavedLocation... params) {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
