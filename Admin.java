

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Admin.java
 * Handles admin authentication: login, logout, session state.
 * Member 2 – Admin & Scooty Management Module
 */
public class Admin {

    // ─── Fields ───────────────────────────────────────────────────────────────

    private int adminId;
    private String username;
    private String email;
    private boolean authenticated;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public Admin() {
        this.authenticated = false;
    }

    // ─── Core Auth Methods ────────────────────────────────────────────────────

    /**
     * Validates credentials against the database.
     * Passwords should be hashed (e.g. BCrypt) in production.
     *
     * @param username Admin username
     * @param password Plain-text password (hash before storing in DB)
     * @return true if login successful, false otherwise
     */
    public boolean login(String username, String password) {

        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            System.out.println("[ERROR] Username and password cannot be empty.");
            return false;
        }

        String sql = "SELECT admin_id, username, email FROM admins " +
                     "WHERE username = ? AND password = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            stmt.setString(2, password.trim()); // replace with hash check in production

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                this.adminId       = rs.getInt("admin_id");
                this.username      = rs.getString("username");
                this.email         = rs.getString("email");
                this.authenticated = true;

                System.out.println("[SUCCESS] Welcome, " + this.username + "!");
                return true;
            } else {
                System.out.println("[FAILED] Invalid username or password.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Login failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Logs out the current admin and clears session data.
     */
    public void logout() {
        if (!this.authenticated) {
            System.out.println("[INFO] No admin is currently logged in.");
            return;
        }
        System.out.println("[INFO] Goodbye, " + this.username + ". Logged out successfully.");
        this.adminId       = 0;
        this.username      = null;
        this.email         = null;
        this.authenticated = false;
    }

    /**
     * Checks if an admin is currently authenticated.
     *
     * @return true if logged in
     */
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    // ─── Validation Helpers ───────────────────────────────────────────────────

    /**
     * Checks whether a username already exists in the database.
     * Useful when creating a new admin account.
     *
     * @param username Username to check
     * @return true if username is taken
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM admins WHERE username = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("[ERROR] Could not check username: " + e.getMessage());
            return false;
        }
    }

    /**
     * Basic input validation: not null, not blank, within length.
     *
     * @param input  The string to validate
     * @param field  Field name for error messages
     * @param maxLen Maximum allowed length
     * @return true if valid
     */
    public boolean validateInput(String input, String field, int maxLen) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println("[VALIDATION] " + field + " cannot be empty.");
            return false;
        }
        if (input.trim().length() > maxLen) {
            System.out.println("[VALIDATION] " + field + " must be under " + maxLen + " characters.");
            return false;
        }
        return true;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public int getAdminId()     { return adminId; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Admin{" +
               "adminId=" + adminId +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", authenticated=" + authenticated +
               '}';
    }

    // ─── Quick Test ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        Admin admin = new Admin();

        System.out.println("=== Admin Login Test ===");

        // Test 1: empty credentials
        admin.login("", "");

        // Test 2: wrong credentials
        admin.login("admin", "wrongpass");

        // Test 3: correct credentials (must exist in your DB)
        boolean success = admin.login("admin", "admin123");

        if (success) {
            System.out.println("Logged in as: " + admin.getUsername());
            System.out.println("Is authenticated: " + admin.isAuthenticated());
            admin.logout();
            System.out.println("After logout, authenticated: " + admin.isAuthenticated());
        }
    }
}
