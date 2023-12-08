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


    private int id; // Unique identifier for each location
    private String address; // Optional, to store the address of the location
    private long timestamp; // Optional, to store the time when the location was saved
    private String notes; // Optional, to store any notes about the location

    // Modified constructor to include the new attributes
    public SavedLocation(int id, String name, double latitude, double longitude, String address, long timestamp, String notes) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    // Getter and setter methods for the new attributes
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
