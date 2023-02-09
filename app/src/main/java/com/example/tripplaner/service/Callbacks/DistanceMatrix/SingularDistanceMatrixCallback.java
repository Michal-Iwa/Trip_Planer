package com.example.tripplaner.service.Callbacks.DistanceMatrix;

import com.google.maps.model.DistanceMatrix;

public interface SingularDistanceMatrixCallback {
    void myResponseCallback(int i, int j, DistanceMatrix result);
}
