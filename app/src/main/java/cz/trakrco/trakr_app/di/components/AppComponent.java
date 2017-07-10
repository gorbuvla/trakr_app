package cz.trakrco.trakr_app.di.components;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import cz.trakrco.trakr_app.di.modules.AppModule;
import cz.trakrco.trakr_app.model.LocalDBService;
import cz.trakrco.trakr_app.model.RemoteDBService;
import dagger.Component;

/**
 * Created by vlad on 05/07/2017.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    FirebaseAuth providesFirebaseAuth();
    RemoteDBService providesRemoteDBService();
    LocalDBService providesLocalDBService();

}
