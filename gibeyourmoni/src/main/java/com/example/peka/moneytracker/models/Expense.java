package com.example.peka.moneytracker.models;

import org.joda.time.DateTime;

/**
 * Created by peka on 21.03.17.
 */

public class Expense {
    private int id;
    private DateTime date;
    private String name;
    private double price;

    public Expense() {
    }

    public Expense(String name, double price, DateTime date) {
        this.name = name;
        this.date = date;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return name + " - " + price + " - " + date.toString("dd-MM-YYYY");
    }
}
