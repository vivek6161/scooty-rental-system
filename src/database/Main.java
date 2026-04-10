package database;

import dao.CustomerDAO;
import dao.ScootyDAO;
import models.Customer;
import models.Scooty;
import java.util.*;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static ScootyDAO scootyDAO = new ScootyDAO();
    static CustomerDAO customerDAO = new CustomerDAO();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== SCOOTY RENTAL SYSTEM =====");
            System.out.println("1. Add Scooty");
            System.out.println("2. View All Scooties");
            System.out.println("3. Add Customer");
            System.out.println("4. View All Customers");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt(); sc.nextLine();

            switch (choice) {
                case 1 -> addScooty();
                case 2 -> viewScooties();
                case 3 -> addCustomer();
                case 4 -> viewCustomers();
                case 0 -> { System.out.println("Bye!"); return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void addScooty() {
        System.out.print("Model Name: "); String model = sc.nextLine();
        System.out.print("Brand: ");      String brand = sc.nextLine();
        System.out.print("Reg Number: "); String reg   = sc.nextLine();
        System.out.print("Daily Rate: "); double rate  = sc.nextDouble(); sc.nextLine();

        Scooty s = new Scooty(model, brand, reg, rate);
        if (scootyDAO.addScooty(s)) System.out.println("Scooty added successfully!");
        else System.out.println("Failed. Reg number may already exist.");
    }

    static void viewScooties() {
        List<Scooty> list = scootyDAO.getAllScooties();
        if (list.isEmpty()) { System.out.println("No scooties found."); return; }
        list.forEach(System.out::println);
    }

    static void addCustomer() {
        System.out.print("Name: ");           String name    = sc.nextLine();
        System.out.print("Phone: ");          String phone   = sc.nextLine();
        System.out.print("Email: ");          String email   = sc.nextLine();
        System.out.print("License Number: "); String license = sc.nextLine();

        Customer c = new Customer(name, phone, email, license);
        if (customerDAO.addCustomer(c)) System.out.println("Customer added! ID: " + c.getCustomerId());
        else System.out.println("Failed!");
    }

    static void viewCustomers() {
        List<Customer> list = customerDAO.getAllCustomers();
        if (list.isEmpty()) { System.out.println("No customers found."); return; }
        list.forEach(System.out::println);
    }
}