package cz.trakrco.trakr_app;

import android.app.Application;
import android.content.Context;

import cz.trakrco.trakr_app.di.components.AppComponent;
import cz.trakrco.trakr_app.di.components.DaggerAppComponent;
import cz.trakrco.trakr_app.di.modules.AppModule;

/**
 * Created by vlad on 04/07/2017.
 */

public class TrakrApp extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public static AppComponent getAppComponent(Context context) {
        return ((TrakrApp)context.getApplicationContext()).getAppComponent();
    }
}
