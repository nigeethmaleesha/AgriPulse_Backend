-- Optional manual PostgreSQL schema for learning/inspection.
-- The application can also create/update these tables automatically through Hibernate (ddl-auto=update).

CREATE TABLE IF NOT EXISTS supply_nodes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    node_type VARCHAR(20) NOT NULL CHECK (node_type IN ('SOURCE', 'FARM', 'HUB', 'FACTORY')),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS capacity_edges (
    id BIGSERIAL PRIMARY KEY,
    from_node_id BIGINT NOT NULL REFERENCES supply_nodes(id),
    to_node_id BIGINT NOT NULL REFERENCES supply_nodes(id),
    capacity_kg_per_day BIGINT NOT NULL CHECK (capacity_kg_per_day > 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    label VARCHAR(160),
    CONSTRAINT uk_capacity_edge_pair UNIQUE (from_node_id, to_node_id),
    CONSTRAINT chk_no_self_edge CHECK (from_node_id <> to_node_id)
);

CREATE TABLE IF NOT EXISTS algorithm_test_results (
    id BIGSERIAL PRIMARY KEY,
    module VARCHAR(60) NOT NULL,
    algorithm VARCHAR(80) NOT NULL,
    input_size INTEGER NOT NULL,
    edge_count INTEGER NOT NULL,
    execution_time_ms DOUBLE PRECISION NOT NULL,
    memory_mb DOUBLE PRECISION NOT NULL,
    solution_metric BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL
);
