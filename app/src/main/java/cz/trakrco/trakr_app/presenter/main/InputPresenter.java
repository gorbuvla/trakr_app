package cz.trakrco.trakr_app.presenter.main;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import javax.inject.Inject;

import cz.trakrco.trakr_app.domain.Location;
import cz.trakrco.trakr_app.domain.Output;
import cz.trakrco.trakr_app.domain.User;
import cz.trakrco.trakr_app.model.StorageService;
import cz.trakrco.trakr_app.view.main.InputFragment;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by vlad on 05/07/2017.
 */

public class InputPresenter {

    private GoogleApiClient googleApiClient;
    private InputFragment view;
    private StorageService storage;
    private User user;


    @Inject
    public InputPresenter(InputFragment fragment, GoogleApiClient client, StorageService storageService, User user) {
        this.googleApiClient = client;
        this.view = fragment;
        this.storage = storageService;
        this.user = user;
    }


    public void save(String activityLabel, Location location, Integer hours, Integer minutes, Integer storageIdentifier) {

        //dummy validation
        if (activityLabel == null || activityLabel.isEmpty()) {
            view.onError("Check label field");
            return;
        }
        if (location == null) {
            view.onError("Use proper location. Either autocomplete or place picker");
            return;
        }
        if (hours == null || minutes == null) {
            view.onError("Check entered time");
            return;
        }
        if (!validate(activityLabel, hours, minutes)) {
            view.onError("Check supplied info");
            return;
        }
        retrievePlaceAndSave(activityLabel, location, hours, minutes, storageIdentifier);
    }

    public void save(String activityLabel, Place place, Integer hours, Integer minutes, final Integer storageIdentifier) {
        view.showLoading();

        Integer time = hours*60 + minutes;
        String address = place.getName() + ", " + place.getAddress().toString();
        Double latitude = place.getLatLng().latitude;
        Double longitude = place.getLatLng().longitude;

        String mode = storageIdentifier == 0 ? "LOCAL" : "REMOTE";

        final Output output = new Output(activityLabel, address, latitude, longitude, mode, time, user);

        storage.save(output, storageIdentifier)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Output>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Output output) {
                        view.stopLoading();
                        Log.i("OnNext", "main thread: " + onMainThread());
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        view.onError("Error while saving");
                        Log.e("InputPresenter.save", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.onSuccess();
                    }
                });
    }


    private void retrievePlaceAndSave(final String activityLabel, Location location, final Integer hours, final Integer minutes, final Integer storageIdentifier) {

        if (!googleApiClient.isConnected()) {
            Log.e("InputPresenter.place", "GoogleApiCLient not connected");
            return;
        }

        Places.GeoDataApi.getPlaceById(googleApiClient, location.getId()).setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                if (places.getStatus().isSuccess()) {
                    Place place = places.get(0);
                    save(activityLabel, place, hours, minutes, storageIdentifier);
                }
                places.release();
            }
        });
    }

    private boolean validate(String activityLabel, Integer hours, Integer minutes) {

        if (activityLabel.length() > 32) {
            view.onError("Label is 32 digits max");
            return false;
        }

        if (hours > 23 || hours < 0) {
            view.onError("Supply valid hour time, pls");
            return false;
        }

        if (minutes > 59 || minutes < 0) {
            view.onError("Supply valid minute time, pls");
            return false;
        }
        return true;
    }


    private boolean onMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


}
