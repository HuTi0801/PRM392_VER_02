package com.example.mentorlink_project.features.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mentorlink_project.R;
import com.example.mentorlink_project.data.repositories.AccountRepository;
import com.example.mentorlink_project.features.dashboard.LectureDashboardActivity;
import com.example.mentorlink_project.features.dashboard.StudentDashboardActivity;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {
    private EditText edtUsername, edtPassword;
    private LoginContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        presenter = new LoginPresenter(this, new AccountRepository(getApplicationContext()));

        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            presenter.handleLogin(username, password);
        });

        View btnLogout = findViewById(R.id.btn_logout);
        if (btnLogout != null) {
            btnLogout.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoginSuccess(String role, String userCode) {
        Toast.makeText(this, "Đăng nhập thành công với vai trò: " + role, Toast.LENGTH_SHORT).show();
        Intent intent;
        if (role.equals("STUDENT")) {
            intent = new Intent(this, StudentDashboardActivity.class);
        } else if (role.equals("LECTURER")) {
            intent = new Intent(this, LectureDashboardActivity.class);
        } else {
            return;
        }
        intent.putExtra("USER_CODE", userCode); // truyền userCode qua Intent
        intent.putExtra("ROLE", role);
        startActivity(intent);
        finish(); // Kết thúc LoginActivity
    }

    @Override
    public void showLoginError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}