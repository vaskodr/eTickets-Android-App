package com.nbu.f105031.etickets.services.interfaces;

import com.nbu.f105031.etickets.models.Order;
import com.nbu.f105031.etickets.models.Ticket;

import java.util.List;

public interface ITicketService {

    boolean ticketExists(int ticketId);
    void addTicket(Ticket ticket);
    List<Ticket> getAllTickets();
}
