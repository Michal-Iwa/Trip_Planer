package com.example.tripplaner.service;

import com.example.tripplaner.model.GeoApiContextSingleton;
import com.example.tripplaner.service.Callbacks.Directions.DirectionsCallback;
import com.example.tripplaner.service.Callbacks.Directions.SingularDirectionsCallback;
import com.example.tripplaner.service.Callbacks.DistanceMatrix.IndexesPair;
import com.example.tripplaner.service.Callbacks.DistanceMatrix.SingularDistanceMatrixCallback;
import com.google.common.escape.ArrayBasedUnicodeEscaper;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DirectionsService {
    private static final String API_KEY = "";

    public static DirectionsService instance;

    private DirectionsService() {}
    private static List<DirectionsResult> directionsResultList;
    private static AtomicInteger amountResults = new AtomicInteger(0);

    public static DirectionsService getInstance() {
        instance = new DirectionsService();
        return instance;
    }

    public void getDirections(String [] locations, int [] bestRoute,
                              final DirectionsCallback callback) {
        List<String> locList = new ArrayList<>();
        for(int i = 0; i < locations.length ; i++) {
            locList.add(locations[bestRoute[i]]);
        }
        locList.add(locations[bestRoute[0]]);
        int amountCalls = locList.size()/26+1;
        amountResults = new AtomicInteger(0);
        directionsResultList = new ArrayList<>();
        for(int i = 1; i <= amountCalls; i++) {
            String [] loc = locList
                    .subList((i-1)*25, (i == amountCalls ? locList.size() : i*25))
                    .stream().toArray(String[]::new);
            singleRequest(loc,  result -> {
                directionsResultList.add(result);
                if(amountResults.incrementAndGet() == amountCalls){
                    callback.myResponseCallback(directionsResultList);
                }
            });
        }

    }

    private void singleRequest(String [] locations, final
    SingularDirectionsCallback callback) {
        DirectionsApiRequest apiRequest = DirectionsApi.newRequest
                (GeoApiContextSingleton.getInstance());
        apiRequest = apiRequest.origin(locations[0]);
        apiRequest = apiRequest.waypoints(Arrays.copyOfRange(locations,1,
                locations.length-1));
        apiRequest = apiRequest.destination(locations[locations.length-1]);
        apiRequest = apiRequest.mode(TravelMode.WALKING);

        apiRequest.setCallback(new com.google.maps.PendingResult.
                Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                callback.myResponseCallback(result);
            }
            @Override
            public void onFailure(Throwable e) {
            }
        });
    }
}
