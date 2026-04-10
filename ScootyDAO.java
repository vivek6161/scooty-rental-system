

import com.scooty.db.DBConnection;
import com.scooty.model.Scooty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ScootyDAO.java
 * Data Access Object — all JDBC / SQL operations for the Scooty table.
 * Member 2 – Admin & Scooty Management Module
 *
 * Table assumed:
 *   CREATE TABLE scooties (
 *       scooty_id     INT PRIMARY KEY AUTO_INCREMENT,
 *       model         VARCHAR(100) NOT NULL,
 *       brand         VARCHAR(100) NOT NULL,
 *       reg_number    VARCHAR(50)  UNIQUE NOT NULL,
 *       status        ENUM('available','booked','maintenance') DEFAULT 'available',
 *       price_per_hr  DOUBLE       NOT NULL,
 *       added_date    DATE         DEFAULT (CURRENT_DATE)
 *   );
 */
public class ScootyDAO {

    // ─── CREATE ───────────────────────────────────────────────────────────────

    /**
     * Inserts a new Scooty record into the database.
     *
     * @param s Scooty object to add
     * @return true if insert was successful
     */
    public boolean addScooty(Scooty s) {
        String sql = "INSERT INTO scooties (model, brand, reg_number, status, price_per_hr) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, s.getModel());
            stmt.setString(2, s.getBrand());
            stmt.setString(3, s.getRegNumber());
            stmt.setString(4, s.getStatus());
            stmt.setDouble(5, s.getPricePerHr());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    s.setScootyId(keys.getInt(1));
                }
                System.out.println("[SUCCESS] Scooty added with ID: " + s.getScootyId());
                return true;
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("[ERROR] Registration number already exists: " + s.getRegNumber());
        } catch (SQLException e) {
            System.out.println("[ERROR] addScooty failed: " + e.getMessage());
        }
        return false;
    }

    // ─── READ (all) ───────────────────────────────────────────────────────────

    /**
     * Retrieves all scooties from the database.
     *
     * @return List of Scooty objects (empty list if none found)
     */
    public List<Scooty> getAllScooties() {
        List<Scooty> list = new ArrayList<>();
        String sql = "SELECT * FROM scooties ORDER BY scooty_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

            if (list.isEmpty()) {
                System.out.println("[INFO] No scooties found in the system.");
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] getAllScooties failed: " + e.getMessage());
        }
        return list;
    }

    // ─── READ (by ID) ─────────────────────────────────────────────────────────

    /**
     * Retrieves a single Scooty by its ID.
     *
     * @param scootyId The scooty ID to look up
     * @return Scooty object, or null if not found
     */
    public Scooty getScootyById(int scootyId) {
        String sql = "SELECT * FROM scooties WHERE scooty_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, scootyId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            } else {
                System.out.println("[INFO] No scooty found with ID: " + scootyId);
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] getScootyById failed: " + e.getMessage());
        }
        return null;
    }

    // ─── READ (by status) ─────────────────────────────────────────────────────

    /**
     * Retrieves all scooties filtered by availability status.
     *
     * @param status "available", "booked", or "maintenance"
     * @return Filtered list of Scooty objects
     */
    public List<Scooty> getScootiesByStatus(String status) {
        List<Scooty> list = new ArrayList<>();
        String sql = "SELECT * FROM scooties WHERE status = ? ORDER BY scooty_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

            System.out.println("[INFO] Found " + list.size() + " scooty/scooties with status: " + status);

        } catch (SQLException e) {
            System.out.println("[ERROR] getScootiesByStatus failed: " + e.getMessage());
        }
        return list;
    }

    // ─── UPDATE (full record) ─────────────────────────────────────────────────

    /**
     * Updates all fields of an existing Scooty record.
     *
     * @param s Scooty object with updated values (must have valid scootyId)
     * @return true if update was successful
     */
    public boolean updateScooty(Scooty s) {
        String sql = "UPDATE scooties SET model = ?, brand = ?, reg_number = ?, " +
                     "status = ?, price_per_hr = ? WHERE scooty_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, s.getModel());
            stmt.setString(2, s.getBrand());
            stmt.setString(3, s.getRegNumber());
            stmt.setString(4, s.getStatus());
            stmt.setDouble(5, s.getPricePerHr());
            stmt.setInt(6, s.getScootyId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[SUCCESS] Scooty ID " + s.getScootyId() + " updated.");
                return true;
            } else {
                System.out.println("[INFO] No scooty found with ID: " + s.getScootyId());
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("[ERROR] Registration number conflict: " + s.getRegNumber());
        } catch (SQLException e) {
            System.out.println("[ERROR] updateScooty failed: " + e.getMessage());
        }
        return false;
    }

    // ─── UPDATE (availability only) ───────────────────────────────────────────

    /**
     * Changes only the availability status of a scooty.
     * Use this for quick status toggles without loading the full object.
     *
     * @param scootyId The scooty to update
     * @param status   New status: "available", "booked", or "maintenance"
     * @return true if update was successful
     */
    public boolean updateAvailability(int scootyId, String status) {
        String sql = "UPDATE scooties SET status = ? WHERE scooty_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, scootyId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[SUCCESS] Scooty ID " + scootyId + " status set to: " + status);
                return true;
            } else {
                System.out.println("[INFO] No scooty found with ID: " + scootyId);
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] updateAvailability failed: " + e.getMessage());
        }
        return false;
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    /**
     * Deletes a Scooty record permanently from the database.
     *
     * @param scootyId ID of the scooty to delete
     * @return true if deletion was successful
     */
    public boolean deleteScooty(int scootyId) {
        String sql = "DELETE FROM scooties WHERE scooty_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, scootyId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("[SUCCESS] Scooty ID " + scootyId + " deleted.");
                return true;
            } else {
                System.out.println("[INFO] No scooty found with ID: " + scootyId);
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] deleteScooty failed: " + e.getMessage());
        }
        return false;
    }

    // ─── DUPLICATE CHECK ──────────────────────────────────────────────────────

    /**
     * Checks if a registration number already exists.
     * Call this before addScooty() to prevent duplicate IDs.
     *
     * @param regNumber Registration number to check
     * @return true if duplicate exists
     */
    public boolean isDuplicateRegNumber(String regNumber) {
        String sql = "SELECT 1 FROM scooties WHERE reg_number = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, regNumber.trim());
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("[ERROR] isDuplicateRegNumber failed: " + e.getMessage());
            return false;
        }
    }

    // ─── DISPLAY HELPER ───────────────────────────────────────────────────────

    /**
     * Prints a formatted table of all scooties to the console.
     */
    public void displayAllScooties() {
        List<Scooty> list = getAllScooties();

        System.out.println("\n╔══════╦══════════════════════╦══════════════╦════════════════╦═════════════╦══════════════╗");
        System.out.printf( "║ %-4s ║ %-20s ║ %-12s ║ %-14s ║ %-11s ║ %-12s ║%n",
                           "ID", "Model", "Brand", "Reg Number", "Status", "Price/Hr");
        System.out.println("╠══════╬══════════════════════╬══════════════╬════════════════╬═════════════╬══════════════╣");

        if (list.isEmpty()) {
            System.out.println("║  No records found.                                                                      ║");
        } else {
            for (Scooty s : list) {
                System.out.printf("║ %-4d ║ %-20s ║ %-12s ║ %-14s ║ %-11s ║ %-12.2f ║%n",
                        s.getScootyId(),
                        s.getModel(),
                        s.getBrand(),
                        s.getRegNumber(),
                        s.getStatus(),
                        s.getPricePerHr());
            }
        }
        System.out.println("╚══════╩══════════════════════╩══════════════╩════════════════╩═════════════╩══════════════╝\n");
    }

    // ─── PRIVATE MAPPER ───────────────────────────────────────────────────────

    /**
     * Maps a ResultSet row to a Scooty object.
     * Centralised here so every read method stays clean.
     */
    private Scooty mapRow(ResultSet rs) throws SQLException {
        Scooty s = new Scooty();
        s.setScootyId(rs.getInt("scooty_id"));
        s.setModel(rs.getString("model"));
        s.setBrand(rs.getString("brand"));
        s.setRegNumber(rs.getString("reg_number"));
        s.setStatus(rs.getString("status"));
        s.setPricePerHr(rs.getDouble("price_per_hr"));
        s.setAddedDate(rs.getDate("added_date"));
        return s;
    }

    // ─── Quick Test ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        ScootyDAO dao = new ScootyDAO();

        // Add
        Scooty s = new Scooty();
        s.setModel("Activa 6G");
        s.setBrand("Honda");
        s.setRegNumber("MH12AB1234");
        s.setStatus("available");
        s.setPricePerHr(50.0);
        dao.addScooty(s);

        // Display all
        dao.displayAllScooties();

        // Update availability
        dao.updateAvailability(s.getScootyId(), "maintenance");

        // Get by ID
        Scooty found = dao.getScootyById(s.getScootyId());
        if (found != null) System.out.println("Found: " + found);

        // Delete
        dao.deleteScooty(s.getScootyId());
        dao.displayAllScooties();
    }
}
