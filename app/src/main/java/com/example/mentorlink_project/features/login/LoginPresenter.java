package com.example.mentorlink_project.features.login;

import android.text.TextUtils;

import com.example.mentorlink_project.data.entities.AccountEntity;
import com.example.mentorlink_project.data.repositories.AccountRepository;

public class LoginPresenter implements LoginContract.Presenter {
    private final LoginContract.View view;
    private final AccountRepository accountRepository;

    public LoginPresenter(LoginContract.View view, AccountRepository accountRepository) {
        this.view = view;
        this.accountRepository = accountRepository;
    }

    @Override
    public void handleLogin(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            view.showLoginError("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        AccountEntity account = accountRepository.getAccountByUserCode(username);
        if (account == null) {
            view.showLoginError("Tài khoản không tồn tại");
        } else if (!account.getPassword().equals(password)) {
            view.showLoginError("Sai mật khẩu");
        } else {
            view.showLoginSuccess(account.getRole(), account.getUserCode());
        }
    }
}
