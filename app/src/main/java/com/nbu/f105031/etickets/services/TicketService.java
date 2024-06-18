package com.nbu.f105031.etickets.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nbu.f105031.etickets.data.DatabaseHelper;
import com.nbu.f105031.etickets.models.Ticket;
import com.nbu.f105031.etickets.services.interfaces.ITicketService;

import java.util.ArrayList;
import java.util.List;

public class TicketService implements ITicketService{
    private DatabaseHelper dbHelper;
    private static final String TAG = "TicketService";

    public TicketService(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean ticketExists(int ticketId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM tickets WHERE id = ?", new String[]{String.valueOf(ticketId)});
        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Ticket exists: " + exists);
        return exists;
    }

    public void addTicket(Ticket ticket) {
        if (!ticketExists(ticket.getId())) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", ticket.getId());
            values.put("name", ticket.getName());
            values.put("description", ticket.getDescription());
            values.put("date", ticket.getDate());
            values.put("price", ticket.getPrice());
            long result = db.insert("tickets", null, values);
            db.close();
            Log.d(TAG, "Ticket added with result: " + result);
        } else {
            Log.d(TAG, "Ticket already exists with ID: " + ticket.getId());
        }
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("tickets", null, null, null, null, null, null);
        if (cursor != null) {
            try {
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int descriptionIndex = cursor.getColumnIndex("description");
                int dateIndex = cursor.getColumnIndex("date");
                int priceIndex = cursor.getColumnIndex("price");

                while (cursor.moveToNext()) {
                    Ticket ticket = new Ticket();
                    ticket.setId(cursor.getInt(idIndex));
                    ticket.setName(cursor.getString(nameIndex));
                    ticket.setDescription(cursor.getString(descriptionIndex));
                    ticket.setDate(cursor.getString(dateIndex));
                    ticket.setPrice(cursor.getDouble(priceIndex));
                    tickets.add(ticket);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error reading from the database", e);
            } finally {
                cursor.close();
            }
        }
        db.close();
        return tickets;
    }
}