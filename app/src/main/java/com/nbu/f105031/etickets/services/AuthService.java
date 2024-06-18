package com.nbu.f105031.etickets.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.nbu.f105031.etickets.HomePageActivity;
import com.nbu.f105031.etickets.LoginActivity;
import com.nbu.f105031.etickets.MainActivity;
import com.nbu.f105031.etickets.RegisterActivity;
import com.nbu.f105031.etickets.services.interfaces.IAuthService;
import com.nbu.f105031.etickets.services.interfaces.IUserService;

public class AuthService implements IAuthService {
    private Context context;
    private IUserService userService;

    public AuthService(Context context, IUserService userService) {
        this.context = context;
        this.userService = userService;
    }

    @Override
    public boolean login(String usernameOrEmail, String password) {
        if (userService.checkUser(usernameOrEmail, password)) {
            SharedPreferences preferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("is_logged_in", true);
            editor.putString("username_or_email", usernameOrEmail);
            editor.apply();

            Intent intent = new Intent(context, HomePageActivity.class);
            context.startActivity(intent);

            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void register(String username, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userService.checkUsernameExists(username)) {
            Toast.makeText(context, "Username already exists, please choose another", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userService.checkEmailExists(email)) {
            Toast.makeText(context, "Email already exists, please choose another", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isAdded = userService.addUser(username, email, password);
        if (isAdded) {
            Toast.makeText(context, "Registration successful, please login", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            if (context instanceof RegisterActivity) {
                ((RegisterActivity) context).finish();
            }
        } else {
            Toast.makeText(context, "Registration failed, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void logout(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}