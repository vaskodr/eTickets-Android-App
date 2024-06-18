package com.nbu.f105031.etickets.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nbu.f105031.etickets.data.DatabaseHelper;
import com.nbu.f105031.etickets.services.interfaces.IUserService;

public class UserService implements IUserService {
    private static final String TAG = "UserService";


    private DatabaseHelper dbHelper;

    public UserService(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public double getUserBalance(String usernameOrEmail) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double balance = 0;
        Cursor cursor = db.rawQuery("SELECT balance FROM users WHERE username = ? OR email = ?", new String[]{usernameOrEmail, usernameOrEmail});
        if (cursor.moveToFirst()) {
            balance = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getUserBalance: " + balance);
        return balance;
    }

    @Override
    public void updateUserBalance(String usernameOrEmail, double newBalance) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", newBalance);
        int rowsUpdated = db.update("users", values, "username = ? OR email = ?", new String[]{usernameOrEmail, usernameOrEmail});
        db.close();
        Log.d(TAG, "updateUserBalance: rowsUpdated = " + rowsUpdated + ", newBalance = " + newBalance);
    }

    @Override
    public void addBalance(String usernameOrEmail, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT balance FROM users WHERE username = ? OR email = ?", new String[]{usernameOrEmail, usernameOrEmail});
        if (cursor.moveToFirst()) {
            double currentBalance = cursor.getDouble(0);
            double newBalance = currentBalance + amount;
            ContentValues values = new ContentValues();
            values.put("balance", newBalance);
            int rowsUpdated = db.update("users", values, "username = ? OR email = ?", new String[]{usernameOrEmail, usernameOrEmail});
            Log.d(TAG, "addBalance: rowsUpdated = " + rowsUpdated + ", currentBalance = " + currentBalance + ", newBalance = " + newBalance);
        }
        cursor.close();
        db.close();
    }


    @Override
    public boolean addUser(String username, String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        values.put("balance", 0); // Initialize balance to 0
        long result = db.insert("users", null, values);
        db.close();
        return result != -1;
    }

    @Override
    public boolean checkUser(String usernameOrEmail, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"id"};
        String selection = "(username = ? OR email = ?) AND password = ?";
        String[] selectionArgs = {usernameOrEmail, usernameOrEmail, password};
        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    @Override
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"id"};
        String selection = "username = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    @Override
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"id"};
        String selection = "email = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    @Override
    public int getUserId(String usernameOrEmail) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int userId = -1;
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username = ? OR email = ?", new String[]{usernameOrEmail, usernameOrEmail});
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return userId;
    }

    @Override
    public boolean buyTicket(int userId, int ticketId, double ticketPrice) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            Cursor userCursor = db.rawQuery("SELECT balance, email FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
            if (userCursor.moveToFirst()) {
                double balance = userCursor.getDouble(0);
                String email = userCursor.getString(1);
                if (balance >= ticketPrice) {

                    // Deduct the ticket price from the user balance
                    ContentValues userValues = new ContentValues();
                    userValues.put("balance", balance - ticketPrice);
                    db.update("users", userValues, "id = ?", new String[]{String.valueOf(userId)});

                    // Insert order
                    ContentValues orderValues = new ContentValues();
                    orderValues.put("user_id", userId);
                    orderValues.put("ticket_id", ticketId);
                    orderValues.put("purchase_date", System.currentTimeMillis());
                    db.insert("orders", null, orderValues);

                    db.setTransactionSuccessful();
                    Log.d(TAG, "Transaction successful for user ID: " + userId + ", Ticket ID: " + ticketId);


                    return true;
                } else {
                    Log.d(TAG, "Insufficient balance for user ID: " + userId);
                }
            } else {
                Log.d(TAG, "User not found with ID: " + userId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in transaction", e);
        } finally {
            db.endTransaction();
        }
        return false;
    }
}
