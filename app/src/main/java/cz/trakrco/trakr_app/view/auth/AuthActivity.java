package cz.trakrco.trakr_app.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import cz.trakrco.trakr_app.R;
import cz.trakrco.trakr_app.TrakrApp;
import cz.trakrco.trakr_app.di.components.AuthActivityComponent;
import cz.trakrco.trakr_app.di.components.DaggerAuthActivityComponent;
import cz.trakrco.trakr_app.di.modules.AuthActivityModule;
import cz.trakrco.trakr_app.view.main.MainActivity;

/**
 * Created by vlad on 01/07/2017.
 */

public class AuthActivity extends AppCompatActivity implements AuthContract.BaseView {


    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_single_replace_view);
        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();

        AuthActivityComponent component = DaggerAuthActivityComponent
                .builder()
                .appComponent(TrakrApp.getAppComponent(getApplicationContext()))
                .authActivityModule(new AuthActivityModule(loginFragment, registerFragment))
                .build();

        component.inject(loginFragment);
        component.inject(registerFragment);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        replaceView(RegisterFragment.class);
    }

    @Override
    public void replaceView(Class fragmentClass) {
        Fragment successor = fragmentClass.equals(LoginFragment.class) ? registerFragment : loginFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.single_view_holder, successor).commit();
    }

    @Override
    public void onOperationSuccess() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginFragment = null;
        registerFragment = null;
    }
}
