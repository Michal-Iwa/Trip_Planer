package com.example.tripplaner.service.Callbacks.Directions;

import com.google.maps.model.DirectionsResult;


public interface SingularDirectionsCallback {
    void myResponseCallback(DirectionsResult result);
}
