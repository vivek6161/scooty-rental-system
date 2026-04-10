public class Scooty {
    private int scootyId;
    private String modelName;
    private String brand;
    private String regNumber;
    private double dailyRate;
    private String status;

    public Scooty(String modelName, String brand, String regNumber, double dailyRate) {
        this.modelName = modelName;
        this.brand = brand;
        this.regNumber = regNumber;
        this.dailyRate = dailyRate;
        this.status = "Available";
    }

    public int getScootyId() { return scootyId; }
    public void setScootyId(int id) { this.scootyId = id; }
    public String getModelName() { return modelName; }
    public String getBrand() { return brand; }
    public String getRegNumber() { return regNumber; }
    public double getDailyRate() { return dailyRate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "[" + scootyId + "] " + brand + " " + modelName +
               " | Reg: " + regNumber + " | Rs." + dailyRate + "/day | " + status;
    }
}
