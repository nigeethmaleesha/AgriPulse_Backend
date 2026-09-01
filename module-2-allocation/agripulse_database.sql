-- =========================================================================
-- AgriPulse — Fertilizer Allocation Module (Task 2A)
-- Database Schema + Sample Data
-- PostgreSQL 17.1
-- =========================================================================
--
-- HOW TO USE:
-- 1. Create the database first (if not already created):
--      CREATE DATABASE agripulse_module2_allocation;
-- 2. Connect to it in pgAdmin's Query Tool (right-click "agripulse_module2_allocation" -> Query Tool)
-- 3. Run this entire script.
--
-- NOTE: Spring Boot (spring.jpa.hibernate.ddl-auto=update) will normally
-- create these tables automatically when the app starts. This script is
-- provided so the schema can also be created/inspected manually, and so
-- sample data is available for quick manual testing without Postman.
-- =========================================================================

-- -------------------------------------------------------------------------
-- Clean slate (optional — uncomment if you want to reset everything)
-- -------------------------------------------------------------------------
-- DROP TABLE IF EXISTS fertilizer_request;
-- DROP TABLE IF EXISTS farm;

-- -------------------------------------------------------------------------
-- Table: farm
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS farm (
    id              BIGSERIAL PRIMARY KEY,
    farm_name       VARCHAR(255) NOT NULL,
    contact_number  VARCHAR(255) NOT NULL UNIQUE,
    region          VARCHAR(255) NOT NULL,
    crop_type       VARCHAR(255),
    land_size       DOUBLE PRECISION,
    registered_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------------------
-- Table: fertilizer_request
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS fertilizer_request (
    id                BIGSERIAL PRIMARY KEY,
    farm_id           BIGINT NOT NULL REFERENCES farm(id),
    fertilizer_type   VARCHAR(255) NOT NULL,
    requested_bags    INTEGER NOT NULL,
    benefit_score     DOUBLE PRECISION NOT NULL,
    urgency_level     VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    status            VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    allocated_bags    INTEGER NOT NULL DEFAULT 0,
    request_date      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Helpful indexes (status is queried frequently by the allocation service)
CREATE INDEX IF NOT EXISTS idx_fertilizer_request_status ON fertilizer_request(status);
CREATE INDEX IF NOT EXISTS idx_fertilizer_request_farm_id ON fertilizer_request(farm_id);

-- -------------------------------------------------------------------------
-- Sample data — 8 farms across 5 regions
-- -------------------------------------------------------------------------
INSERT INTO farm (farm_name, contact_number, region, crop_type, land_size) VALUES
('Farm-A', '0710000301', 'Kandy',        'Tea',     3.5),
('Farm-B', '0710000302', 'Matara',       'Tea',     5.0),
('Farm-C', '0710000303', 'Ratnapura',    'Rubber',  2.0),
('Farm-D', '0710000304', 'Badulla',      'Tea',     4.2),
('Farm-E', '0710000305', 'Nuwara Eliya', 'Tea',     6.1),
('Farm-F', '0710000306', 'Kandy',        'Coconut', 1.8),
('Farm-G', '0710000307', 'Matara',       'Tea',     3.0),
('Farm-H', '0710000308', 'Ratnapura',    'Rubber',  2.7);

-- -------------------------------------------------------------------------
-- Sample data — fertilizer requests (all PENDING, ready for allocation testing)
-- -------------------------------------------------------------------------
INSERT INTO fertilizer_request (farm_id, fertilizer_type, requested_bags, benefit_score, urgency_level, status) VALUES
(1, 'Urea',    20, 60.0, 'MEDIUM', 'PENDING'),  -- Farm-A
(2, 'NPK',     30, 100.0,'HIGH',   'PENDING'),  -- Farm-B
(3, 'Compost', 10, 30.0, 'LOW',    'PENDING'),  -- Farm-C
(4, 'TSP',     25, 70.0, 'MEDIUM', 'PENDING'),  -- Farm-D
(5, 'Urea',    15, 45.0, 'HIGH',   'PENDING'),  -- Farm-E
(6, 'MOP',     12, 25.0, 'LOW',    'PENDING'),  -- Farm-F
(7, 'NPK',     18, 55.0, 'MEDIUM', 'PENDING'),  -- Farm-G
(8, 'Compost', 22, 65.0, 'HIGH',   'PENDING');  -- Farm-H

-- -------------------------------------------------------------------------
-- Useful verification queries (run manually as needed)
-- -------------------------------------------------------------------------

-- View all requests with farm details joined in
-- SELECT fr.id, f.farm_name, f.region, fr.fertilizer_type, fr.requested_bags,
--        fr.benefit_score, fr.urgency_level, fr.status, fr.allocated_bags
-- FROM fertilizer_request fr
-- JOIN farm f ON fr.farm_id = f.id
-- ORDER BY fr.id;

-- Reset all requests back to PENDING (useful before re-testing allocation)
-- UPDATE fertilizer_request SET status = 'PENDING', allocated_bags = 0;

-- Check total requested bags vs a hypothetical capacity
-- SELECT SUM(requested_bags) AS total_requested_bags FROM fertilizer_request WHERE status = 'PENDING';

-- View allocation outcome after running POST /api/fertilizer/allocate
-- SELECT f.farm_name, fr.requested_bags, fr.allocated_bags, fr.status, fr.benefit_score
-- FROM fertilizer_request fr
-- JOIN farm f ON fr.farm_id = f.id
-- ORDER BY fr.status, fr.benefit_score DESC;
