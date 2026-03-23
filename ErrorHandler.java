import javax.swing.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Member 4 – ErrorHandler
 * Centralized error handling for the entire system.
 * All modules use this class for consistent user feedback and logging.
 */
public class ErrorHandler {

    private static final String LOG_FILE = "error_log.txt";
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ── Show dialogs ──────────────────────────────────────────────

    /** Show a red error popup */
    public static void showError(String message) {
        JOptionPane.showMessageDialog(null,
            "<html><b style='color:red;'>❌ Error</b><br>" + message + "</html>",
            "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Show a yellow warning popup */
    public static void showWarning(String message) {
        JOptionPane.showMessageDialog(null,
            "<html><b style='color:orange;'>⚠️ Warning</b><br>" + message + "</html>",
            "Warning", JOptionPane.WARNING_MESSAGE);
    }

    /** Show a green success popup */
    public static void showSuccess(String message) {
        JOptionPane.showMessageDialog(null,
            "<html><b style='color:green;'>✅ Success</b><br>" + message + "</html>",
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Show general info popup */
    public static void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // ── File logging ──────────────────────────────────────────────

    /**
     * Log error to file + console.
     * All modules should call this instead of printStackTrace.
     */
    public static void log(String message) {
        String entry = "[" + LocalDateTime.now().format(FMT) + "] ERROR: " + message;
        System.err.println(entry);

        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(entry);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Could not write to error log: " + e.getMessage());
        }
    }

    /** Log a SQL exception with context */
    public static void logSQL(String context, Exception ex) {
        log("[SQL] " + context + " → " + ex.getMessage());
    }

    // ── Validation helpers ────────────────────────────────────────

    /** Returns true if string is null or blank */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** Returns true if string is a valid positive integer */
    public static boolean isPositiveInt(String s) {
        try {
            return Integer.parseInt(s.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Returns true if string is a valid positive double */
    public static boolean isPositiveDouble(String s) {
        try {
            return Double.parseDouble(s.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate a field and show warning if invalid.
     * @return true if valid
     */
    public static boolean validateNotEmpty(String value, String fieldName) {
        if (isEmpty(value)) {
            showWarning(fieldName + " cannot be empty.");
            return false;
        }
        return true;
    }

    public static boolean validatePositiveNumber(String value, String fieldName) {
        if (!isPositiveDouble(value)) {
            showWarning(fieldName + " must be a positive number.");
            return false;
        }
        return true;
    }

    // ── Confirm dialog ────────────────────────────────────────────

    /** Ask for confirmation before destructive actions */
    public static boolean confirm(String message) {
        int result = JOptionPane.showConfirmDialog(null,
            message, "Confirm", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
}
