package com.example.tripplaner.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private int id;
    private List<LatLng> route;

    public Route() {
        this.route = new ArrayList<>();
    }

    public void setRoute(List<LatLng> route){
        this.route = route;
    }

    public void addPlaceToVisit(LatLng place){
        route.add(place);
    }
    public void deletePlaceFromRoute(LatLng place){
        int min = 0;
        double minD = 1000000,tmp=0;
        for(int i = 0; i < route.size(); i++){
            tmp = calcDistance(place, route.get(i));
            if(minD > tmp){
                System.out.println(tmp);
                min = i;
                minD = tmp;
            }
        }
        route.remove(min);
    }

    private double calcDistance (LatLng place1, LatLng place2){
        return Math.sqrt(Math.pow((place1.longitude - place2.longitude),2)
                + Math.pow((place1.latitude - place2.latitude),2));
    }

    public List<LatLng> getRoute() {
        return route;
    }

    public Memento saveToMemento() {
        return new Memento(this.route);
    }

    public void restoreFromMemento(Memento memento) {
        this.route = memento.getSavedState();
    }

    public static class Memento {
        private final List<LatLng> route;

        public Memento(List<LatLng> stateToSave) {
            route = new ArrayList<>(stateToSave);
        }

        // accessible by outer class only
        private List<LatLng> getSavedState() {
            return route;
        }

    }
}
