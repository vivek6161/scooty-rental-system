-- ═══════════════════════════════════════════════════════════════
--  Scooty Rental Management System — Database Setup Script
--  Member 2: Admin & Scooty Management Module
--  Run this in MySQL Workbench or terminal before running Java.
-- ═══════════════════════════════════════════════════════════════

-- Step 1: Create and select the database
CREATE DATABASE IF NOT EXISTS scooty_rental_db;
USE scooty_rental_db;

-- ─── Table: admins ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS admins (
    admin_id   INT          PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(50)  UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,          -- store hashed in production
    email      VARCHAR(100),
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ─── Table: scooties ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS scooties (
    scooty_id    INT          PRIMARY KEY AUTO_INCREMENT,
    model        VARCHAR(100) NOT NULL,
    brand        VARCHAR(100) NOT NULL,
    reg_number   VARCHAR(50)  UNIQUE NOT NULL,
    status       ENUM('available', 'booked', 'maintenance') DEFAULT 'available',
    price_per_hr DOUBLE       NOT NULL,
    added_date   DATE         DEFAULT (CURRENT_DATE)
);

-- ─── Sample admin account ──────────────────────────────────────
-- Username: admin  |  Password: admin123  (plain-text for testing)
INSERT INTO admins (username, password, email)
VALUES ('admin', 'admin123', 'admin@scooty.com')
ON DUPLICATE KEY UPDATE username = username;

-- ─── Sample scooty records ─────────────────────────────────────
INSERT INTO scooties (model, brand, reg_number, status, price_per_hr) VALUES
('Activa 6G',    'Honda',  'MH12AB1001', 'available',   50.00),
('Jupiter',      'TVS',    'MH12CD2002', 'available',   45.00),
('Dio',          'Honda',  'MH12EF3003', 'booked',      40.00),
('Access 125',   'Suzuki', 'MH12GH4004', 'maintenance', 55.00),
('NTorq 125',    'TVS',    'MH12IJ5005', 'available',   60.00);

-- ─── Verify ────────────────────────────────────────────────────
SELECT * FROM admins;
SELECT * FROM scooties;
