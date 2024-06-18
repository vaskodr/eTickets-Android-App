package com.nbu.f105031.etickets.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nbu.f105031.etickets.models.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "fetchDb.db";
    private static final int DATABASE_VERSION = 11;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "email TEXT, " +
                "password TEXT, " +
                "balance REAL DEFAULT 0)");

        db.execSQL("CREATE TABLE tickets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "description TEXT, " +
                "date TEXT, " +
                "price REAL)");

        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "ticket_id INTEGER, " +
                "purchase_date TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES users(id), " +
                "FOREIGN KEY(ticket_id) REFERENCES tickets(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS tickets");
        db.execSQL("DROP TABLE IF EXISTS orders");
        onCreate(db);
    }

    public void seedTickets() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getReadableDatabase();
                addTicket(db, "Concert", "Live Music Concert", "2023-07-01", 50.00);
                addTicket(db, "Theater", "Theater play", "2023-07-15", 30.00);
                addTicket(db, "Sports Event", "Football match", "2023-07-20", 70.00);
                addTicket(db, "Cinema", "Maze Runner", "2023-07-01", 50.00);
                addTicket(db, "Theater", "The Forest", "2023-07-15", 30.00);
                addTicket(db, "Sports Event", "Basketball match", "2023-07-20", 70.00);
            }
        });

    }

    private void addTicket(SQLiteDatabase db, String eventName, String description, String date, double price) {
        ContentValues values = new ContentValues();
        values.put("name", eventName);
        values.put("description", description);
        values.put("date", date);
        values.put("price", price);
        db.insert("tickets", null, values);
    }
}