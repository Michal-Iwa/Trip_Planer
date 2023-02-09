package com.example.tripplaner.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tripplaner.service.DirectionsService;
import com.google.maps.model.DirectionsResult;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DirectionsViewModel extends ViewModel {
    public MutableLiveData<List<DirectionsResult>> directionsResultMLD = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();
    public MutableLiveData<Boolean> errorGettingData = new MutableLiveData<>();

    private static List<DirectionsResult> directionsResult;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void refresh(String [] locations, int [] bestRoute) {
        loading.setValue(true);
        DirectionsService directionsService = DirectionsService.getInstance();
        directionsService.getDirections(locations, bestRoute, (result) -> {
            directionsResult = result;
            fetchDirections();
        });
    }

    private void fetchDirections() {

        compositeDisposable.add(
                ObservableDirections()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<DirectionsResult>>() {
                                           @Override
                                           public void onSuccess(List<DirectionsResult> result) {
                                               directionsResultMLD.setValue(result);
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

    static Single<List<DirectionsResult>> ObservableDirections() {
        return Single.defer(() -> Single.just(directionsResult));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
