<<<<<<< HEAD
package com.scooty.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection.java
 * Manages JDBC connection to the MySQL database.
 * All DAO classes call DBConnection.getConnection() to get a live connection.
 * Member 2 – Admin & Scooty Management Module
 *
 * ── Setup ──────────────────────────────────────────────────────────────────
 *  1. Add MySQL JDBC driver JAR to your project:
 *       mysql-connector-j-8.x.x.jar
 *  2. Create the database in MySQL:
 *       CREATE DATABASE scooty_rental_db;
 *  3. Update DB_URL, DB_USER, DB_PASSWORD below to match your setup.
 * ───────────────────────────────────────────────────────────────────────────
 */
public class DBConnection {

    // ─── Config — update these to match your MySQL setup ─────────────────────

    private static final String DB_URL      = "jdbc:mysql://localhost:3306/scooty_rental_db" +
                                              "?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "your_password_here";   // ← change this

    // ─── Driver load (runs once when class is first used) ─────────────────────

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[DB] MySQL driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("[DB ERROR] MySQL driver not found: " + e.getMessage());
            System.out.println("           Add mysql-connector-j JAR to your project.");
        }
    }

    // ─── getConnection ────────────────────────────────────────────────────────

    /**
     * Opens and returns a new JDBC Connection.
     * Always called inside a try-with-resources block so it auto-closes.
     *
     * Usage:
     *   try (Connection conn = DBConnection.getConnection();
     *        PreparedStatement stmt = conn.prepareStatement(sql)) { ... }
     *
     * @return Live Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // ─── Test connection ──────────────────────────────────────────────────────

    /**
     * Quick sanity check — run this first to verify DB setup.
     */
    public static void main(String[] args) {
        System.out.println("=== Testing DB Connection ===");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("[SUCCESS] Connected to: " + conn.getMetaData().getURL());
            }
        } catch (SQLException e) {
            System.out.println("[FAILED] Could not connect: " + e.getMessage());
            System.out.println("  Check: DB running? Username/password correct? DB name correct?");
        }
=======
import java.sql.*;

/**
 * DBConnection – Written by Member 1 (Backend Lead).
 * Stub included here so Member 4's code compiles independently.
 * Replace with Member 1's actual implementation.
 */
public class DBConnection {

    private static final String URL  = "jdbc:mysql://localhost:3306/ScootyRentalDB";
    private static final String USER = "root";
    private static final String PASS = "V1vek5500";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
>>>>>>> 017987dcda29d69f92144393b28fd174b4259b67
    }
}
