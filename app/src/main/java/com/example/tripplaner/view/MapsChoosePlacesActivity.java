package com.example.tripplaner.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripplaner.R;
import com.example.tripplaner.model.FindBestRoute;
import com.example.tripplaner.model.Route;
import com.example.tripplaner.model.Trip;
import com.example.tripplaner.view.dialogs.SaveTripDialog;
import com.example.tripplaner.viewmodel.DirectionsViewModel;
import com.example.tripplaner.viewmodel.DistancesViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.tripplaner.databinding.ActivityMapsChoosePlacesBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapsChoosePlacesActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, SaveTripDialog.SaveTripDialogListener {

    private GoogleMap mMap;
    private ActivityMapsChoosePlacesBinding binding;

    private Route route = new Route();
    private List<Route.Memento> savedRoutes = new ArrayList<>();
    private Trip currentTrip;

    private DistancesViewModel distancesViewModel;
    private DirectionsViewModel directionsViewModel;

    private static DistanceMatrix currentDistanceMatrix;
    private static List<DirectionsResult> currentDirectionsResults;

    private String encodedPolyline;
    private List<com.google.maps.model.LatLng> decodedPolyline;


    private Button undoBtn;
    private Button calculateRouteBtn;
    private Button saveRouteBtn;
    private ProgressBar getDistancesLoadingPB;
    private TextView distanceErrorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsChoosePlacesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.amcp_map);
        mapFragment.getMapAsync(this);

        //widgets
        undoBtn = findViewById(R.id.amcp_undo_btn);
        calculateRouteBtn = findViewById(R.id.amcp_calculate_route_btn);
        getDistancesLoadingPB = findViewById(R.id.amcp_pg_loading_data);
        distanceErrorTV = findViewById(R.id.amcp_tv_distances_error);
        saveRouteBtn = findViewById(R.id.amcp_btn_save_route);

        distancesViewModel = new ViewModelProvider(this).get(DistancesViewModel.class);
        observerDistancesViewModel();

        directionsViewModel = new ViewModelProvider(this).get(DirectionsViewModel.class);
        observerDirectionsViewModel();

        //click listeners
        undoBtn.setOnClickListener(v -> {
            if(savedRoutes.size() >= 1) {
                route.restoreFromMemento(savedRoutes.get(savedRoutes.size()-1));
                savedRoutes.remove(savedRoutes.size()-1);
                mMap.clear();
                route.getRoute().forEach(latLng -> mMap.addMarker(
                        new MarkerOptions().draggable(true).position(latLng)));
            }
            else{ Toast.makeText(this, "Nothing to undo",Toast.LENGTH_SHORT).show(); }
        });

        calculateRouteBtn.setOnClickListener(v -> {
            if(route.getRoute().size() > 2){
                distancesViewModel.refresh(route.getRoute());
            }
            else { Toast.makeText(this,"Mark more places", Toast.LENGTH_SHORT).show(); }

        });
        saveRouteBtn.setOnClickListener( v -> {
            //pop up window
            SaveTripDialog saveTripDialog = new SaveTripDialog();
            saveTripDialog.show(getSupportFragmentManager(), "Save Trip Dialog");
        });

    }

    private void observerDirectionsViewModel() {
        directionsViewModel.directionsResultMLD.observe(this,  directionsResult -> {
            mMap.clear();
            currentDirectionsResults = new ArrayList<>(directionsResult);
            decodedPolyline = new ArrayList<>();
            encodedPolyline = "";
            for (DirectionsResult result : currentDirectionsResults) {
                encodedPolyline += result.routes[0].overviewPolyline.getEncodedPath();
                decodedPolyline.addAll(result.routes[0].overviewPolyline.decodePath());
            }
            route.getRoute().stream().forEach(latLng -> mMap.addMarker(new MarkerOptions().draggable(true).position(latLng)));

            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(decodedPolyline.stream()
                            .map(latLng -> new LatLng(latLng.lat,latLng.lng))
                            .collect(Collectors.toList()))
                    .width(5)
                    .color(Color.BLUE));
            saveRouteBtn.setVisibility(View.VISIBLE);
        });
    }

    private void observerDistancesViewModel() {
        distancesViewModel.distanceMatrixMLD.observe(this, (Observer<DistanceMatrix>) distanceMatrix -> {
            currentDistanceMatrix = new DistanceMatrix(
                    distanceMatrix.originAddresses,
                    distanceMatrix.destinationAddresses,
                    distanceMatrix.rows);
            new FindBestRoute(distanceMatrix);
            directionsViewModel.refresh(distanceMatrix.originAddresses, FindBestRoute.bestRoute);
        });
        distancesViewModel.errorGettingData.observe(this, isError -> {
            if (isError != null) {
                distanceErrorTV.setVisibility(isError ? View.VISIBLE : View.GONE);
            }
        });
        distancesViewModel.loading.observe(this, isLoading -> {
            if(isLoading != null){
                getDistancesLoadingPB.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if(isLoading) {
                    distanceErrorTV.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.addMarker(new MarkerOptions().draggable(true).position(latLng));
        savedRoutes.add(route.saveToMemento());
        route.addPlaceToVisit(latLng);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        savedRoutes.add(route.saveToMemento());
        route.deletePlaceFromRoute(marker.getPosition());
        marker.remove();
        return true;
    }
    @Override
    public void onMarkerDrag(@NonNull Marker marker) {}

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        route.addPlaceToVisit(marker.getPosition());
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
        savedRoutes.add(route.saveToMemento());
        route.deletePlaceFromRoute(marker.getPosition());
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

    @Override
    public void applyTripTitle(String tripsName) {
        saveTripToDatabase(tripsName);
    }

    private void saveTripToDatabase(String tripName) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currUserId = firebaseAuth.getCurrentUser().getUid();
        FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
        CollectionReference collectionReference =
                dataBase.collection("Users");

        CollectionReference tripsReference = collectionReference
                .document(currUserId)
                .collection("Trips");
        DocumentReference tripDoc = tripsReference.document();
        String tripId = tripDoc.getId();
        Trip tripToSave = new Trip(tripId,tripName,encodedPolyline,
                PolyUtil.encode(route.getRoute()));
        tripDoc.set(tripToSave)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MapsChoosePlacesActivity.this,
                                "Trip successfully saved",
                                Toast.LENGTH_SHORT ).show();
                        Intent intent = new Intent(
                                MapsChoosePlacesActivity.this,
                                DisplayDataActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsChoosePlacesActivity.this,
                                "Error, couldn't save the trip",
                                Toast.LENGTH_SHORT ).show();
                        Intent intent = new Intent(
                                MapsChoosePlacesActivity.this,
                                DisplayDataActivity.class);
                        startActivity(intent);
                    }
                });
    }
}