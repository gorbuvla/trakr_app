package cz.trakrco.trakr_app.di.modules;

import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;

import cz.trakrco.trakr_app.domain.User;
import cz.trakrco.trakr_app.di.scopes.ActivityScope;
import cz.trakrco.trakr_app.view.main.MainActivity;
import dagger.Module;
import dagger.Provides;

/**
 * Created by vlad on 05/07/2017.
 */
@Module
public class MainActivityModule {

    private GoogleApiClient googleApiClient;

    public MainActivityModule(MainActivity activity) {

        googleApiClient = new GoogleApiClient
                .Builder(activity)
                .enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
                })
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Provides
    @ActivityScope
    GoogleApiClient provideGoogleApiClient() {
        return googleApiClient;
    }

    @Provides
    @ActivityScope
    User provideUser(FirebaseAuth auth) { return new User(auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail()); }

}
