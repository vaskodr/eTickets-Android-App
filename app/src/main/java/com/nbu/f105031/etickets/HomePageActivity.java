package com.nbu.f105031.etickets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.nbu.f105031.etickets.adapters.TicketsAdapter;
import com.nbu.f105031.etickets.fragments.AddBalanceFragment;
import com.nbu.f105031.etickets.fragments.ViewOrdersFragment;
import com.nbu.f105031.etickets.fragments.ViewTicketsFragment;
import com.nbu.f105031.etickets.services.AuthService;
import com.nbu.f105031.etickets.services.SoundService;
import com.nbu.f105031.etickets.services.UserService;
import com.nbu.f105031.etickets.services.interfaces.IAuthService;
import com.nbu.f105031.etickets.services.interfaces.IUserService;

public class HomePageActivity extends AppCompatActivity implements AddBalanceFragment.OnBalanceAddedListener, TicketsAdapter.OnTicketPurchasedListener {

    private static final String TAG = "HomePageActivity";
    private TextView welcomeTextView, balanceTextView;
    private IUserService userService;
    private IAuthService authService;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("eTickets");

        welcomeTextView = findViewById(R.id.welcomeTextView);
        balanceTextView = findViewById(R.id.balanceTextView);

        preferences = getSharedPreferences("user_session", MODE_PRIVATE);

        String usernameOrEmail = preferences.getString("username_or_email", "User");

        userService = new UserService(this);
        authService = new AuthService(this, userService);

        welcomeTextView.setText("Welcome, " + usernameOrEmail + ", to eTickets App!");

        updateBalance(usernameOrEmail);
    }

    @Override
    public void onResume() {
        super.onResume();
        String usernameOrEmail = preferences.getString("username_or_email", "User");
        updateBalance(usernameOrEmail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu called");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu called");
        boolean isLoggedIn = preferences.getBoolean("is_logged_in", false);
        Log.d(TAG, "User logged in: " + isLoggedIn);

        MenuItem logoutItem = menu.findItem(R.id.action_logout);
        MenuItem addBalanceItem = menu.findItem(R.id.action_add_balance);
        MenuItem viewTicketsItem = menu.findItem(R.id.action_view_tickets);
        MenuItem viewOrdersItem = menu.findItem(R.id.action_view_orders);
        logoutItem.setVisible(isLoggedIn);
        addBalanceItem.setVisible(isLoggedIn);
        viewTicketsItem.setVisible(isLoggedIn);
        viewOrdersItem.setVisible(isLoggedIn);

        Log.d(TAG, "Menu items visibility set");

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            authService.logout(preferences);
            return true;
        } else if (id == R.id.action_add_balance) {
            showAddBalanceFragment();
            return true;
        } else if (id == R.id.action_view_tickets) {
            showViewTicketsFragment();
            return true;
        } else if (id == R.id.action_view_orders) {
            showViewOrdersFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAddBalanceFragment() {
        AddBalanceFragment addBalanceFragment = new AddBalanceFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, addBalanceFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showViewTicketsFragment() {
        ViewTicketsFragment viewTicketsFragment = new ViewTicketsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, viewTicketsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showViewOrdersFragment() {
        ViewOrdersFragment viewOrdersFragment = new ViewOrdersFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, viewOrdersFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void updateBalance(String usernameOrEmail) {
        double balance = userService.getUserBalance(usernameOrEmail);
        balanceTextView.setText("Balance: $" + balance);
    }

    @Override
    public void onBalanceAdded() {
        String usernameOrEmail = preferences.getString("username_or_email", "User");
        updateBalance(usernameOrEmail);
        getSupportFragmentManager().popBackStack();
        Toast.makeText(this, "Balance updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTicketPurchased() {
        String usernameOrEmail = preferences.getString("username_or_email", "User");
        updateBalance(usernameOrEmail);

        Intent soundIntent = new Intent(this, SoundService.class);
        startService(soundIntent);
    }
}
