package cz.trakrco.trakr_app.view.auth;


import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import cz.trakrco.trakr_app.presenter.auth.RegisterPresenter;

/**
 * Created by vlad on 01/07/2017.
 */

public class RegisterFragment extends Fragment implements AuthContract.RegisterView {

    @BindView(R.id.email_register)
    EditText emailEditText;

    @BindView(R.id.password_register1)
    EditText password1EditText;

    @BindView(R.id.password_register2)
    EditText password2EditText;

    @BindView(R.id.register_err_msg)
    TextView errorTextView;

    @BindView(R.id.register_btn)
    Button registerButton;

    @BindView(R.id.nav_login_btn)
    Button navLogin;

    @BindView(R.id.register_progress)
    ProgressBar progressBar;

    Unbinder unbinder;

    @Inject
    RegisterPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navLogin.setPaintFlags(navLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @OnClick(R.id.nav_login_btn)
    public void navigateLogin() {
        AuthContract.BaseView view = (AuthActivity) getActivity();
        view.replaceView(this.getClass());
    }

    @OnClick(R.id.register_btn)
    public void onRegisterClicked() {
        presenter.register(emailEditText.getText().toString(), password1EditText.getText().toString(), password2EditText.getText().toString());
    }

    @Override
    public void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        registerButton.setEnabled(!show);
        emailEditText.setEnabled(!show);
        password1EditText.setEnabled(!show);
        password2EditText.setEnabled(!show);
        navLogin.setEnabled(!show);
    }

    @Override
    public void onError(String msg) {
        errorTextView.setText(msg);
        errorTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRegisterSuccess() {
        emailEditText.setText("");
        password1EditText.setText("");
        password2EditText.setText("");
        errorTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.successGreen));
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(R.string.register_success);
    }

    @Override
    public void navigateMain() {
        AuthContract.BaseView view = (AuthContract.BaseView)getActivity();
        view.onOperationSuccess();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public Activity getParentActivity() {
        return getActivity();
    }

}
