package cz.trakrco.trakr_app.presenter.auth;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import cz.trakrco.trakr_app.view.auth.LoginFragment;

/**
 * Created by vlad on 01/07/2017.
 */

public class LoginPresenter {

    private LoginFragment view;
    private FirebaseAuth auth;

    @Inject
    public LoginPresenter(LoginFragment view, FirebaseAuth auth) {
        this.view = view;
        this.auth = auth;
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }


    public void signIn(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            view.onError("Supply email and password");
            return;
        }
        view.showLoading(true);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(view.getParentActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        view.showLoading(false);
                        if (task.isSuccessful()) {
                            view.onLoginSuccess();
                            view.navigateMain();
                        } else {
                            view.onError("Check your credentials");
                        }
                    }
                });
    }

}
