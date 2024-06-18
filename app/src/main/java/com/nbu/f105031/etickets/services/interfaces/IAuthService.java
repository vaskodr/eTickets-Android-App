package com.nbu.f105031.etickets.services.interfaces;

import android.content.SharedPreferences;

public interface IAuthService {
    boolean login(String usernameOrEmail, String password);
    void register(String username, String email, String password, String confirmPassword);
    void logout(SharedPreferences preferences);
}
