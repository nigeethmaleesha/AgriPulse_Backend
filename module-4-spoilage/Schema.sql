-- ============================================================
-- AgriPulse - Task 4 (Spoilage Risk) - Database Schema
-- Run this in pgAdmin, DBeaver, or psql against your PostgreSQL
-- database before starting the Spring Boot app.
-- ============================================================

-- This table is SHARED between Member 7 (sorting/ranking) and
-- Member 8 (live priority queue). Both read/write the same rows.
CREATE TABLE IF NOT EXISTS harvest_batches (
    id                  BIGSERIAL PRIMARY KEY,
    farm_id             BIGINT,
    collection_point_id BIGINT,
    quantity            NUMERIC(10,2)   NOT NULL,
    harvest_time        TIMESTAMP       NOT NULL,
    temperature         NUMERIC(5,2)    NOT NULL,   -- degrees Celsius
    humidity            NUMERIC(5,2)    NOT NULL,   -- percentage 0-100
    risk_score          NUMERIC(6,2),               -- calculated, can start NULL
    status              VARCHAR(20)     NOT NULL DEFAULT 'ready'  -- ready | collected
);

-- This table stores every benchmark run so you have real,
-- measured evidence for your report (not made-up numbers).
CREATE TABLE IF NOT EXISTS algorithm_test_results (
    id                  BIGSERIAL PRIMARY KEY,
    module              VARCHAR(50)     NOT NULL,   -- e.g. 'spoilage_ranking'
    algorithm           VARCHAR(50)     NOT NULL,   -- 'bubble' | 'insertion' | 'merge'
    input_size          INTEGER         NOT NULL,
    execution_time_ms   NUMERIC(12,4)   NOT NULL,
    memory_mb           NUMERIC(12,4),
    solution_metric     VARCHAR(255),               -- optional notes/result summary
    run_at              TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- A couple of sample rows so you can test the ranking endpoint
-- immediately without writing insert statements yourself.
INSERT INTO harvest_batches (farm_id, collection_point_id, quantity, harvest_time, temperature, humidity, status)
VALUES
    (1, 1, 120.5, NOW() - INTERVAL '5 hours', 29.0, 88.0, 'ready'),
    (2, 1, 80.0,  NOW() - INTERVAL '2 hours', 24.0, 72.0, 'ready'),
    (3, 2, 95.0,  NOW() - INTERVAL '1 hour',  23.0, 68.0, 'ready'),
    (4, 2, 60.0,  NOW() - INTERVAL '8 hours', 31.0, 90.0, 'ready');