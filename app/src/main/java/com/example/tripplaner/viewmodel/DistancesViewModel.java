package com.example.tripplaner.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tripplaner.service.DistanceMatrixService;
import com.google.maps.model.DistanceMatrix;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.model.DistanceMatrixRow;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DistancesViewModel extends ViewModel {
    public MutableLiveData<DistanceMatrix> distanceMatrixMLD = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();
    public MutableLiveData<Boolean> errorGettingData = new MutableLiveData<>();

    private static DistanceMatrix distanceMatrix;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void refresh(List<LatLng> locations) {
        loading.setValue(true);
        DistanceMatrixService distanceMatrixService = DistanceMatrixService.getInstance();
        distanceMatrixService.getDistanceMatrix(locations, (result) -> {
            distanceMatrix = new DistanceMatrix(result.originAddresses, result.destinationAddresses, result.rows);
            fetchDistanceMatrix();
        });
    }

    private void fetchDistanceMatrix() {

        compositeDisposable.add(
                ObservableDistanceMatrix()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DistanceMatrix>() {
                                           @Override
                                           public void onSuccess(DistanceMatrix result) {
                                               distanceMatrixMLD.setValue(result);
                                               loading.setValue(false);
                                               errorGettingData.setValue(false);
                                           }

                                           @Override
                                           public void onError(Throwable e) {
                                               System.out.println("false");
                                               loading.setValue(false);
                                               errorGettingData.setValue(true);
                                           }
                                       }
                        ));

    }

    static Single<DistanceMatrix> ObservableDistanceMatrix() {
        return Single.defer(() -> Single.just(distanceMatrix));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
