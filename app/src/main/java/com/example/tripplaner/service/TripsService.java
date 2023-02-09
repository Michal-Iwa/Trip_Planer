package com.example.tripplaner.service;


import com.example.tripplaner.model.Trip;
import com.example.tripplaner.service.Callbacks.Trips.TripsListCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TripsService{
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
    CollectionReference collectionReference =
            dataBase.collection("Users");
    String firebaseUserId = firebaseAuth.getCurrentUser().getUid();
    private List<Trip> trips = new ArrayList<>();

    public static TripsService instance;

    private TripsService() {}

    public static TripsService getInstance() {
        if(instance == null) {
            instance = new TripsService();
        }
        return instance;
    }

    public void getTrips(final TripsListCallback callback){
        collectionReference.document(firebaseUserId)
                .collection("Trips")
                .addSnapshotListener((value, error) -> {
                    if(value != null) {
                        List<DocumentSnapshot> tripsDocument =
                                new ArrayList<>(value.getDocuments());
                        trips = tripsDocument.stream()
                                .map(tripDocument ->
                                        tripDocument.toObject(Trip.class))
                                .collect(Collectors.toList());
                        callback.myResponseCallback(trips);

                    }
                });
    }
}
