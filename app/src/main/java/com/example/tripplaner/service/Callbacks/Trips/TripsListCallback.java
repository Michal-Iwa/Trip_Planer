package com.example.tripplaner.service.Callbacks.Trips;

import com.example.tripplaner.model.Trip;

import java.util.List;

public interface TripsListCallback {
    void myResponseCallback(List<Trip> result);
}
