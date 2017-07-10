package cz.trakrco.trakr_app.di.modules;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import cz.trakrco.trakr_app.model.DatabaseHelper;
import cz.trakrco.trakr_app.model.LocalDBService;
import cz.trakrco.trakr_app.model.RemoteDBService;
import dagger.Module;
import dagger.Provides;

/**
 * Created by vlad on 05/07/2017.
 */
@Module
public class AppModule {

    private Application application;
    private FirebaseAuth auth;
    private RemoteDBService rdbService;
    private LocalDBService ldbService;

    public AppModule(Application application) {
        this.application = application;
        auth = FirebaseAuth.getInstance();
        rdbService = new RemoteDBService(FirebaseDatabase.getInstance().getReference());
        ldbService = new LocalDBService(new DatabaseHelper(application));
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    FirebaseAuth provideFirebaseAuth() {
        return auth;
    }

    @Provides
    @Singleton
    RemoteDBService provideRemoteDBService() {
        return rdbService;
    }

    @Provides
    @Singleton
    LocalDBService provideLocalDBService() {
        return ldbService;
    }
}
