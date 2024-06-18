package com.nbu.f105031.etickets.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nbu.f105031.etickets.R;
import com.nbu.f105031.etickets.adapters.OrdersAdapter;
import com.nbu.f105031.etickets.models.Order;
import com.nbu.f105031.etickets.services.OrderService;
import com.nbu.f105031.etickets.services.UserService;
import com.nbu.f105031.etickets.services.interfaces.IOrderService;
import com.nbu.f105031.etickets.services.interfaces.IUserService;

import java.util.List;

public class ViewOrdersFragment extends Fragment {

    private static final String TAG = "ViewOrdersFragment";
    private IOrderService orderService;
    private RecyclerView recyclerViewOrders;
    private SharedPreferences preferences;
    private IUserService userService;
    private static final String SCROLL_POSITION_KEY = "scroll_position_key";
    private int scrollPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            scrollPosition = savedInstanceState.getInt(SCROLL_POSITION_KEY, 0);
        }

        preferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        orderService = new OrderService(getActivity());
        userService = new UserService(getActivity());

        String usernameOrEmail = preferences.getString("username_or_email", null);
        if (usernameOrEmail != null) {
            int userId = userService.getUserId(usernameOrEmail);
            Log.d(TAG, "Retrieved user ID: " + userId);
            if (userId != -1) {
                List<Order> orders = orderService.getOrdersForUser(userId);
                OrdersAdapter adapter = new OrdersAdapter(orders);
                recyclerViewOrders.setAdapter(adapter);
                recyclerViewOrders.scrollToPosition(scrollPosition);
            } else {
                Log.e(TAG, "Invalid user ID retrieved");
            }
        } else {
            Log.e(TAG, "Username or email not found in shared preferences");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerViewOrders.getLayoutManager();
        if (layoutManager != null) {
            scrollPosition = layoutManager.findFirstVisibleItemPosition();
            outState.putInt(SCROLL_POSITION_KEY, scrollPosition);
        }
    }
}