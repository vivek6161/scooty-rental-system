package com.scooty.service;

import com.scooty.dao.ScootyDAO;
import com.scooty.model.Scooty;

import java.util.List;

/**
 * ScootyService.java
 * Business logic layer — sits between AdminDashboard and ScootyDAO.
 * Handles: input validation, duplicate checks, status rules.
 * Never touches JDBC directly — delegates all DB work to ScootyDAO.
 * Member 2 – Admin & Scooty Management Module
 */
public class ScootyService {

    private final ScootyDAO dao;

    // Valid status values
    private static final String[] VALID_STATUSES = {"available", "booked", "maintenance"};

    public ScootyService() {
        this.dao = new ScootyDAO();
    }

    // ─── Register (Add) ───────────────────────────────────────────────────────

    /**
     * Validates fields and registers a new scooty.
     * Blocks: empty fields, duplicate reg numbers, negative price.
     *
     * @param model      Scooty model name
     * @param brand      Brand name
     * @param regNumber  Registration number (must be unique)
     * @param pricePerHr Hourly rental price (must be > 0)
     * @return true if scooty was successfully added
     */
    public boolean registerScooty(String model, String brand,
                                  String regNumber, double pricePerHr) {

        // 1. Validate fields
        if (!validateField(model,     "Model",               50)) return false;
        if (!validateField(brand,     "Brand",               50)) return false;
        if (!validateField(regNumber, "Registration Number", 20)) return false;

        if (pricePerHr <= 0) {
            System.out.println("[VALIDATION] Price per hour must be greater than 0.");
            return false;
        }

        // 2. Duplicate check
        if (isDuplicateId(regNumber)) {
            System.out.println("[VALIDATION] Registration number '" + regNumber + "' already exists.");
            return false;
        }

        // 3. Build object and persist
        Scooty s = new Scooty(model.trim(), brand.trim(),
                              regNumber.trim().toUpperCase(),
                              "available", pricePerHr);
        return dao.addScooty(s);
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    /**
     * Updates all details of an existing scooty after validation.
     *
     * @param scootyId   ID of the scooty to update
     * @param model      New model name
     * @param brand      New brand name
     * @param regNumber  New registration number
     * @param status     New status
     * @param pricePerHr New hourly price
     * @return true if updated successfully
     */
    public boolean updateScootyDetails(int scootyId, String model, String brand,
                                       String regNumber, String status, double pricePerHr) {

        if (!validateField(model,     "Model",               50)) return false;
        if (!validateField(brand,     "Brand",               50)) return false;
        if (!validateField(regNumber, "Registration Number", 20)) return false;
        if (!isValidStatus(status)) return false;

        if (pricePerHr <= 0) {
            System.out.println("[VALIDATION] Price per hour must be greater than 0.");
            return false;
        }

        // Check for duplicate reg number (ignoring the current scooty's own entry)
        Scooty existing = dao.getScootyById(scootyId);
        if (existing == null) {
            System.out.println("[ERROR] No scooty found with ID: " + scootyId);
            return false;
        }

        if (!existing.getRegNumber().equalsIgnoreCase(regNumber) && isDuplicateId(regNumber)) {
            System.out.println("[VALIDATION] Registration number '" + regNumber + "' is taken.");
            return false;
        }

        Scooty updated = new Scooty(model.trim(), brand.trim(),
                                    regNumber.trim().toUpperCase(),
                                    status, pricePerHr);
        updated.setScootyId(scootyId);
        return dao.updateScooty(updated);
    }

    // ─── Change Status ────────────────────────────────────────────────────────

    /**
     * Changes the availability status of a scooty.
     * Allowed values: "available", "booked", "maintenance"
     *
     * @param scootyId ID of the scooty
     * @param status   New status string
     * @return true if updated successfully
     */
    public boolean changeStatus(int scootyId, String status) {
        if (!isValidStatus(status)) return false;

        if (dao.getScootyById(scootyId) == null) {
            System.out.println("[ERROR] Scooty ID " + scootyId + " not found.");
            return false;
        }

        return dao.updateAvailability(scootyId, status);
    }

    // ─── Remove ───────────────────────────────────────────────────────────────

    /**
     * Removes a scooty from the system after confirming it exists.
     *
     * @param scootyId ID of the scooty to delete
     * @return true if deleted successfully
     */
    public boolean removeScooty(int scootyId) {
        if (dao.getScootyById(scootyId) == null) {
            System.out.println("[ERROR] Scooty ID " + scootyId + " not found. Cannot delete.");
            return false;
        }
        return dao.deleteScooty(scootyId);
    }

    // ─── View All ─────────────────────────────────────────────────────────────

    /**
     * Returns and prints all scooties.
     *
     * @return List of all Scooty objects
     */
    public List<Scooty> viewAllScooties() {
        dao.displayAllScooties();
        return dao.getAllScooties();
    }

    /**
     * Returns scooties filtered by status.
     *
     * @param status Filter value
     * @return Filtered list
     */
    public List<Scooty> viewScootiesByStatus(String status) {
        if (!isValidStatus(status)) return List.of();
        return dao.getScootiesByStatus(status);
    }

    // ─── Duplicate Check ─────────────────────────────────────────────────────

    /**
     * Checks if a registration number is already in use.
     *
     * @param regNumber Registration number to check
     * @return true if duplicate exists
     */
    public boolean isDuplicateId(String regNumber) {
        return dao.isDuplicateRegNumber(regNumber);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    /**
     * Validates a string field: not null, not blank, within max length.
     */
    private boolean validateField(String value, String fieldName, int maxLen) {
        if (value == null || value.trim().isEmpty()) {
            System.out.println("[VALIDATION] " + fieldName + " cannot be empty.");
            return false;
        }
        if (value.trim().length() > maxLen) {
            System.out.println("[VALIDATION] " + fieldName + " must be under " + maxLen + " characters.");
            return false;
        }
        return true;
    }

    /**
     * Validates that a status string is one of the allowed values.
     */
    private boolean isValidStatus(String status) {
        if (status == null) {
            System.out.println("[VALIDATION] Status cannot be null.");
            return false;
        }
        for (String valid : VALID_STATUSES) {
            if (valid.equalsIgnoreCase(status.trim())) return true;
        }
        System.out.println("[VALIDATION] Invalid status '" + status +
                           "'. Allowed: available, booked, maintenance.");
        return false;
    }
}
