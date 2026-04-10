package models;

public class Customer {
    private int customerId;
    private String name;
    private String phone;
    private String email;
    private String licenseNumber;

    public Customer(String name, String phone, String email, String licenseNumber) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.licenseNumber = licenseNumber;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int id) { this.customerId = id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getLicenseNumber() { return licenseNumber; }

    @Override
    public String toString() {
        return "[" + customerId + "] " + name + " | Phone: " + phone;
    }
}