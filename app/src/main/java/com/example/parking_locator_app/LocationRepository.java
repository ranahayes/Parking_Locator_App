package com.example.parking_locator_app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Room;

import java.util.List;


public class LocationRepository {

    private LocationDao locationDao;

    public LocationRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        locationDao = db.locationDao();
    }

    public interface OnLocationsReceivedListener {
        void onLocationsReceived(List<SavedLocation> locations);
    }

    @SuppressLint("StaticFieldLeak")
    public void getAllLocations(OnLocationsReceivedListener listener) {
        new AsyncTask<Void, Void, List<SavedLocation>>() {
            @Override
            protected List<SavedLocation> doInBackground(Void... voids) {
                List<SavedLocation> locations = locationDao.getAllLocations();
                Log.d("LocationRepository", "Fetched locations: " + locations);
                return locations;
            }

            @Override
            protected void onPostExecute(List<SavedLocation> locations) {
                listener.onLocationsReceived(locations);
            }
        }.execute();
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


    public void deleteLocationById(int id) {
        new deleteAsyncTask(locationDao).execute(id);
    }

    private static class deleteAsyncTask extends AsyncTask<Integer, Void, Void> {
        private LocationDao asyncTaskDao;

        deleteAsyncTask(LocationDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Integer... params) {
            asyncTaskDao.deleteById(params[0]);
            return null;
        }
    }


    public void update(SavedLocation location) {
        new updateAsyncTask(locationDao).execute(location);
    }

    private static class updateAsyncTask extends AsyncTask<SavedLocation, Void, Void> {
        private LocationDao asyncTaskDao;

        updateAsyncTask(LocationDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SavedLocation... params) {
            asyncTaskDao.update(params[0]);
            return null;
        }
    }

    // Asynchronous method to find locations by name
    @SuppressLint("StaticFieldLeak")
    public void findLocationsByName(String name, OnLocationsReceivedListener listener) {
        new AsyncTask<String, Void, List<SavedLocation>>() {
            @Override
            protected List<SavedLocation> doInBackground(String... strings) {
                return locationDao.findLocationsByName(strings[0]);
            }

            @Override
            protected void onPostExecute(List<SavedLocation> locations) {
                listener.onLocationsReceived(locations);
            }
        }.execute(name);
    }
}