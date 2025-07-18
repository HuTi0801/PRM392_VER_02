package com.example.mentorlink_project.features.login;

public class LoginContract {
    interface View {
        void showLoginSuccess(String role, String userCode);
        void showLoginError(String message);
    }

    interface Presenter {
        void handleLogin(String username, String password);
    }
}
