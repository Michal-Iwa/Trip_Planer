package com.example.tripplaner.model.bestRoute;

import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;

import java.util.ArrayList;
import java.util.List;


public class Travel {

    private List<City> travel = new ArrayList<>();
    private List<City> previousTravel = new ArrayList<>();


    public Travel(DistanceMatrix distanceMatrix) {
        int i = 0;
        for(DistanceMatrixRow row : distanceMatrix.rows){
            travel.add(new City(i,distanceMatrix));
            i++;
        }
    }


    public void swapCities() {
        int a = generateRandomIndex();
        int b = generateRandomIndex();
        previousTravel = new ArrayList<>(travel);
        City x = travel.get(a);
        City y = travel.get(b);
        travel.set(a, y);
        travel.set(b, x);
    }

    public void revertSwap() {
        travel = previousTravel;
    }

    private int generateRandomIndex() {
        return (int) (Math.random() * travel.size());
    }

    public City getCity(int index) {
        return travel.get(index);
    }

    public double getDistance() {
        double distance = 0;
        for (int index = 0; index < travel.size(); index++) {
            City starting = getCity(index);
            City destination;
            if (index + 1 < travel.size()) {
                destination = getCity(index + 1);
            } else {
                destination = getCity(0);
            }
            distance += starting.distanceToCity(destination);
        }
        return distance;
    }

    public List<City> getTravelList() {
        return new ArrayList<>(travel);
    }
}
