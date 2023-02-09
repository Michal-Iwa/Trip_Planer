package com.example.tripplaner.view;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.tripplaner.R;
import com.example.tripplaner.model.Trip;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.tripplaner.databinding.ActivityMapsDisplayTripBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class MapsDisplayTripActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsDisplayTripBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsDisplayTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseAuth = FirebaseAuth.getInstance();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            trip = (Trip) getIntent().getSerializableExtra("Trip");

        }
    }

    private void showEncodedPlacesToVisit(String encodedPlacesToVisit) {
        List<LatLng> placesToVisit = new ArrayList<>();
        placesToVisit = PolyUtil.decode(encodedPlacesToVisit);
        for(LatLng latLng : placesToVisit) {
            mMap.addMarker(new MarkerOptions().position(latLng).draggable(false));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(placesToVisit.get(0)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    private void showEncodedPolyline(String encodedPolyline) {
        List<LatLng> polylinePoints = new ArrayList<>();
        System.out.println(encodedPolyline);
        polylinePoints = PolyUtil.decode(encodedPolyline);

        Polyline line = mMap.addPolyline(new PolylineOptions()
                .addAll(polylinePoints)
                .width(5)
                .color(Color.BLUE));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        showEncodedPolyline(trip.getPolylinePointsString());
        showEncodedPlacesToVisit(trip.getEncodedPlacesToVisit());
    }
}