-- AgriPulse Database Initialization & Seed Script (Phase 2)

-- 1. Create Tables
CREATE TABLE IF NOT EXISTS collection_points (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS roads (
    id BIGSERIAL PRIMARY KEY,
    from_point_id VARCHAR(50) NOT NULL,
    to_point_id VARCHAR(50) NOT NULL,
    distance DOUBLE PRECISION NOT NULL,
    incline DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    road_quality DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    monsoon_status BOOLEAN NOT NULL DEFAULT FALSE,
    is_open BOOLEAN NOT NULL DEFAULT TRUE,
    capacity_kg_per_day DOUBLE PRECISION NOT NULL DEFAULT 1000.0,
    CONSTRAINT fk_from_point FOREIGN KEY (from_point_id) REFERENCES collection_points(id),
    CONSTRAINT fk_to_point FOREIGN KEY (to_point_id) REFERENCES collection_points(id)
);

CREATE TABLE IF NOT EXISTS harvest_batches (
    id VARCHAR(50) PRIMARY KEY,
    collection_point_id VARCHAR(50) NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    priority_score DOUBLE PRECISION NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'READY',
    harvest_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_batch_cp FOREIGN KEY (collection_point_id) REFERENCES collection_points(id)
);

-- 2. Seed Collection Points (C1 to C6)
INSERT INTO collection_points (id, name, latitude, longitude) VALUES
('C1', 'Main Factory / Central Depot', 6.9271, 79.8612),
('C2', 'Valley Collection Point 2', 6.9320, 79.8700),
('C3', 'Hills Junction Point 3', 6.9400, 79.8800),
('C4', 'Highland Farm Point 4', 6.9500, 79.8900),
('C5', 'River Pass Point 5', 6.9600, 79.9000),
('C6', 'Mountain Peak Collection Point 6', 6.9700, 79.9100)
ON CONFLICT (id) DO NOTHING;

-- 3. Seed Roads
INSERT INTO roads (from_point_id, to_point_id, distance, incline, road_quality, monsoon_status, is_open, capacity_kg_per_day) VALUES
('C1', 'C2', 5.0, 1.0, 1.0, FALSE, TRUE, 2000.0),
('C2', 'C3', 8.0, 1.2, 1.1, TRUE,  TRUE, 1500.0),
('C3', 'C4', 6.0, 1.1, 1.0, FALSE, TRUE, 1800.0),
('C4', 'C6', 10.0, 1.5, 1.3, TRUE,  TRUE, 1200.0),
('C2', 'C5', 12.0, 1.3, 1.2, FALSE, TRUE, 1400.0),
('C5', 'C6', 7.0, 1.4, 1.1, FALSE, TRUE, 1600.0),
('C1', 'C3', 14.0, 1.2, 1.0, FALSE, TRUE, 2500.0);

-- 4. Seed Harvest Batches (B-102 & B-091)
INSERT INTO harvest_batches (id, collection_point_id, quantity, priority_score, status, harvest_time) VALUES
('B-102', 'C6', 500.0, 95.0, 'READY', CURRENT_TIMESTAMP),
('B-091', 'C4', 350.0, 75.0, 'READY', CURRENT_TIMESTAMP - INTERVAL '2 hours')
ON CONFLICT (id) DO NOTHING;
