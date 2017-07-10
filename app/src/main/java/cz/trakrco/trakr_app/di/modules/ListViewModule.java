package cz.trakrco.trakr_app.di.modules;

import android.content.Context;

import cz.trakrco.trakr_app.domain.User;
import cz.trakrco.trakr_app.di.scopes.FragmentScope;
import cz.trakrco.trakr_app.model.StorageService;
import cz.trakrco.trakr_app.presenter.main.OutputListAdapter;
import cz.trakrco.trakr_app.view.main.ListFragment;
import dagger.Module;
import dagger.Provides;

/**
 * Created by vlad on 09/07/2017.
 */
@Module
public class ListViewModule {

    private Context context;
    private ListFragment view;

    public ListViewModule(ListFragment view) {
        this.view = view;
    }

    @Provides
    @FragmentScope
    OutputListAdapter provideOutputListAdapter(StorageService storageService, User user) {
        return new OutputListAdapter(view, storageService, user);
    }

}
