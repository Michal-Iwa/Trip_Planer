package com.example.tripplaner.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tripplaner.R;
import com.example.tripplaner.model.adapter.RecyclerViewTripsAdapter;
import com.example.tripplaner.model.Trip;
import com.example.tripplaner.viewmodel.TripsViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class DisplayDataActivity extends AppCompatActivity {

    private Button createNewRouteBtn;
    private ProgressBar loadingDataPB;
    private TextView noDataTV;
    private RecyclerView tripsList;
    private SwipeRefreshLayout refreshLayout;

    private TripsViewModel tripsViewModel;
    private RecyclerViewTripsAdapter tripsAdapter = new RecyclerViewTripsAdapter(new ArrayList<>());


    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        createNewRouteBtn = findViewById(R.id.activity_display_data_btn_create_new_route);
        loadingDataPB = findViewById(R.id.activity_display_data_pg_loading_data);
        noDataTV = findViewById(R.id.activity_display_data_tv_no_data);
        tripsList = findViewById(R.id.activity_display_data_rv_trips);
        refreshLayout = findViewById(R.id.swipeRefreshLayout);

        firebaseAuth = FirebaseAuth.getInstance();

        createNewRouteBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapsChoosePlacesActivity.class);
            startActivity(intent);
        });

        refreshLayout.setOnRefreshListener(() -> {
            tripsViewModel.refresh();
            refreshLayout.setRefreshing(false);
        });
        refreshLayout.setRefreshing(false);


        tripsViewModel = new ViewModelProvider(this).get(TripsViewModel.class);
        tripsViewModel.refresh();

        tripsList.setLayoutManager(new LinearLayoutManager(this));
        tripsList.setAdapter(tripsAdapter);

        observerTripsViewModel();
    }

    private void observerTripsViewModel() {
        tripsViewModel.trips.observe(this, (Observer<List<Trip>>) trips -> {
            tripsList.setVisibility(View.VISIBLE);
            tripsAdapter.updateTrips(trips);
        });
        tripsViewModel.tripLoadError.observe(this, isError -> {
            if (isError != null) {
                noDataTV.setVisibility(isError ? View.VISIBLE : View.GONE);
            }
        });
        tripsViewModel.loading.observe(this, isLoading -> {
            if(isLoading != null){
                loadingDataPB.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if(isLoading) {
                    tripsList.setVisibility(View.GONE);
                    noDataTV.setVisibility(View.GONE);
                }
            }
        });
    }
}