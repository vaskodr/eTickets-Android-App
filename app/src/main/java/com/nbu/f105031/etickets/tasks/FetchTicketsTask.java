package com.nbu.f105031.etickets.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.nbu.f105031.etickets.models.Ticket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchTicketsTask extends AsyncTask<Void, Void, List<Ticket>> {

    public interface OnTicketsFetchedListener {
        void onTicketsFetched(List<Ticket> tickets);
    }

    private OnTicketsFetchedListener listener;

    public FetchTicketsTask(OnTicketsFetchedListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<Ticket> doInBackground(Void... voids) {
        List<Ticket> tickets = new ArrayList<>();
        String urlString = "https://app.ticketmaster.com/discovery/v2/events?apikey=yZcAu2U19tpbLU3qsBnTUzVWX6p1A8gB&locale=*";

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder jsonResult = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonResult.append(line);
            }
            reader.close();

            JSONObject jsonObject = new JSONObject(jsonResult.toString());
            JSONObject embedded = jsonObject.getJSONObject("_embedded");
            JSONArray events = embedded.getJSONArray("events");

            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                String name = event.getString("name");
                String date = event.getJSONObject("dates").getJSONObject("start").getString("localDate");
                double price = 0.0;
                if (event.has("priceRanges")) {
                    price = event.getJSONArray("priceRanges").getJSONObject(0).getDouble("min");
                }
                String description = event.optString("info", "No description available");

                Ticket ticket = new Ticket(i, name, description, date, price);
                tickets.add(ticket);
                Log.d("FetchTicketsTask", "Added ticket: " + ticket.getName());
            }
        } catch (Exception e) {
            Log.e("FetchTicketsTask", "Error fetching events", e);
        }

        return tickets;
    }

    @Override
    protected void onPostExecute(List<Ticket> tickets) {
        if (listener != null) {
            listener.onTicketsFetched(tickets);
        }
    }
}