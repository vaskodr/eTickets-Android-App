package com.nbu.f105031.etickets.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nbu.f105031.etickets.R;
import com.nbu.f105031.etickets.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Order> orders;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public OrdersAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.textViewEventName.setText(order.getTicketName());
        holder.textViewEventDate.setText(dateFormat.format(order.getTicketDate()));
        holder.textViewQuantity.setText("Quantity: " + order.getQuantity());
        holder.textViewPurchaseDate.setText("Purchase Date: " + dateFormat.format(order.getPurchaseDate()));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEventName;
        TextView textViewEventDate;
        TextView textViewQuantity;
        TextView textViewPurchaseDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.textViewEventName);
            textViewEventDate = itemView.findViewById(R.id.textViewEventDate);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewPurchaseDate = itemView.findViewById(R.id.textViewPurchaseDate);
        }
    }
}