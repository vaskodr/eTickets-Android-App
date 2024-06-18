package com.nbu.f105031.etickets.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nbu.f105031.etickets.data.DatabaseHelper;
import com.nbu.f105031.etickets.models.Order;
import com.nbu.f105031.etickets.services.interfaces.IOrderService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderService implements IOrderService {

    private DatabaseHelper dbHelper;
    private static final String TAG = "OrderService";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


    public OrderService(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public List<Order> getOrdersForUser(int userId) {
        Log.d(TAG, "Fetching orders for user ID: " + userId);
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT orders.ticket_id, tickets.name, tickets.date, COUNT(orders.id) AS quantity, MAX(orders.purchase_date) AS latest_purchase_date " +
                            "FROM orders " +
                            "INNER JOIN tickets ON orders.ticket_id = tickets.id " +
                            "WHERE orders.user_id = ? " +
                            "GROUP BY orders.ticket_id, tickets.name, tickets.date",
                    new String[]{String.valueOf(userId)}
            );

            if (cursor != null && cursor.getCount() > 0) {
                int ticketIdIndex = cursor.getColumnIndex("ticket_id");
                int ticketNameIndex = cursor.getColumnIndex("name");
                int ticketDateIndex = cursor.getColumnIndex("date");
                int quantityIndex = cursor.getColumnIndex("quantity");
                int purchaseDateIndex = cursor.getColumnIndex("latest_purchase_date");

                while (cursor.moveToNext()) {
                    Order order = new Order();
                    order.setTicketId(cursor.getInt(ticketIdIndex));
                    order.setTicketName(cursor.getString(ticketNameIndex));

                    String ticketDateString = cursor.getString(ticketDateIndex);
                    try {
                        Date ticketDate = dateFormat.parse(ticketDateString);
                        order.setTicketDate(ticketDate);
                    } catch (ParseException e) {
                        Log.e(TAG, "Error parsing ticket date: " + ticketDateString, e);
                        order.setTicketDate(null);
                    }

                    order.setQuantity(cursor.getInt(quantityIndex));
                    order.setPurchaseDate(new Date(cursor.getLong(purchaseDateIndex)));
                    orders.add(order);
                }
            } else {
                Log.d(TAG, "No orders found for user ID: " + userId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching orders for user ID: " + userId, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return orders;
    }
}
