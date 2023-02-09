package com.example.tripplaner.model;

import com.google.maps.GeoApiContext;

public class GeoApiContextSingleton {
    private static final String API_KEY =
            "";
    private static GeoApiContextSingleton
            instance = null;
    private static GeoApiContext context;
    private GeoApiContextSingleton() {
        context = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();
    }
    public static GeoApiContext getInstance() {
        if(instance == null) {
            instance = new GeoApiContextSingleton();
        }
        return context;
    }
}
