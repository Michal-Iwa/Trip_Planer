package com.example.tripplaner.service.Callbacks.Directions;

import com.google.maps.model.DirectionsResult;

import java.util.List;

public interface DirectionsCallback {
    void myResponseCallback(List<DirectionsResult> result);
}
