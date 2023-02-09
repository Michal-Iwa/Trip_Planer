package com.example.tripplaner.model.bestRoute;

import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;

public class City {

    private int index;
    private DistanceMatrixElement [] elements;

    public City(int index,DistanceMatrix distanceMatrix) {
        this.index = index;
        elements = distanceMatrix.rows[index].elements;
    }

    public double distanceToCity(City city) {
        return (double) elements[city.index].duration.inSeconds;
    }
    public int getIndex(){
        return index;
    }

}
