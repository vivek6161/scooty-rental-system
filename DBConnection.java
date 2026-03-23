import java.sql.*;

/**
 * DBConnection – Written by Member 1 (Backend Lead).
 * Stub included here so Member 4's code compiles independently.
 * Replace with Member 1's actual implementation.
 */
public class DBConnection {

    private static final String URL  = "jdbc:mysql://localhost:3306/ScootyRentalDB";
    private static final String USER = "root";
    private static final String PASS = "yourpassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
