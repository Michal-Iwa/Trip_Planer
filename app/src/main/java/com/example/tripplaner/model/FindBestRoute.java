package com.example.tripplaner.model;

import com.example.tripplaner.model.bestRoute.City;
import com.example.tripplaner.model.bestRoute.Travel;
import com.google.maps.model.DistanceMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FindBestRoute {
    public static int[] bestRoute;
    private DistanceMatrix distanceMatrix;
    private static final int MAX_INT = 2147483647;
    private int bestDuration;

    public FindBestRoute(DistanceMatrix distanceMatrix) {
        bestRoute = new int [distanceMatrix.rows.length];
        bestDuration = MAX_INT;
        this.distanceMatrix = distanceMatrix;
        if(distanceMatrix.originAddresses.length < 12) {
            BruteForce();
        } else {
            for(int j = 0; j < 10; j++){
                System.out.println("Proba: " + j);
                SimulatedAnnealing(100000,0.9999);
            }
        }
    }
    private void BruteForce() {
        int n = distanceMatrix.destinationAddresses.length;
        int[] elements = new int[n];
        int[] indexes = new int[n];
        for (int i = 0; i < n; i++) {
            indexes[i] = 0;
            elements[i] = i;
        }
        int k = 0;
        int minDuration = MAX_INT;
        int [] curBestRoute = Arrays.copyOf(elements,n);
        int duration = 0;
        for(int p = 0; p<n-1; p++){
            duration += distanceMatrix.rows
                    [elements[p]].elements[elements[p+1]]
                    .duration.inSeconds;
        }
        duration += distanceMatrix.rows[elements[n-1]]
                .elements[elements[0]].duration.inSeconds;
        minDuration = duration;
        while (k < n-1) {
            if (indexes[k] < k) {
                swap(elements, k % 2 == 0 ?  0: indexes[k], k);
                duration = 0;
                for(int p = 0; p<n-1; p++){
                    duration += distanceMatrix.rows
                            [elements[p]].elements[elements[p+1]]
                            .duration.inSeconds;
                }
                duration += distanceMatrix.rows[elements[n-1]]
                        .elements[elements[0]].duration.inSeconds;
                if(duration<minDuration){
                    minDuration = duration;
                    curBestRoute = Arrays.copyOf(elements,n);
                }
                indexes[k]++;
                k = 0;
            }
            else {
                indexes[k] = 0;
                k++;
            }

        }
        bestRoute = curBestRoute;
        bestDuration = minDuration;
        System.out.println("Best case: " + minDuration);
    }

    private void SimulatedAnnealing(
            double startingTemperature,
            double coolingRate) {
        Travel travel = new Travel(distanceMatrix);
        double t = startingTemperature;
        double bestDistance = travel.getDistance();
        Travel bestSolution = travel;
        Travel currentSolution = bestSolution;
        List<City> cityList = new ArrayList<>();

        int i =0;
        boolean q = true;
        long millisActualTime = System.currentTimeMillis();
        for (i = 0; t > 0.1; i++) {
            currentSolution.swapCities();
            double currentDistance = currentSolution.getDistance();
            if (currentDistance < bestDistance) {
                if(bestDuration == currentDistance && q==true) {
                    System.out.println("Iteracja: "+i);
                    long executionTime = System.currentTimeMillis() - millisActualTime;
                    System.out.println("Znaleziono po czasie " + executionTime + " milisekund");
                    q=false;
                }
                bestDistance = currentDistance;
                cityList = currentSolution.getTravelList();
            } else if (Math.exp((bestDistance - currentDistance)
                    / t) < Math.random()) {
                currentSolution.revertSwap();
            }
            t *= coolingRate;
        }
        if(bestDuration > bestDistance) {
            Integer [] tmp;
            tmp = cityList.stream().map(City::getIndex).toArray(Integer[]::new);
            bestRoute = Arrays.stream(tmp).mapToInt(Integer::intValue).toArray();
            bestDuration = (int) bestDistance;
        }
    }

    private static void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }

}
