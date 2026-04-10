
import java.util.Scanner;

/**
 * AdminDashboard.java
 * Main entry point for the Admin & Scooty Management Module.
 * Handles the console menu, reads user input, and calls ScootyService.
 * Member 2 – Admin & Scooty Management Module
 *
 * Flow:
 *   main() → login screen → showMenu() → handleInput() → ScootyService → ScootyDAO → DB
 */
public class AdminDashboard {

    private static final Admin         admin   = new Admin();
    private static final ScootyService service = new ScootyService();
    private static final Scanner       sc      = new Scanner(System.in);

    // ─── Entry Point ──────────────────────────────────────────────────────────

    public static void main(String[] args) {
        printBanner();

        // Step 1: Login gate
        boolean loggedIn = false;
        int attempts = 0;

        while (!loggedIn && attempts < 3) {
            System.out.print("Username : ");
            String username = sc.nextLine().trim();

            System.out.print("Password : ");
            String password = sc.nextLine().trim();

            loggedIn = admin.login(username, password);
            if (!loggedIn) {
                attempts++;
                System.out.println("Attempts remaining: " + (3 - attempts) + "\n");
            }
        }

        if (!loggedIn) {
            System.out.println("[LOCKED] Too many failed attempts. Exiting.");
            return;
        }

        // Step 2: Show dashboard loop
        boolean running = true;
        while (running) {
            showMenu();
            int choice = readInt("Enter choice: ");
            running = handleInput(choice);
        }

        admin.logout();
        sc.close();
        System.out.println("Goodbye!");
    }

    // ─── Menu Display ─────────────────────────────────────────────────────────

    /**
     * Prints the main dashboard menu to the console.
     */
    public static void showMenu() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║     SCOOTY MANAGEMENT SYSTEM     ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  1. View All Scooties            ║");
        System.out.println("║  2. Add New Scooty               ║");
        System.out.println("║  3. Update Scooty Details        ║");
        System.out.println("║  4. Change Availability Status   ║");
        System.out.println("║  5. Delete Scooty                ║");
        System.out.println("║  6. Filter by Status             ║");
        System.out.println("║  7. Logout                       ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    // ─── Input Handler ────────────────────────────────────────────────────────

    /**
     * Handles the user's menu choice.
     * Returns false when the user wants to exit.
     *
     * @param choice Integer menu choice
     * @return true to keep the loop running, false to exit
     */
    public static boolean handleInput(int choice) {
        System.out.println();

        switch (choice) {

            case 1: // ── View all ──────────────────────────────────────────────
                service.viewAllScooties();
                break;

            case 2: // ── Add scooty ────────────────────────────────────────────
            System.out.print("Model        : "); String model  = sc.nextLine();
            System.out.print("Brand        : "); String brand  = sc.nextLine();
            System.out.print("Reg Number   : "); String regNum = sc.nextLine();
            double price = readDouble("Price per Hr : ");
            sc.nextLine(); // ← add this line
            boolean added = service.registerScooty(model, brand, regNum, price);
            if (added) System.out.println("[OK] Scooty registered successfully.");
            break;

            case 3: // ── Update scooty ─────────────────────────────────────────
    int updateId = readInt("Scooty ID to update : ");
    sc.nextLine(); // ← add this
    System.out.print("New Model       : "); String nModel  = sc.nextLine();
    System.out.print("New Brand       : "); String nBrand  = sc.nextLine();
    System.out.print("New Reg Number  : "); String nReg    = sc.nextLine();
    System.out.print("New Status      : "); String nStatus = sc.nextLine();
    double nPrice = readDouble("New Price/Hr    : ");
    sc.nextLine(); // ← add this
    service.updateScootyDetails(updateId, nModel, nBrand, nReg, nStatus, nPrice);
    break;

case 4: // ── Change status ─────────────────────────────────────────
    int statusId = readInt("Scooty ID : ");
    sc.nextLine(); // ← add this
    System.out.println("Statuses  : available | booked | maintenance");
    System.out.print("New Status: ");
    String newStatus = sc.nextLine().trim();
    service.changeStatus(statusId, newStatus);
    break;

case 5: // ── Delete scooty ─────────────────────────────────────────
    int delId = readInt("Scooty ID to delete : ");
    sc.nextLine(); // ← add this
    System.out.print("Confirm delete? (yes/no): ");
    String confirm = sc.nextLine().trim();
    if (confirm.equalsIgnoreCase("yes")) {
        service.removeScooty(delId);
    } else {
        System.out.println("[CANCELLED] Delete aborted.");
    }
    break;

case 6: // ── Filter by status ──────────────────────────────────────
    System.out.println("Filter by: available | booked | maintenance");
    System.out.print("Status    : ");
    String filterStatus = sc.nextLine().trim();
    service.viewScootiesByStatus(filterStatus);
    break;

            case 7: // ── Logout ────────────────────────────────────────────────
                return false;

            default:
                System.out.println("[INFO] Invalid choice. Enter a number from 1 to 7.");
        }

        return true; // keep menu running
    }

    // ─── Input Helpers ────────────────────────────────────────────────────────

    /**
     * Reads a validated integer from the console.
     */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("[INPUT] Please enter a valid whole number.");
            }
        }
    }

    /**
     * Reads a validated double from the console.
     */
    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("[INPUT] Please enter a valid number (e.g. 50.0).");
            }
        }
    }

    // ─── Banner ───────────────────────────────────────────────────────────────

    private static void printBanner() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     SCOOTY RENTAL MANAGEMENT SYSTEM      ║");
        System.out.println("║         Admin Login — Member 2           ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();
    }
}
