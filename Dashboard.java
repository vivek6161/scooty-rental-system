import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Member 4 – Dashboard, Maintenance & Testing Lead
 * Responsibility: System Presentation & Quality
 * Explains: Abstraction + System Architecture
 */
public class Dashboard extends JFrame {

    // ─── Colors & Fonts ───────────────────────────────────────────
    private static final Color BG_DARK      = new Color(15, 23, 42);
    private static final Color CARD_BG      = new Color(30, 41, 59);
    private static final Color ACCENT_BLUE  = new Color(59, 130, 246);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ACCENT_AMBER = new Color(251, 191, 36);
    private static final Color ACCENT_RED   = new Color(239, 68, 68);
    private static final Color TEXT_MAIN    = new Color(248, 250, 252);
    private static final Color TEXT_MUTED   = new Color(148, 163, 184);
    private static final Font  FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font  FONT_CARD    = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font  FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_BTN     = new Font("Segoe UI", Font.BOLD, 13);

    // ─── Stat labels (updated live) ───────────────────────────────
    private JLabel lblTotalScooties   = new JLabel("0");
    private JLabel lblActiveRentals   = new JLabel("0");
    private JLabel lblAvailableScooties = new JLabel("0");
    private JLabel lblTotalEarnings   = new JLabel("₹0");

    // ─── Panels ───────────────────────────────────────────────────
    private JPanel mainPanel;
    private JPanel currentView;
    private JTextArea logArea;

    public Dashboard() {
        setTitle("🛵 Scooty Rental – Dashboard");
        setSize(1100, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BG_DARK);
        buildUI();
        refreshStats();
        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════
    //  UI CONSTRUCTION
    // ══════════════════════════════════════════════════════════════
    private void buildUI() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_DARK);

        mainPanel.add(buildSidebar(), BorderLayout.WEST);
        mainPanel.add(buildMainArea(), BorderLayout.CENTER);

        add(mainPanel);
    }

    // ── Sidebar ──────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(15, 23, 42));
        sidebar.setPreferredSize(new Dimension(200, 680));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(51, 65, 85)));

        // Logo
        JLabel logo = new JLabel("🛵 ScootyRMS");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(ACCENT_BLUE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(new EmptyBorder(20, 10, 20, 10));
        sidebar.add(logo);
        sidebar.add(new JSeparator());

        // Nav buttons
        String[][] navItems = {
            {"📊", "Dashboard"},
            {"🔧", "Maintenance"},
            {"📋", "All Scooties"},
            {"🧪", "Run Tests"},
            {"📜", "System Logs"},
        };

        for (String[] item : navItems) {
            JButton btn = createNavButton(item[0] + "  " + item[1]);
            btn.addActionListener(e -> handleNav(item[1]));
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        sidebar.add(Box.createVerticalGlue());

        // Refresh button at bottom
        JButton refreshBtn = createNavButton("🔄  Refresh Stats");
        refreshBtn.setForeground(ACCENT_GREEN);
        refreshBtn.addActionListener(e -> refreshStats());
        sidebar.add(refreshBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        return sidebar;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_LABEL);
        btn.setForeground(TEXT_MUTED);
        btn.setBackground(BG_DARK);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 20, 8, 10));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(TEXT_MAIN); }
            public void mouseExited(MouseEvent e)  { btn.setForeground(TEXT_MUTED); }
        });
        return btn;
    }

    // ── Main area ────────────────────────────────────────────────
    private JPanel buildMainArea() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(BG_DARK);
        area.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JLabel header = new JLabel("Dashboard Overview");
        header.setFont(FONT_TITLE);
        header.setForeground(TEXT_MAIN);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        area.add(header, BorderLayout.NORTH);

        // Stat cards
        JPanel cards = buildStatCards();
        area.add(cards, BorderLayout.CENTER);

        // Log area at bottom
        logArea = new JTextArea(6, 40);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(15, 23, 42));
        logArea.setForeground(new Color(134, 239, 172));
        logArea.setEditable(false);
        logArea.setBorder(new EmptyBorder(8, 12, 8, 12));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85)),
            "  System Log  ", TitledBorder.LEFT, TitledBorder.TOP,
            FONT_LABEL, TEXT_MUTED));
        logScroll.setBackground(BG_DARK);
        area.add(logScroll, BorderLayout.SOUTH);

        currentView = area;
        return area;
    }

    // ── Stat cards ───────────────────────────────────────────────
    private JPanel buildStatCards() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setBackground(BG_DARK);

        grid.add(createStatCard("🛵 Total Scooties",   lblTotalScooties,     ACCENT_BLUE));
        grid.add(createStatCard("🔑 Active Rentals",   lblActiveRentals,     ACCENT_GREEN));
        grid.add(createStatCard("✅ Available",         lblAvailableScooties, ACCENT_AMBER));
        grid.add(createStatCard("💰 Total Earnings",   lblTotalEarnings,     ACCENT_RED));

        return grid;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accent.darker(), 1),
            new EmptyBorder(20, 24, 20, 24)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_LABEL);
        titleLbl.setForeground(TEXT_MUTED);

        valueLabel.setFont(FONT_CARD);
        valueLabel.setForeground(accent);

        JPanel topBar = new JPanel();
        topBar.setBackground(accent);
        topBar.setPreferredSize(new Dimension(0, 4));

        card.add(topBar, BorderLayout.NORTH);
        card.add(titleLbl, BorderLayout.CENTER);
        card.add(valueLabel, BorderLayout.SOUTH);

        return card;
    }

    // ══════════════════════════════════════════════════════════════
    //  ABSTRACTION – Abstract base for all modules (OOP concept)
    // ══════════════════════════════════════════════════════════════
    abstract static class RentalModule {
        protected String moduleName;

        public RentalModule(String name) {
            this.moduleName = name;
        }

        /** Each module must implement its own display logic */
        public abstract JPanel buildPanel();

        /** Each module must provide a status summary */
        public abstract String getStatus();
    }

    // ══════════════════════════════════════════════════════════════
    //  MAINTENANCE MODULE  (extends RentalModule – Abstraction)
    // ══════════════════════════════════════════════════════════════
    class MaintenanceModule extends RentalModule {

        public MaintenanceModule() {
            super("Maintenance");
        }

        @Override
        public JPanel buildPanel() {
            JPanel panel = new JPanel(new BorderLayout(0, 16));
            panel.setBackground(BG_DARK);

            // Title
            JLabel title = new JLabel("🔧 Maintenance Management");
            title.setFont(FONT_TITLE);
            title.setForeground(TEXT_MAIN);
            panel.add(title, BorderLayout.NORTH);

            // Form to add maintenance record
            JPanel form = new JPanel(new GridLayout(4, 2, 12, 12));
            form.setBackground(CARD_BG);
            form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)),
                new EmptyBorder(16, 16, 16, 16)
            ));

            form.add(styledLabel("Scooty ID:"));
            JTextField tfScootyId = styledField();
            form.add(tfScootyId);

            form.add(styledLabel("Issue Description:"));
            JTextField tfIssue = styledField();
            form.add(tfIssue);

            form.add(styledLabel("Cost (₹):"));
            JTextField tfCost = styledField();
            form.add(tfCost);

            JButton btnAdd = styledButton("➕ Add Maintenance Record", ACCENT_AMBER);
            btnAdd.addActionListener(e -> {
                MaintenanceService.addRecord(
                    tfScootyId.getText().trim(),
                    tfIssue.getText().trim(),
                    tfCost.getText().trim(),
                    logArea
                );
                tfScootyId.setText(""); tfIssue.setText(""); tfCost.setText("");
            });
            form.add(styledLabel(""));
            form.add(btnAdd);

            panel.add(form, BorderLayout.CENTER);

            // Maintenance alert table
            panel.add(buildMaintenanceTable(), BorderLayout.SOUTH);

            return panel;
        }

        @Override
        public String getStatus() {
            return MaintenanceService.getPendingCount() + " pending maintenance records";
        }

        private JScrollPane buildMaintenanceTable() {
            String[] cols = {"ID", "Scooty ID", "Issue", "Cost (₹)", "Date", "Status"};
            DefaultTableModel model = new DefaultTableModel(cols, 0);

            // Load from DB
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT * FROM Maintenance ORDER BY maintenance_date DESC";
                ResultSet rs = conn.createStatement().executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("maintenance_id"),
                        rs.getInt("scooty_id"),
                        rs.getString("issue"),
                        "₹" + rs.getDouble("cost"),
                        rs.getDate("maintenance_date"),
                        rs.getString("status")
                    });
                }
            } catch (SQLException ex) {
                ErrorHandler.log("MaintenanceTable load failed: " + ex.getMessage());
            }

            JTable table = new JTable(model);
            styleTable(table);
            JScrollPane scroll = new JScrollPane(table);
            scroll.setPreferredSize(new Dimension(0, 180));
            scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)),
                "  Maintenance Records  ", TitledBorder.LEFT, TitledBorder.TOP,
                FONT_LABEL, TEXT_MUTED));
            scroll.setBackground(CARD_BG);
            return scroll;
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  MAINTENANCE SERVICE – Business logic for maintenance
    // ══════════════════════════════════════════════════════════════
    static class MaintenanceService {

        /** Add a new maintenance record and mark scooty unavailable */
        public static void addRecord(String scootyId, String issue, String costStr, JTextArea log) {
            if (scootyId.isEmpty() || issue.isEmpty() || costStr.isEmpty()) {
                ErrorHandler.showWarning("All fields are required for maintenance record.");
                return;
            }

            double cost;
            try {
                cost = Double.parseDouble(costStr);
                if (cost < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                ErrorHandler.showWarning("Cost must be a valid positive number.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                // Insert maintenance record
                String insertSQL = "INSERT INTO Maintenance (scooty_id, issue, cost, maintenance_date, status) "
                                 + "VALUES (?, ?, ?, CURDATE(), 'Pending')";
                PreparedStatement ps = conn.prepareStatement(insertSQL);
                ps.setInt(1, Integer.parseInt(scootyId));
                ps.setString(2, issue);
                ps.setDouble(3, cost);
                ps.executeUpdate();

                // Mark scooty as Under Maintenance
                String updateSQL = "UPDATE Scooty SET status = 'Under Maintenance' WHERE scooty_id = ?";
                PreparedStatement ps2 = conn.prepareStatement(updateSQL);
                ps2.setInt(1, Integer.parseInt(scootyId));
                ps2.executeUpdate();

                SystemLogger.log("Maintenance record added for Scooty #" + scootyId, log);
                ErrorHandler.showSuccess("Maintenance record added. Scooty marked as Under Maintenance.");

            } catch (SQLException ex) {
                ErrorHandler.log("Failed to add maintenance record: " + ex.getMessage());
                ErrorHandler.showError("Database error: " + ex.getMessage());
            }
        }

        /** Mark maintenance as completed */
        public static void completeRecord(int maintenanceId, int scootyId) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql1 = "UPDATE Maintenance SET status = 'Completed' WHERE maintenance_id = ?";
                PreparedStatement ps = conn.prepareStatement(sql1);
                ps.setInt(1, maintenanceId);
                ps.executeUpdate();

                String sql2 = "UPDATE Scooty SET status = 'Available' WHERE scooty_id = ?";
                PreparedStatement ps2 = conn.prepareStatement(sql2);
                ps2.setInt(1, scootyId);
                ps2.executeUpdate();

            } catch (SQLException ex) {
                ErrorHandler.log("Failed to complete maintenance: " + ex.getMessage());
            }
        }

        /** Count pending maintenance items */
        public static int getPendingCount() {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT COUNT(*) FROM Maintenance WHERE status = 'Pending'";
                ResultSet rs = conn.createStatement().executeQuery(sql);
                if (rs.next()) return rs.getInt(1);
            } catch (SQLException ex) {
                ErrorHandler.log("getPendingCount failed: " + ex.getMessage());
            }
            return 0;
        }

        /** Alert if any scooties have been rented for too long (overdue check) */
        public static void checkOverdueAlerts(JTextArea log) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT r.rental_id, c.name, s.model, r.start_date "
                           + "FROM Rental r "
                           + "JOIN Booking b ON r.booking_id = b.booking_id "
                           + "JOIN Customer c ON b.customer_id = c.customer_id "
                           + "JOIN Scooty s ON b.scooty_id = s.scooty_id "
                           + "WHERE r.status = 'Active' AND r.start_date < DATE_SUB(NOW(), INTERVAL 3 DAY)";
                ResultSet rs = conn.createStatement().executeQuery(sql);
                int count = 0;
                while (rs.next()) {
                    count++;
                    SystemLogger.log("⚠️ OVERDUE: Rental #" + rs.getInt("rental_id")
                        + " | Customer: " + rs.getString("name")
                        + " | Scooty: " + rs.getString("model")
                        + " | Since: " + rs.getDate("start_date"), log);
                }
                if (count == 0) SystemLogger.log("✅ No overdue rentals found.", log);
            } catch (SQLException ex) {
                ErrorHandler.log("Overdue check failed: " + ex.getMessage());
            }
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  DASHBOARD STATS – Read all summary data from DB
    // ══════════════════════════════════════════════════════════════
    private void refreshStats() {
        try (Connection conn = DBConnection.getConnection()) {

            // Total scooties
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM Scooty");
            if (rs1.next()) lblTotalScooties.setText(String.valueOf(rs1.getInt(1)));

            // Active rentals
            ResultSet rs2 = conn.createStatement().executeQuery(
                "SELECT COUNT(*) FROM Rental WHERE status = 'Active'");
            if (rs2.next()) lblActiveRentals.setText(String.valueOf(rs2.getInt(1)));

            // Available scooties
            ResultSet rs3 = conn.createStatement().executeQuery(
                "SELECT COUNT(*) FROM Scooty WHERE status = 'Available'");
            if (rs3.next()) lblAvailableScooties.setText(String.valueOf(rs3.getInt(1)));

            // Total earnings
            ResultSet rs4 = conn.createStatement().executeQuery(
                "SELECT SUM(total_amount) FROM Rental WHERE status = 'Completed'");
            if (rs4.next()) {
                double earnings = rs4.getDouble(1);
                lblTotalEarnings.setText("₹" + String.format("%.0f", earnings));
            }

            SystemLogger.log("Dashboard stats refreshed.", logArea);

        } catch (SQLException ex) {
            ErrorHandler.log("Stats refresh failed: " + ex.getMessage());
            lblTotalScooties.setText("--");
            lblActiveRentals.setText("--");
            lblAvailableScooties.setText("--");
            lblTotalEarnings.setText("--");
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  SCOOTY LIST VIEW
    // ══════════════════════════════════════════════════════════════
    private JPanel buildAllScootiesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_DARK);

        JLabel title = new JLabel("🛵 All Scooties");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Model", "Brand", "Color", "Rate/hr (₹)", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Scooty ORDER BY scooty_id";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("scooty_id"),
                    rs.getString("model"),
                    rs.getString("brand"),
                    rs.getString("color"),
                    "₹" + rs.getDouble("rate_per_hour"),
                    rs.getString("status")
                });
            }
        } catch (SQLException ex) {
            ErrorHandler.log("Load scooties failed: " + ex.getMessage());
        }

        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                String status = (String) getValueAt(row, 5);
                if ("Available".equals(status))         c.setForeground(ACCENT_GREEN);
                else if ("Rented".equals(status))       c.setForeground(ACCENT_AMBER);
                else if ("Under Maintenance".equals(status)) c.setForeground(ACCENT_RED);
                else c.setForeground(TEXT_MAIN);
                return c;
            }
        };
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(CARD_BG);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ══════════════════════════════════════════════════════════════
    //  NAVIGATION HANDLER
    // ══════════════════════════════════════════════════════════════
    private void handleNav(String section) {
        mainPanel.remove(currentView);
        JPanel newView;

        switch (section) {
            case "Maintenance":
                newView = new MaintenanceModule().buildPanel();
                break;
            case "All Scooties":
                newView = buildAllScootiesPanel();
                break;
            case "Run Tests":
                newView = buildTestPanel();
                break;
            case "System Logs":
                newView = buildLogsPanel();
                break;
            default:  // Dashboard
                newView = buildMainArea();
                refreshStats();
                break;
        }

        newView.setBorder(new EmptyBorder(24, 24, 24, 24));
        mainPanel.add(newView, BorderLayout.CENTER);
        currentView = newView;
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // ══════════════════════════════════════════════════════════════
    //  TEST PANEL
    // ══════════════════════════════════════════════════════════════
    private JPanel buildTestPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(BG_DARK);

        JLabel title = new JLabel("🧪 System Testing");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);
        panel.add(title, BorderLayout.NORTH);

        JTextArea results = new JTextArea();
        results.setFont(new Font("Consolas", Font.PLAIN, 13));
        results.setBackground(new Color(15, 23, 42));
        results.setForeground(ACCENT_GREEN);
        results.setEditable(false);
        JScrollPane scroll = new JScrollPane(results);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setBackground(BG_DARK);

        JButton btnAll    = styledButton("▶ Run All Tests",       ACCENT_BLUE);
        JButton btnDB     = styledButton("🔌 Test DB Connection",  ACCENT_GREEN);
        JButton btnScooty = styledButton("🛵 Test Scooty CRUD",    ACCENT_AMBER);
        JButton btnOverdue= styledButton("⚠️  Check Overdue",      ACCENT_RED);

        btnAll.addActionListener(e    -> SystemTester.runAllTests(results));
        btnDB.addActionListener(e     -> SystemTester.testDBConnection(results));
        btnScooty.addActionListener(e -> SystemTester.testScootyCRUD(results));
        btnOverdue.addActionListener(e-> MaintenanceService.checkOverdueAlerts(logArea));

        btnRow.add(btnAll); btnRow.add(btnDB);
        btnRow.add(btnScooty); btnRow.add(btnOverdue);
        panel.add(btnRow, BorderLayout.SOUTH);

        return panel;
    }

    // ── Logs Panel ──────────────────────────────────────────────
    private JPanel buildLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_DARK);

        JLabel title = new JLabel("📜 System Logs");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);
        panel.add(title, BorderLayout.NORTH);

        JTextArea logsArea = new JTextArea(SystemLogger.getLogs());
        logsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logsArea.setBackground(new Color(15, 23, 42));
        logsArea.setForeground(new Color(134, 239, 172));
        logsArea.setEditable(false);
        panel.add(new JScrollPane(logsArea), BorderLayout.CENTER);

        JButton clearBtn = styledButton("🗑 Clear Logs", ACCENT_RED);
        clearBtn.addActionListener(e -> {
            SystemLogger.clearLogs();
            logsArea.setText("");
        });
        panel.add(clearBtn, BorderLayout.SOUTH);

        return panel;
    }

    // ══════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════
    private void styleTable(JTable table) {
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_MAIN);
        table.setFont(FONT_LABEL);
        table.setRowHeight(28);
        table.setGridColor(new Color(51, 65, 85));
        table.getTableHeader().setBackground(new Color(30, 41, 59));
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionBackground(ACCENT_BLUE.darker());
        table.setSelectionForeground(Color.WHITE);
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private JTextField styledField() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_LABEL);
        tf.setBackground(new Color(15, 23, 42));
        tf.setForeground(TEXT_MAIN);
        tf.setCaretColor(TEXT_MAIN);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85)),
            new EmptyBorder(4, 8, 4, 8)));
        return tf;
    }

    private JButton styledButton(String text, Color accent) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(accent.darker());
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(accent); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(accent.darker()); }
        });
        return btn;
    }

    // ══════════════════════════════════════════════════════════════
    //  ENTRY POINT
    // ══════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Dashboard::new);
    }
}
