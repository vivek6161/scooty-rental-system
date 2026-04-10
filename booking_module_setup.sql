-- ═══════════════════════════════════════════════════════════════
-- Scooty Rental Management System — Booking & Rental Module
-- Member 3: Booking & Rental Module
-- Run AFTER database_setup.sql (which creates admins & scooties).
-- ═══════════════════════════════════════════════════════════════

USE scooty_rental;

-- ─── Table: customers ──────────────────────────────────────────
-- Stores all registered customers.
CREATE TABLE IF NOT EXISTS customers (
    customer_id     INT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(100)  NOT NULL,
    phone           VARCHAR(15)   UNIQUE NOT NULL,        -- used as unique identifier
    email           VARCHAR(100),
    license_number  VARCHAR(50)   NOT NULL,
    registered_at   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

-- ─── Table: bookings ───────────────────────────────────────────
-- Stores every rental booking from start to close.
CREATE TABLE IF NOT EXISTS bookings (
    booking_id      INT PRIMARY KEY AUTO_INCREMENT,
    customer_id     INT           NOT NULL,
    scooty_id       INT           NOT NULL,
    start_time      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time        TIMESTAMP     NULL,                   -- null while rental is active
    total_hours     DOUBLE        DEFAULT 0.0,            -- calculated on close
    total_amount    DOUBLE        DEFAULT 0.0,            -- calculated on close
    status          ENUM('active','closed','cancelled') DEFAULT 'active',

    -- Foreign key constraints keep data consistent
    CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    CONSTRAINT fk_booking_scooty   FOREIGN KEY (scooty_id)   REFERENCES scooties(scooty_id)
);

-- ─── Sample customers ──────────────────────────────────────────
INSERT INTO customers (name, phone, email, license_number) VALUES
('Rahul Sharma',   '9876543210', 'rahul@email.com',  'DL-0420110012345'),
('Priya Verma',    '9823456781', 'priya@email.com',  'MH-1220190023456'),
('Amit Patel',     '9812345678', 'amit@email.com',   'GJ-0120180034567')
ON DUPLICATE KEY UPDATE name = name;

-- ─── Useful views for reporting ────────────────────────────────

-- View: active rentals with customer and scooty details joined
CREATE OR REPLACE VIEW active_rentals AS
SELECT
    b.booking_id,
    c.name          AS customer_name,
    c.phone         AS customer_phone,
    s.brand,
    s.model,
    s.reg_number,
    s.price_per_hr,
    b.start_time,
    TIMESTAMPDIFF(MINUTE, b.start_time, NOW()) AS minutes_elapsed
FROM bookings b
JOIN customers c ON b.customer_id = c.customer_id
JOIN scooties  s ON b.scooty_id   = s.scooty_id
WHERE b.status = 'active';

-- View: rental history with full details
CREATE OR REPLACE VIEW rental_history AS
SELECT
    b.booking_id,
    c.name          AS customer_name,
    c.phone,
    s.brand,
    s.model,
    s.reg_number,
    b.start_time,
    b.end_time,
    b.total_hours,
    b.total_amount,
    b.status
FROM bookings b
JOIN customers c ON b.customer_id = c.customer_id
JOIN scooties  s ON b.scooty_id   = s.scooty_id
ORDER BY b.start_time DESC;

-- ─── Verify ────────────────────────────────────────────────────
SELECT * FROM customers;
SELECT * FROM active_rentals;
SELECT * FROM rental_history;
