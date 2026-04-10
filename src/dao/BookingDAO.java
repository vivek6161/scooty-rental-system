package dao;

import database.DBConnection;
import java.sql.*;

public class BookingDAO {

    // Create a new booking
    public int createBooking(int customerId, int scootyId) {
        String insertBooking = "INSERT INTO bookings (customer_id, scooty_id, start_time, status) VALUES (?, ?, NOW(), 'active')";
        String updateScooty  = "UPDATE Scooty SET status = 'Rented' WHERE scooty_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Mark scooty as Rented
            try (PreparedStatement ps = conn.prepareStatement(updateScooty)) {
                ps.setInt(1, scootyId);
                ps.executeUpdate();
            }

            // Create booking
            try (PreparedStatement ps = conn.prepareStatement(insertBooking, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, customerId);
                ps.setInt(2, scootyId);
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int id = keys.getInt(1);
                    conn.commit();
                    System.out.println("Booking created! ID: " + id);
                    return id;
                }
            }

            conn.rollback();

        } catch (SQLException e) {
            System.out.println("Error creating booking: " + e.getMessage());
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
        return -1;
    }

    // Close rental and generate bill
    public void closeBooking(int bookingId) {
        String getBooking   = "SELECT b.scooty_id, b.start_time, s.daily_rate FROM bookings b JOIN Scooty s ON b.scooty_id = s.scooty_id WHERE b.booking_id = ?";
        String updateBooking = "UPDATE bookings SET end_time=NOW(), total_hours=?, total_amount=?, status='closed' WHERE booking_id=?";
        String updateScooty  = "UPDATE Scooty SET status='Available' WHERE scooty_id=?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int scootyId = 0;
            double totalHours = 0;
            double totalAmount = 0;

            // Calculate hours and amount
            try (PreparedStatement ps = conn.prepareStatement(getBooking)) {
                ps.setInt(1, bookingId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    scootyId = rs.getInt("scooty_id");
                    Timestamp start = rs.getTimestamp("start_time");
                    double dailyRate = rs.getDouble("daily_rate");

                    long diffMs = System.currentTimeMillis() - start.getTime();
                    totalHours = diffMs / (1000.0 * 60 * 60);
                    if (totalHours < 1) totalHours = 1; // minimum 1 hour
                    totalAmount = totalHours * (dailyRate / 24); // convert daily to hourly
                }
            }

            // Update booking
            try (PreparedStatement ps = conn.prepareStatement(updateBooking)) {
                ps.setDouble(1, totalHours);
                ps.setDouble(2, totalAmount);
                ps.setInt(3, bookingId);
                ps.executeUpdate();
            }

            // Free scooty
            try (PreparedStatement ps = conn.prepareStatement(updateScooty)) {
                ps.setInt(1, scootyId);
                ps.executeUpdate();
            }

            conn.commit();

            // Print bill
            System.out.println("\n==============================");
            System.out.println("        RENTAL BILL           ");
            System.out.println("==============================");
            System.out.println("  Booking ID  : " + bookingId);
            System.out.printf("  Total Hours : %.2f hrs%n", totalHours);
            System.out.printf("  Total Amount: Rs.%.2f%n", totalAmount);
            System.out.println("  Status      : CLOSED");
            System.out.println("==============================\n");

        } catch (SQLException e) {
            System.out.println("Error closing booking: " + e.getMessage());
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
    }

    // View all active bookings
    public void viewActiveBookings() {
        String sql = "SELECT b.booking_id, c.name, s.model_name, b.start_time FROM bookings b " +
                     "JOIN customers c ON b.customer_id = c.customer_id " +
                     "JOIN Scooty s ON b.scooty_id = s.scooty_id " +
                     "WHERE b.status = 'active'";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Active Bookings ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("[" + rs.getInt("booking_id") + "] " +
                    rs.getString("name") + " | " +
                    rs.getString("model_name") + " | Since: " +
                    rs.getTimestamp("start_time"));
            }
            if (!found) System.out.println("No active bookings.");

        } catch (SQLException e) {
            System.out.println("Error fetching bookings: " + e.getMessage());
        }
    }
}