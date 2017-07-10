package cz.trakrco.trakr_app.di.modules;

import cz.trakrco.trakr_app.di.scopes.ActivityScope;
import cz.trakrco.trakr_app.view.auth.LoginFragment;
import cz.trakrco.trakr_app.view.auth.RegisterFragment;
import dagger.Module;
import dagger.Provides;

/**
 * Created by vlad on 04/07/2017.
 */
@Module
public class AuthActivityModule {

    private LoginFragment loginView;
    private RegisterFragment registerView;

    public AuthActivityModule(LoginFragment loginView, RegisterFragment registerView) {
        this.loginView = loginView;
        this.registerView = registerView;
    }

    @Provides
    @ActivityScope
    LoginFragment provideLoginView() {
        return loginView;
    }

    @Provides
    @ActivityScope
    RegisterFragment provideRegisterView() {
        return registerView;
    }
}
