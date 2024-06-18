package com.nbu.f105031.etickets.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nbu.f105031.etickets.R;
import com.nbu.f105031.etickets.services.UserService;
import com.nbu.f105031.etickets.services.interfaces.IUserService;

public class AddBalanceFragment extends Fragment {
    private static final String TAG = "UserService";
    private EditText editTextAmount;
    private Button buttonAddBalance;
    private IUserService userService;
    private OnBalanceAddedListener listener;
    private SharedPreferences preferences;

    public interface OnBalanceAddedListener {
        void onBalanceAdded();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBalanceAddedListener) {
            listener = (OnBalanceAddedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnBalanceAddedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_balance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextAmount = view.findViewById(R.id.editTextBalance);
        buttonAddBalance = view.findViewById(R.id.buttonAddBalance);

        preferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userService = new UserService(getActivity());

        buttonAddBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = editTextAmount.getText().toString().trim();
                if (TextUtils.isEmpty(amountStr)) {
                    Toast.makeText(getActivity(), "Amount is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(amountStr);
                String usernameOrEmail = preferences.getString("username_or_email", "User");
                Log.d(TAG, "Adding balance: " + amount + " to user: " + usernameOrEmail);
                userService.addBalance(usernameOrEmail, amount);

                if (listener != null) {
                    listener.onBalanceAdded();
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}