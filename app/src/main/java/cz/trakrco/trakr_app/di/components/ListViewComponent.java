package cz.trakrco.trakr_app.di.components;

import cz.trakrco.trakr_app.di.modules.ListViewModule;
import cz.trakrco.trakr_app.di.scopes.FragmentScope;
import cz.trakrco.trakr_app.view.main.ListFragment;
import dagger.Component;

/**
 * Created by vlad on 09/07/2017.
 */
@FragmentScope
@Component(modules = ListViewModule.class, dependencies = {MainActivityComponent.class})
public interface ListViewComponent {
    void inject(ListFragment view);
}
