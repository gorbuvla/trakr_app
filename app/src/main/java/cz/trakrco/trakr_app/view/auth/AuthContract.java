package cz.trakrco.trakr_app.view.auth;

import android.app.Activity;

/**
 * Created by vlad on 01/07/2017.
 */

public interface AuthContract {

    interface BaseView {
        void replaceView(Class t);
        void onOperationSuccess();
    }

    interface RequiredCommon {
        void showLoading(boolean show);
        void onError(String msg);
        void navigateMain();
        Activity getParentActivity();
    }

    interface LoginView extends RequiredCommon {
        void onLoginSuccess();
    }

    interface RegisterView extends RequiredCommon{
        void onRegisterSuccess();
    }

}
