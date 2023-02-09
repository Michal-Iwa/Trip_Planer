package com.example.tripplaner.model;

import java.io.Serializable;

public class Trip implements Serializable {
    private String tripId;
    private String tripName;
    private String polylinePointsString;
    private String encodedPlacesToVisit;


    public Trip() {
    }

    public Trip(String tripId, String tripName,
                String polylinePointsString, String encodedPlacesToVisit) {
        this.tripId = tripId;
        this.tripName = tripName;
        this.polylinePointsString = polylinePointsString;
        this.encodedPlacesToVisit = encodedPlacesToVisit;
    }

    public String getPolylinePointsString() {
        return polylinePointsString;
    }

    public void setPolylinePointsString(String polylinePointsString) {
        this.polylinePointsString = polylinePointsString;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getEncodedPlacesToVisit() {
        return encodedPlacesToVisit;
    }

    public void setEncodedPlacesToVisit(String encodedPlacesToVisit) {
        this.encodedPlacesToVisit = encodedPlacesToVisit;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripId='" + tripId + '\'' +
                ", tripName='" + tripName + '\'' +
                ", polylinePointsString='" + polylinePointsString + '\'' +
                ", encodedPlacesToVisit='" + encodedPlacesToVisit + '\'' +
                '}';
    }
}
