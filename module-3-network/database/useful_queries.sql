-- 1) View all network nodes
SELECT * FROM supply_nodes ORDER BY id;

-- 2) View capacity edges with readable node codes
SELECT e.id,
       f.code AS from_code,
       t.code AS to_code,
       e.capacity_kg_per_day,
       e.active,
       e.label
FROM capacity_edges e
JOIN supply_nodes f ON f.id = e.from_node_id
JOIN supply_nodes t ON t.id = e.to_node_id
ORDER BY e.id;

-- 3) View latest Member 5 benchmark evidence
SELECT *
FROM algorithm_test_results
WHERE module = 'Task 3A - Capacity Graph & Max Flow'
ORDER BY created_at DESC
LIMIT 100;
