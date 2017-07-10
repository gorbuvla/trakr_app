package cz.trakrco.trakr_app.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.trakrco.trakr_app.R;
import cz.trakrco.trakr_app.TrakrApp;
import cz.trakrco.trakr_app.di.components.DaggerMainActivityComponent;
import cz.trakrco.trakr_app.di.components.MainActivityComponent;
import cz.trakrco.trakr_app.di.modules.MainActivityModule;
import cz.trakrco.trakr_app.model.StorageService;
import cz.trakrco.trakr_app.view.auth.AuthActivity;

/**
 * Created by vlad on 01/07/2017.
 */

public class MainActivity extends AppCompatActivity {

    private MainActivityComponent component;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    FirebaseAuth auth;

    @Inject
    StorageService storageService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Trakr.");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.save_label);

        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        component = DaggerMainActivityComponent.builder()
                .appComponent(TrakrApp.getAppComponent(getApplicationContext()))
                .mainActivityModule(new MainActivityModule(this))
                .build();

        component.inject(this);

        setupDrawer();

        if (storageService != null) {
            Log.i("MainActivity", "Storage Service OK");
        }

        InputFragment fragment = new InputFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();

    }

    private void setupDrawer() {
        View headerView = navigationView.getHeaderView(0);
        TextView email = (TextView) headerView.findViewById(R.id.user_email);
        email.setText(auth.getCurrentUser().getEmail());
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;
                switch (item.getItemId()) {

                    case R.id.drawer_new: {
                        fragment = new InputFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
                        break;
                    }
                    case R.id.drawer_all: {
                        fragment = new ListFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
                        break;
                    }
                    case R.id.logout: {
                        logout();
                        break;
                    }
                    default:
                        return false;
                }

                drawerLayout.closeDrawer(Gravity.START);

                return false;
            }
        });
    }

    private void logout() {
        auth.signOut();
        Intent i = new Intent(getApplicationContext(), AuthActivity.class);
        startActivity(i);
        finish();
    }

    protected MainActivityComponent getComponent() {
        return component;
    }
}
