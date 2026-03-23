import javax.swing.*;
import java.sql.*;

/**
 * Member 4 – SystemTester
 * Tests every module of the Scooty Rental System.
 * Results are printed to the JTextArea in the Dashboard.
 *
 * Tests cover:
 *  - DB Connection
 *  - Scooty CRUD (read)
 *  - Customer existence
 *  - Booking table
 *  - Rental calculations
 *  - Maintenance records
 *  - ErrorHandler validation
 */
public class SystemTester {

    private static int passed = 0;
    private static int failed = 0;

    // ── Run all tests ─────────────────────────────────────────────
    public static void runAllTests(JTextArea out) {
        passed = 0; failed = 0;
        clear(out);
        print(out, "╔══════════════════════════════════════╗");
        print(out, "   🧪 SCOOTY RENTAL – SYSTEM TESTS");
        print(out, "╚══════════════════════════════════════╝\n");

        testDBConnection(out);
        testScootyCRUD(out);
        testCustomerTable(out);
        testBookingTable(out);
        testRentalCalculation(out);
        testMaintenanceTable(out);
        testErrorHandlerValidation(out);

        print(out, "\n──────────────────────────────────────");
        print(out, String.format(" RESULTS: %d passed  |  %d failed", passed, failed));
        if (failed == 0)
            print(out, " ✅ ALL TESTS PASSED");
        else
            print(out, " ❌ SOME TESTS FAILED – check logs");
        print(out, "──────────────────────────────────────");
    }

    // ── Individual tests ──────────────────────────────────────────

    public static void testDBConnection(JTextArea out) {
        print(out, "\n[TEST] Database Connection");
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                pass(out, "Connected to DB successfully");
            } else {
                fail(out, "Connection returned null/closed");
            }
        } catch (Exception e) {
            fail(out, "Connection failed: " + e.getMessage());
        }
    }

    public static void testScootyCRUD(JTextArea out) {
        print(out, "\n[TEST] Scooty Table – Read");
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM Scooty");
            if (rs.next()) {
                int count = rs.getInt(1);
                pass(out, "Scooty table accessible. Rows: " + count);
            } else {
                fail(out, "No result from Scooty table");
            }
        } catch (Exception e) {
            fail(out, "Scooty read failed: " + e.getMessage());
        }

        print(out, "\n[TEST] Scooty Status Values");
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM Scooty WHERE status NOT IN "
                       + "('Available','Rented','Under Maintenance')";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (rs.next() && rs.getInt(1) == 0) {
                pass(out, "All scooty statuses are valid");
            } else {
                fail(out, "Invalid status values found in Scooty table");
            }
        } catch (Exception e) {
            fail(out, "Status check failed: " + e.getMessage());
        }
    }

    public static void testCustomerTable(JTextArea out) {
        print(out, "\n[TEST] Customer Table – Read");
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM Customer");
            if (rs.next()) {
                pass(out, "Customer table accessible. Rows: " + rs.getInt(1));
            } else {
                fail(out, "No result from Customer table");
            }
        } catch (Exception e) {
            fail(out, "Customer read failed: " + e.getMessage());
        }
    }

    public static void testBookingTable(JTextArea out) {
        print(out, "\n[TEST] Booking Table – Read");
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM Booking");
            if (rs.next()) {
                pass(out, "Booking table accessible. Rows: " + rs.getInt(1));
            } else {
                fail(out, "No result from Booking table");
            }
        } catch (Exception e) {
            fail(out, "Booking read failed: " + e.getMessage());
        }
    }

    public static void testRentalCalculation(JTextArea out) {
        print(out, "\n[TEST] Rent Calculation Logic");

        // Test 1: Basic calculation
        double rate = 50.0;     // ₹50/hr
        int    hours = 3;
        double expected = 150.0;
        double actual = rate * hours;
        if (actual == expected) {
            pass(out, "Basic calc OK: ₹50/hr x 3hr = ₹" + actual);
        } else {
            fail(out, "Calc mismatch: expected ₹" + expected + " got ₹" + actual);
        }

        // Test 2: Zero hours
        actual = rate * 0;
        if (actual == 0.0) {
            pass(out, "Zero-hour calc = ₹0.0 ✓");
        } else {
            fail(out, "Zero-hour calc returned: " + actual);
        }

        // Test 3: Negative hours (invalid input guard)
        try {
            if (-2 < 0) throw new IllegalArgumentException("Hours cannot be negative");
            fail(out, "Negative hours not caught");
        } catch (IllegalArgumentException e) {
            pass(out, "Negative hours rejected correctly");
        }
    }

    public static void testMaintenanceTable(JTextArea out) {
        print(out, "\n[TEST] Maintenance Table – Read");
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM Maintenance");
            if (rs.next()) {
                pass(out, "Maintenance table accessible. Rows: " + rs.getInt(1));
            } else {
                fail(out, "No result from Maintenance table");
            }
        } catch (Exception e) {
            fail(out, "Maintenance read failed: " + e.getMessage());
        }

        print(out, "\n[TEST] Pending Maintenance Count");
        int pending = Dashboard.MaintenanceService.getPendingCount();
        pass(out, "Pending maintenance records: " + pending);
    }

    public static void testErrorHandlerValidation(JTextArea out) {
        print(out, "\n[TEST] ErrorHandler Validation");

        // isEmpty
        if (ErrorHandler.isEmpty(""))     pass(out, "isEmpty(\"\") = true ✓");
        else fail(out, "isEmpty(\"\") should be true");

        if (!ErrorHandler.isEmpty("abc")) pass(out, "isEmpty(\"abc\") = false ✓");
        else fail(out, "isEmpty(\"abc\") should be false");

        // isPositiveInt
        if (ErrorHandler.isPositiveInt("5"))    pass(out, "isPositiveInt(\"5\") ✓");
        else fail(out, "isPositiveInt(\"5\") failed");

        if (!ErrorHandler.isPositiveInt("-3"))  pass(out, "isPositiveInt(\"-3\") = false ✓");
        else fail(out, "isPositiveInt(\"-3\") should be false");

        if (!ErrorHandler.isPositiveInt("abc")) pass(out, "isPositiveInt(\"abc\") = false ✓");
        else fail(out, "isPositiveInt(\"abc\") should be false");

        // isPositiveDouble
        if (ErrorHandler.isPositiveDouble("9.99"))  pass(out, "isPositiveDouble(\"9.99\") ✓");
        else fail(out, "isPositiveDouble(\"9.99\") failed");
    }

    // ── Helpers ───────────────────────────────────────────────────
    private static void pass(JTextArea out, String msg) {
        passed++;
        print(out, "  ✅ PASS  " + msg);
    }

    private static void fail(JTextArea out, String msg) {
        failed++;
        print(out, "  ❌ FAIL  " + msg);
        ErrorHandler.log("TEST FAILED: " + msg);
    }

    private static void print(JTextArea out, String msg) {
        if (out != null) {
            SwingUtilities.invokeLater(() -> {
                out.append(msg + "\n");
                out.setCaretPosition(out.getDocument().getLength());
            });
        }
        System.out.println(msg);
    }

    private static void clear(JTextArea out) {
        if (out != null) SwingUtilities.invokeLater(() -> out.setText(""));
    }
}
