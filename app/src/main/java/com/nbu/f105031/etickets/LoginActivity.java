package com.nbu.f105031.etickets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nbu.f105031.etickets.services.AuthService;
import com.nbu.f105031.etickets.services.UserService;
import com.nbu.f105031.etickets.services.interfaces.IAuthService;
import com.nbu.f105031.etickets.services.interfaces.IUserService;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsernameOrEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonBack;
    private IAuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("eTickets");

        editTextUsernameOrEmail = findViewById(R.id.usernameOrEmail);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btnLogin);
        buttonBack = findViewById(R.id.btnBack);

        IUserService userService = new UserService(this);
        authService = new AuthService(this, userService);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameOrEmail = editTextUsernameOrEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                authService.login(usernameOrEmail, password);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}