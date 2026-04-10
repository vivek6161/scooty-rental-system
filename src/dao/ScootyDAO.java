package dao;

import database.DBConnection;
import models.Scooty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScootyDAO {

    public boolean addScooty(Scooty s) {
        String sql = "INSERT INTO Scooty (model_name, brand, reg_number, daily_rate, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getModelName());
            ps.setString(2, s.getBrand());
            ps.setString(3, s.getRegNumber());
            ps.setDouble(4, s.getDailyRate());
            ps.setString(5, "Available");

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding scooty: " + e.getMessage());
            return false;
        }
    }

    public List<Scooty> getAllScooties() {
        List<Scooty> list = new ArrayList<>();
        String sql = "SELECT * FROM Scooty";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Scooty s = new Scooty(
                    rs.getString("model_name"),
                    rs.getString("brand"),
                    rs.getString("reg_number"),
                    rs.getDouble("daily_rate")
                );
                s.setScootyId(rs.getInt("scooty_id"));
                s.setStatus(rs.getString("status"));
                list.add(s);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching scooties: " + e.getMessage());
        }
        return list;
    }

    public boolean updateStatus(int scootyId, String newStatus) {
        String sql = "UPDATE Scooty SET status = ? WHERE scooty_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, scootyId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating status: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteScooty(int scootyId) {
        String sql = "DELETE FROM Scooty WHERE scooty_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, scootyId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting scooty: " + e.getMessage());
            return false;
        }
    }
}