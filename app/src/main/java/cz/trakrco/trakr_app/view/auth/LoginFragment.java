package cz.trakrco.trakr_app.view.auth;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.trakrco.trakr_app.R;
import cz.trakrco.trakr_app.presenter.auth.LoginPresenter;

/**
 * Created by vlad on 01/07/2017.
 */

public class LoginFragment extends Fragment implements AuthContract.LoginView {


    @BindView(R.id.email_login)
    EditText editTextEmail;

    @BindView(R.id.password_login)
    EditText editTextPassword;

    @BindView(R.id.login_btn)
    Button loginButton;

    @BindView(R.id.login_err_msg)
    TextView errorTextView;

    @BindView(R.id.nav_register_btn)
    Button navRegisterButton;

    @BindView(R.id.login_progress)
    ProgressBar progressBar;

    @Inject
    LoginPresenter presenter;

    Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (presenter.isUserLoggedIn()) {
            AuthActivity activity = (AuthActivity)getActivity();
            activity.onOperationSuccess();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navRegisterButton.setPaintFlags(navRegisterButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @OnClick(R.id.nav_register_btn)
    public void navigateRegisterView() {
        Log.i("LoginFragment", "Navigation button clicked!");
        AuthContract.BaseView view = (AuthActivity) getActivity();
        view.replaceView(this.getClass());
    }

    @OnClick(R.id.login_btn)
    public void onSignInClicked() {
        presenter.signIn(editTextEmail.getText().toString(), editTextPassword.getText().toString());
    }

    @Override
    public void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!show);
        editTextEmail.setEnabled(!show);
        editTextPassword.setEnabled(!show);
        navRegisterButton.setEnabled(!show);
    }

    @Override
    public void onError(String msg) {
        errorTextView.setText(msg);
    }

    @Override
    public void navigateMain() {
        AuthContract.BaseView view = (AuthContract.BaseView)getActivity();
        view.onOperationSuccess();
    }

    @Override
    public Activity getParentActivity() {
        return getActivity();
    }

    @Override
    public void onLoginSuccess() {
        errorTextView.setText(R.string.operation_ok);
        errorTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.successGreen));
    }
}
