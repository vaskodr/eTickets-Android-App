package com.nbu.f105031.etickets.services.interfaces;

import com.nbu.f105031.etickets.models.Order;

import java.util.List;

public interface IOrderService {
    List<Order> getOrdersForUser(int userId);
}
