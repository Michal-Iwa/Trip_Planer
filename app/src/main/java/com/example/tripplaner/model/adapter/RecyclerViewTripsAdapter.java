package com.example.tripplaner.model.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripplaner.R;
import com.example.tripplaner.model.Trip;
import com.example.tripplaner.view.MapsDisplayTripActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class RecyclerViewTripsAdapter extends RecyclerView.Adapter<RecyclerViewTripsAdapter.ViewHolder>{

    private List<Trip> trips;


    public RecyclerViewTripsAdapter(List<Trip> trips) {
        this.trips = trips;
    }


    public void updateTrips(List<Trip> trips) {
        this.trips.clear();
        this.trips.addAll(trips);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tripNameTV;
        public Button deleteRouteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            tripNameTV = itemView.findViewById(R.id.item_trip_tv_name);
            deleteRouteBtn = itemView.findViewById(R.id.item_trip_btn_delete_trip);
        }

        public void bind(Trip trip) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = dataBase.collection("Users");
            String firebaseUserId = firebaseAuth.getCurrentUser().getUid();

            tripNameTV.setText(trip.getTripName());

            tripNameTV.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), MapsDisplayTripActivity.class);
                intent.putExtra("Trip",trip);
                v.getContext().startActivity(intent);
            });

            deleteRouteBtn.setOnClickListener(v -> {
                        DocumentReference docRef = collectionReference
                                .document(firebaseUserId)
                                .collection("Trips")
                                .document(trip.getTripId());
                        docRef.delete().addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Toast.makeText(v.getContext(), "Trip successfully deleted",Toast.LENGTH_SHORT ).show();
                            }
                            else {
                                Toast.makeText(v.getContext(), "Couldn't delete this trip",Toast.LENGTH_SHORT ).show();
                            }
                        });
                    });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(trips.get(position));
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

}
