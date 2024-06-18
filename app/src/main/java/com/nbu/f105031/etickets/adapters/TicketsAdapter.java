package com.nbu.f105031.etickets.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nbu.f105031.etickets.R;
import com.nbu.f105031.etickets.models.Ticket;
import com.nbu.f105031.etickets.services.UserService;
import com.nbu.f105031.etickets.services.interfaces.IUserService;

import java.util.List;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.TicketViewHolder> {

    public interface OnTicketPurchasedListener {
        void onTicketPurchased();
    }

    private List<Ticket> tickets;
    private IUserService userService;
    private Context context;
    private OnTicketPurchasedListener listener;

    public TicketsAdapter(Context context, List<Ticket> tickets, OnTicketPurchasedListener listener) {
        this.context = context;
        this.tickets = tickets;
        this.listener = listener;
        userService = new UserService(context);
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);
        holder.bind(ticket);
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    class TicketViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName;
        private TextView textViewDescription;
        private TextView textViewDate;
        private TextView textViewPrice;
        private Button buttonBuyTicket;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            buttonBuyTicket = itemView.findViewById(R.id.buttonBuyTicket);
        }

        public void bind(final Ticket ticket) {
            textViewName.setText(ticket.getName());
            textViewDescription.setText(ticket.getDescription());
            textViewDate.setText(ticket.getDate());
            textViewPrice.setText(String.valueOf(ticket.getPrice()));
            buttonBuyTicket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences preferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
                    String usernameOrEmail = preferences.getString("username_or_email", "User");
                    int userId = userService.getUserId(usernameOrEmail);
                    if (userService.buyTicket(userId, ticket.getId(), ticket.getPrice())) {
                        Toast.makeText(context, "Ticket purchased successfully", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onTicketPurchased();
                        }
                    } else {
                        Toast.makeText(context, "Insufficient balance", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}