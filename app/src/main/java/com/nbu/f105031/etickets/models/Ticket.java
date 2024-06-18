package com.nbu.f105031.etickets.models;

public class Ticket {
    private int id;
    private String name;
    private String description;
    private String date;
    private double price;

    public Ticket() {

    }

    public Ticket(int id, String name, String description, String date, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.price = price;
    }

    // Getters and Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}