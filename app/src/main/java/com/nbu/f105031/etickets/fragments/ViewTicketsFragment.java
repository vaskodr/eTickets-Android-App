package com.nbu.f105031.etickets.fragments;

import android.content.Context;
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
import com.nbu.f105031.etickets.adapters.TicketsAdapter;
import com.nbu.f105031.etickets.services.interfaces.ITicketService;
import com.nbu.f105031.etickets.tasks.FetchTicketsTask;
import com.nbu.f105031.etickets.models.Ticket;
import com.nbu.f105031.etickets.services.TicketService;

import java.util.List;

public class ViewTicketsFragment extends Fragment {

    private RecyclerView recyclerViewTickets;
    private TicketsAdapter.OnTicketPurchasedListener listener;
    private ITicketService ticketService;
    private static final String TAG = "ViewTicketsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_tickets, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TicketsAdapter.OnTicketPurchasedListener) {
            listener = (TicketsAdapter.OnTicketPurchasedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnTicketPurchasedListener");
        }
        ticketService = new TicketService(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewTickets = view.findViewById(R.id.recyclerViewTickets);
        recyclerViewTickets.setLayoutManager(new LinearLayoutManager(getActivity()));
        fetchTickets();
    }

    private void fetchTickets() {
        new FetchTicketsTask(new FetchTicketsTask.OnTicketsFetchedListener() {
            @Override
            public void onTicketsFetched(List<Ticket> tickets) {
                Log.d(TAG, "Fetched tickets count: " + tickets.size());
                for (Ticket ticket : tickets) {
                    ticketService.addTicket(ticket);
                }
                displayTickets();
            }
        }).execute();
    }

    private void displayTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        Log.d(TAG, "Displaying tickets count: " + tickets.size());
        TicketsAdapter adapter = new TicketsAdapter(getActivity(), tickets, listener);
        recyclerViewTickets.setAdapter(adapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}