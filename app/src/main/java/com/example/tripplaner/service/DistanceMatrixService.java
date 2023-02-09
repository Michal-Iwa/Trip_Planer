package com.example.tripplaner.service;

import com.example.tripplaner.service.Callbacks.DistanceMatrix.IndexesPair;
import com.example.tripplaner.service.Callbacks.DistanceMatrix.SingularDistanceMatrixCallback;
import com.google.android.gms.maps.model.LatLng;

import com.example.tripplaner.model.GeoApiContextSingleton;
import com.example.tripplaner.service.Callbacks.DistanceMatrix.DistanceMatrixCallback;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.TravelMode;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DistanceMatrixService {

    private static final String API_KEY = "";

    public static DistanceMatrixService instance;

    private DistanceMatrixService() {}
    public static DistanceMatrix distanceMatrix;
    private static Map<IndexesPair,DistanceMatrix> distanceMatrixMap = new HashMap<>();
    private static AtomicInteger amountResults = new AtomicInteger(0);

    public static DistanceMatrixService getInstance() {
        if(instance == null)  instance = new DistanceMatrixService();
        return instance;
    }

    public void getDistanceMatrix (List<LatLng> list,
                                   final DistanceMatrixCallback callback) {
        com.google.maps.model.LatLng [] latLngs =
                list.stream()
                        .map(latLng -> new com.google.maps.model.LatLng
                                (latLng.latitude, latLng.longitude))
                        .toArray(com.google.maps.model.LatLng[]::new);

        int amountRows = (list.size()-1)/10+1;
        int amountColumns = (list.size()-1)/10+1;
        int amountCalls = (int) Math.pow(amountRows,2);

        distanceMatrixMap = new HashMap<>();
        amountResults = new AtomicInteger(0);
        for(int i = 1; i <= amountRows; i++) {
            for (int j = 1; j<= amountColumns; j++) {
                com.google.maps.model.LatLng[] origins = Arrays
                        .copyOfRange(latLngs, i*10-10,
                                ((i==amountRows) ? list.size() : i*10));
                com.google.maps.model.LatLng[] destinations = Arrays
                        .copyOfRange(latLngs, j*10-10,
                                ((j==amountColumns) ? list.size() : j*10));
                singleRequest(i,j, origins,destinations,
                        (indexOrigins, indexDestinations, result) -> {
                    distanceMatrixMap.putIfAbsent(new IndexesPair
                            (indexOrigins,indexDestinations),result);
                    if(amountResults.incrementAndGet() == amountCalls)
                        mergeDistanceMatrices(callback, amountCalls,
                                amountRows, amountColumns,list.size());
                });
            }
        }

    }

    public void print(DistanceMatrix distanceMatrix) {
        System.out.println("Got it!");
        System.out.println("origins: ");
        Arrays.stream(distanceMatrix.originAddresses).forEach(System.out::println);
        System.out.println("destinations: ");
        Arrays.stream(distanceMatrix.destinationAddresses).forEach(System.out::println);
        System.out.println("elements: ");
        System.out.println("rows amount: " +distanceMatrix.rows.length);
        for (DistanceMatrixRow row : distanceMatrix.rows) {
            System.out.println("elements amount: " + row.elements.length);
            for (DistanceMatrixElement element : row.elements) {
                System.out.println(element);
            }
        }

    }

    private void mergeDistanceMatrices(final DistanceMatrixCallback callback, int amountCalls,
                                       int amountRows, int amountColumns, int amountPlaces) {
        distanceMatrix = new DistanceMatrix(new String[0], new String[0], new DistanceMatrixRow[0]);
        for(int i = 1; i <= amountRows; i++) {
            DistanceMatrix tmp1 = distanceMatrixMap.get(new IndexesPair(i,1));
            if(i==1){
                distanceMatrix = new DistanceMatrix(
                        concatenate(distanceMatrix.originAddresses, tmp1.originAddresses),
                        concatenate(distanceMatrix.destinationAddresses, tmp1.destinationAddresses),
                        concatenate(distanceMatrix.rows,tmp1.rows));
            } else {
                distanceMatrix = new DistanceMatrix(
                        concatenate(distanceMatrix.originAddresses, tmp1.originAddresses),
                        distanceMatrix.destinationAddresses,
                        concatenate(distanceMatrix.rows,tmp1.rows));
            }
            for (int j = 2; j <= amountColumns; j++) {
                if (distanceMatrix != null && distanceMatrixMap.containsKey(new IndexesPair(i,j))) {
                    DistanceMatrix tmp = distanceMatrixMap.get(new IndexesPair(i,j));
                    DistanceMatrixRow[] rows1 = distanceMatrix.rows;
                    DistanceMatrixRow[] rows2 = tmp.rows;

                    for(int k = 0; k < rows2.length; k++) {
                        rows1[k+(i-1)*10].elements = concatenate(rows1[k+(i-1)*10].elements,rows2[k].elements) ;
                    }
                    if(i==1) {
                        distanceMatrix = new DistanceMatrix(
                                distanceMatrix.originAddresses,
                                concatenate(distanceMatrix.destinationAddresses, tmp.destinationAddresses),
                                rows1);
                    } else{
                        distanceMatrix = new DistanceMatrix(
                                distanceMatrix.originAddresses,
                                distanceMatrix.destinationAddresses,
                                rows1);
                    }
                }
            }
        }
        callback.myResponseCallback(distanceMatrix);
    }
    public <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    private void singleRequest(int i, int j,com.google.maps.model.LatLng[] origins,
                               com.google.maps.model.LatLng[] destinations,
                               final SingularDistanceMatrixCallback callback) {
        DistanceMatrixApiRequest apiRequest = DistanceMatrixApi.newRequest(GeoApiContextSingleton.getInstance());
        apiRequest = apiRequest.origins(origins);
        apiRequest = apiRequest.destinations(destinations);
        apiRequest = apiRequest.mode(TravelMode.WALKING);

        apiRequest.setCallback(new com.google.maps.PendingResult.Callback<DistanceMatrix>() {
            @Override
            public void onResult(DistanceMatrix result) {
                callback.myResponseCallback(i,j,result);
            }

            @Override
            public void onFailure(Throwable e) {
            }
        });
    }
}
