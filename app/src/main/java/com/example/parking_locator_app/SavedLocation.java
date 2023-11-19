package com.example.parking_locator_app;

public class SavedLocation {
    private String name;
    private double latitude;
    private double longitude;

    // Constructor, getters, and setters
    public SavedLocation(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
