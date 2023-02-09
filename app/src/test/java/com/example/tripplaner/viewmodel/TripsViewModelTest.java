package com.example.tripplaner.viewmodel;

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;


import com.example.tripplaner.model.Trip;
import com.example.tripplaner.service.Callbacks.Trips.TripsListCallback;
import com.example.tripplaner.service.TripsService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;

public class TripsViewModelTest {

    TripsViewModel tripsViewModel = new TripsViewModel();
    @Before
    public void setupRxSchedulers() {
        Scheduler immediate = new Scheduler() {
            @Override
            public Worker createWorker() {
                return new ExecutorScheduler.ExecutorWorker(runnable ->
                {runnable.run();}, true);
            }
        };
        RxJavaPlugins.setInitNewThreadSchedulerHandler(scheduler -> immediate);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);
    }
    @Test
    public void getTripsSuccess() {
        Trip trip = new Trip("1","Wycieczka do Honolulu",
                "polylinePoints", "encodedPlaces");
        ArrayList<Trip> tripsList = new ArrayList<>();
        tripsList.add(trip);
        tripsViewModel.fetchTrips(tripsList);

        Assert.assertEquals(1,tripsViewModel.trips.getValue().size());
        Assert.assertEquals(false,tripsViewModel.loading.getValue());
        Assert.assertEquals(false,tripsViewModel.tripLoadError.getValue());
    }
    @Test
    public void getTripsFail() {

        tripsViewModel.fetchTrips(null);

        Assert.assertEquals(false,tripsViewModel.loading.getValue());
        Assert.assertEquals(true,tripsViewModel.tripLoadError.getValue());
    }
}
