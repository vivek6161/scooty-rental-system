import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Member 4 – SystemLogger
 * Keeps an in-memory log and updates the JTextArea in the Dashboard.
 * All modules can call SystemLogger.log() to append messages.
 */
public class SystemLogger {

    private static final List<String> LOG_BUFFER = new ArrayList<>();
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Log a message to the buffer and optionally update a JTextArea.
     * @param message  The log message
     * @param logArea  JTextArea in the Dashboard (can be null)
     */
    public static void log(String message, JTextArea logArea) {
        String entry = "[" + LocalDateTime.now().format(FMT) + "]  " + message;
        LOG_BUFFER.add(entry);
        System.out.println(entry);

        if (logArea != null) {
            SwingUtilities.invokeLater(() -> {
                logArea.append(entry + "\n");
                // Auto-scroll to bottom
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }
    }

    /** Overload: log without UI area */
    public static void log(String message) {
        log(message, null);
    }

    /** Get all logs as a single string */
    public static String getLogs() {
        StringBuilder sb = new StringBuilder();
        for (String line : LOG_BUFFER) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /** Clear in-memory logs */
    public static void clearLogs() {
        LOG_BUFFER.clear();
    }

    /** Get count of log entries */
    public static int getLogCount() {
        return LOG_BUFFER.size();
    }
}
