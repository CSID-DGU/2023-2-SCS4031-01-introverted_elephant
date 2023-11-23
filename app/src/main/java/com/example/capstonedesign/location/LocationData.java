package com.example.capstonedesign.location;

public class LocationData {
    double latitude;
    double longitude;
    String formattedTimestamp;

    public LocationData(double latitude, double longitude, String formattedTimestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.formattedTimestamp = formattedTimestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getFormattedTimestamp() {
        return formattedTimestamp;
    }

}
