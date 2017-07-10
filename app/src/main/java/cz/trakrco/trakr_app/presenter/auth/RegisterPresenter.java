package cz.trakrco.trakr_app.presenter.auth;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import cz.trakrco.trakr_app.domain.User;
import cz.trakrco.trakr_app.model.LocalDBService;
import cz.trakrco.trakr_app.model.RemoteDBService;
import cz.trakrco.trakr_app.view.auth.RegisterFragment;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by vlad on 04/07/2017.
 */

public class RegisterPresenter {

    private RegisterFragment view;
    private FirebaseAuth auth;
    private RemoteDBService rdbService;
    private LocalDBService ldbService;


    @Inject
    public RegisterPresenter(RegisterFragment view, FirebaseAuth auth, RemoteDBService rdbService, LocalDBService ldbService) {
        this.view = view;
        this.auth = auth;
        this.rdbService = rdbService;
        this.ldbService = ldbService;
    }


    public void register(final String email, String password1, String password2) {
        if (!email.isEmpty() && checkEmail(email) && !password1.isEmpty() && password1.equals(password2) && password1.length() > 6) {
            view.showLoading(true);

            auth.createUserWithEmailAndPassword(email, password1)
                    .addOnCompleteListener(view.getParentActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String uid = auth.getCurrentUser().getUid();
                                storeNewUser(new User(uid, email));
                            } else {
                                view.onError("Registration failed");
                            }
                            view.showLoading(false);

                        }
                    });
        } else {
            view.onError("Check your credentials");
        }
    }

    private void storeNewUser(User user) {
        Observable<User> s1 = rdbService.save(user);
        Observable<User> s2 = ldbService.save(user);
        Observable.zip(s1, s2, new BiFunction<User, User, Object>() {
            @Override
            public Object apply(@io.reactivex.annotations.NonNull User user, @io.reactivex.annotations.NonNull User user2) throws Exception {
                Log.i("RegisterPresenter", "Stored new user: " + user.getUid() + " " + user2.getUid());
                return user;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Object o) {
                        view.navigateMain();
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e("RegisterZippedObserver", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.onRegisterSuccess();
                    }
                });

    }

    private boolean checkEmail(String email) {
        return email.contains("@") && (email.length() - email.lastIndexOf("@")) > 3;
    }
}
