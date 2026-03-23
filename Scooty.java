package com.scooty.model;

import java.sql.Date;

/**
 * Scooty.java
 * Model / Entity class — holds all data fields for a scooty.
 * No business logic, no database calls — just data + getters/setters.
 * Member 2 – Admin & Scooty Management Module
 */
public class Scooty {

    // ─── Fields ───────────────────────────────────────────────────────────────

    private int    scootyId;
    private String model;
    private String brand;
    private String regNumber;
    private String status;       // "available" | "booked" | "maintenance"
    private double pricePerHr;
    private Date   addedDate;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public Scooty() {}

    public Scooty(String model, String brand, String regNumber,
                  String status, double pricePerHr) {
        this.model      = model;
        this.brand      = brand;
        this.regNumber  = regNumber;
        this.status     = status;
        this.pricePerHr = pricePerHr;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public int    getScootyId()              { return scootyId; }
    public void   setScootyId(int scootyId)  { this.scootyId = scootyId; }

    public String getModel()                 { return model; }
    public void   setModel(String model)     { this.model = model; }

    public String getBrand()                 { return brand; }
    public void   setBrand(String brand)     { this.brand = brand; }

    public String getRegNumber()             { return regNumber; }
    public void   setRegNumber(String r)     { this.regNumber = r; }

    public String getStatus()               { return status; }
    public void   setStatus(String status)  { this.status = status; }

    public double getPricePerHr()                  { return pricePerHr; }
    public void   setPricePerHr(double pricePerHr) { this.pricePerHr = pricePerHr; }

    public Date getAddedDate()              { return addedDate; }
    public void setAddedDate(Date addedDate){ this.addedDate = addedDate; }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
            "Scooty{id=%d, model='%s', brand='%s', reg='%s', status='%s', price=%.2f/hr, added=%s}",
            scootyId, model, brand, regNumber, status, pricePerHr, addedDate
        );
    }
}
