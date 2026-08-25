-- Optional sample data for Module 4 testing.
-- Run only once on an empty/temporary development database to avoid duplicates.
INSERT INTO harvest_batches
(farm_id, collection_point_id, quantity, harvest_time, temperature, humidity, risk_score, status)
VALUES
(1, 1, 120.50, NOW() - INTERVAL '5 hours', 29.0, 88.0, NULL, 'ready'),
(2, 1, 80.00,  NOW() - INTERVAL '2 hours', 24.0, 72.0, NULL, 'ready'),
(3, 2, 95.00,  NOW() - INTERVAL '1 hour',  23.0, 68.0, NULL, 'ready'),
(4, 2, 60.00,  NOW() - INTERVAL '8 hours', 31.0, 90.0, NULL, 'ready');
