package cz.trakrco.trakr_app.di.components;

import cz.trakrco.trakr_app.di.modules.InputViewModule;
import cz.trakrco.trakr_app.di.scopes.FragmentScope;
import cz.trakrco.trakr_app.view.main.InputFragment;
import dagger.Component;

/**
 * Created by vlad on 05/07/2017.
 */
@FragmentScope
@Component(modules = InputViewModule.class, dependencies = {MainActivityComponent.class})
public interface InputViewComponent {
    void inject(InputFragment view);
}
