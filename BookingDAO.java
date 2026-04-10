package com.scooty.dao;

// DBConnection is in the default package — no import needed
import com.scooty.model.Booking;
import com.scooty.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BookingDAO.java
 * Data Access Object — all database operations for customers and bookings.
 * No business logic here; only raw SQL CRUD.
 * Member 3 – Booking & Rental Module
 *
 * ── Tables used ──────────────────────────────────────────────────────────────
 *   customers  — stores customer records
 *   bookings   — stores rental booking records
 *   scooties   — updated (status) on assign / close
 * ─────────────────────────────────────────────────────────────────────────────
 */
public class BookingDAO {

    // ══════════════════════════════════════════════════════════════════════════
    //  CUSTOMER OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Insert a new customer into the database.
     * @return generated customer_id, or -1 on failure
     */
    public int addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (name, phone, email, license_number) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getLicenseNumber());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        int id = keys.getInt(1);
                        customer.setCustomerId(id);
                        System.out.println("[DAO] Customer added with ID: " + id);
                        return id;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("[DAO ERROR] addCustomer: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Find a customer by their ID.
     * @return Customer object, or null if not found
     */
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapCustomer(rs);
            }

        } catch (SQLException e) {
            System.out.println("[DAO ERROR] getCustomerById: " + e.getMessage());
        }
        return null;
    }

    /**
     * Find a customer by phone number.
     * @return Customer object, or null if not found
     */
    public Customer getCustomerByPhone(String phone) {
        String sql = "SELECT * FROM customers WHERE phone = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapCustomer(rs);
            }

        } catch (SQLException e) {
            System.out.println("[DAO ERROR] getCustomerByPhone: " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns all customers in the system.
     */
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY registered_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(mapCustomer(rs));

        } catch (SQLException e) {
            System.out.println("[DAO ERROR] getAllCustomers: " + e.getMessage());
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BOOKING OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Insert a new booking into the database.
     * Also marks the scooty status as 'booked'.
     * Uses a transaction so both updates succeed or both roll back.
     * @return generated booking_id, or -1 on failure
     */
    public int createBooking(Booking booking) {
        String insertBooking  = "INSERT INTO bookings (customer_id, scooty_id, start_time, status) VALUES (?, ?, ?, ?)";
        String updateScooty   = "UPDATE scooties SET status = 'booked' WHERE scooty_id = ? AND status = 'available'";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // begin transaction

            // 1. Mark scooty as booked
            try (PreparedStatement upd = conn.prepareStatement(updateScooty)) {
                upd.setInt(1, booking.getScootyId());
                int affected = upd.executeUpdate();
                if (affected == 0) {
                    System.out.println("[DAO] Scooty " + booking.getScootyId() + " is not available.");
                    conn.rollback();
                    return -1;
                }
            }

            // 2. Insert booking record
            try (PreparedStatement ins = conn.prepareStatement(insertBooking, Statement.RETURN_GENERATED_KEYS)) {
                ins.setInt(1, booking.getCustomerId());
                ins.setInt(2, booking.getScootyId());
                ins.setTimestamp(3, booking.getStartTime());
                ins.setString(4, Booking.STATUS_ACTIVE);

                ins.executeUpdate();
                try (ResultSet keys = ins.getGeneratedKeys()) {
                    if (keys.next()) {
                        int id = keys.getInt(1);
                        booking.setBookingId(id);
                        conn.commit();
                        System.out.println("[DAO] Booking created with ID: " + id);
                        return id;
                    }
                }
            }

            conn.rollback();
        } catch (SQLException e) {
            System.out.println("[DAO ERROR] createBooking: " + e.getMessage());
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
        return -1;
    }

    /**
     * Fetch a booking by its ID.
     */
    public Booking getBookingById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapBooking(rs);
            }

        } catch (SQLException e) {
            System.out.println("[DAO ERROR] getBookingById: " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns all active (ongoing) bookings.
     */
    public List<Booking> getActiveBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE status = 'active' ORDER BY start_time DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(mapBooking(rs));

        } catch (SQLException e) {
            System.out.println("[DAO ERROR] getActiveBookings: " + e.getMessage());
        }
        return list;
    }

    /**
     * Close a rental — sets end_time, total_hours, total_amount, status='closed'.
     * Also marks the scooty back to 'available'.
     * Uses a transaction so both updates succeed or both roll back.
     */
    public boolean closeBooking(Booking booking) {
        String updateBooking = "UPDATE bookings SET end_time=?, total_hours=?, total_amount=?, status='closed' WHERE booking_id=?";
        String updateScooty  = "UPDATE scooties SET status='available' WHERE scooty_id=?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement b = conn.prepareStatement(updateBooking)) {
                b.setTimestamp(1, booking.getEndTime());
                b.setDouble(2, booking.getTotalHours());
                b.setDouble(3, booking.getTotalAmount());
                b.setInt(4, booking.getBookingId());
                b.executeUpdate();
            }

            try (PreparedStatement s = conn.prepareStatement(updateScooty)) {
                s.setInt(1, booking.getScootyId());
                s.executeUpdate();
            }

            conn.commit();
            System.out.println("[DAO] Booking " + booking.getBookingId() + " closed. Scooty " + booking.getScootyId() + " is now available.");
            return true;

        } catch (SQLException e) {
            System.out.println("[DAO ERROR] closeBooking: " + e.getMessage());
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
        return false;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    private Customer mapCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setName(rs.getString("name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setLicenseNumber(rs.getString("license_number"));
        c.setRegisteredAt(rs.getTimestamp("registered_at"));
        return c;
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setCustomerId(rs.getInt("customer_id"));
        b.setScootyId(rs.getInt("scooty_id"));
        b.setStartTime(rs.getTimestamp("start_time"));
        b.setEndTime(rs.getTimestamp("end_time"));
        b.setTotalHours(rs.getDouble("total_hours"));
        b.setTotalAmount(rs.getDouble("total_amount"));
        b.setStatus(rs.getString("status"));
        return b;
    }
}
