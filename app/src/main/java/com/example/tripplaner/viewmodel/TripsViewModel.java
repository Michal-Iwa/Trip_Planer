package com.example.tripplaner.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tripplaner.service.TripsService;
import com.example.tripplaner.model.Trip;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class TripsViewModel extends ViewModel {

    public MutableLiveData<List<Trip>> trips = new MutableLiveData<>();
    public MutableLiveData<Boolean> tripLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>(true);

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private TripsService tripsService;

    public TripsViewModel() {
        super();
    }

    public void refresh() {
        loading.setValue(true);
        tripsService = TripsService.getInstance();
        tripsService.getTrips(this::fetchTrips);
    }

    public void fetchTrips(List<Trip> tripsDB) {
        compositeDisposable.add(
                ObservableTrips(tripsDB)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(
                                new DisposableSingleObserver<List<Trip>>() {
                            @Override
                            public void onSuccess(List<Trip> tripsList) {
                                trips.setValue(tripsList);
                                loading.setValue(false);
                                tripLoadError.setValue(false);
                            }
                            @Override
                            public void onError(Throwable e) {
                                System.out.println("false");
                                loading.setValue(false);
                                tripLoadError.setValue(true);
                            }
                        }
        ));
    }

    static Single<List<Trip>> ObservableTrips(List<Trip> tripsDB) {
        return Single.defer(() -> Single.just(tripsDB));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
