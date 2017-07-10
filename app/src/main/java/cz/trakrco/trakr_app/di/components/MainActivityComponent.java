package cz.trakrco.trakr_app.di.components;

import com.google.android.gms.common.api.GoogleApiClient;

import cz.trakrco.trakr_app.domain.User;
import cz.trakrco.trakr_app.di.modules.MainActivityModule;
import cz.trakrco.trakr_app.di.scopes.ActivityScope;
import cz.trakrco.trakr_app.model.StorageService;
import cz.trakrco.trakr_app.view.main.MainActivity;
import dagger.Component;

/**
 * Created by vlad on 05/07/2017.
 */
@ActivityScope
@Component(modules = MainActivityModule.class, dependencies = AppComponent.class)
public interface MainActivityComponent {
    void inject(MainActivity activity);
    GoogleApiClient providesGoogleApiClient();
    User providesUser();
    StorageService providesStorageService();
}
