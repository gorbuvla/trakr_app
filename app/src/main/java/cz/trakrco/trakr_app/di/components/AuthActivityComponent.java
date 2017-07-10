package cz.trakrco.trakr_app.di.components;

import cz.trakrco.trakr_app.di.modules.AuthActivityModule;
import cz.trakrco.trakr_app.di.scopes.ActivityScope;
import cz.trakrco.trakr_app.view.auth.LoginFragment;
import cz.trakrco.trakr_app.view.auth.RegisterFragment;
import dagger.Component;

/**
 * Created by vlad on 04/07/2017.
 */
@ActivityScope
@Component(modules = AuthActivityModule.class, dependencies = AppComponent.class)
public interface AuthActivityComponent {
    void inject(LoginFragment loginView);
    void inject(RegisterFragment registerView);
}
