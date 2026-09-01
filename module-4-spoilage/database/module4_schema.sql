-- AgriPulse Module 4 shared schema: Member 7 + Member 8
-- Connect pgAdmin to database: agripulse_module4_spoilage, then run this script if you
-- prefer manual table creation. If you use spring.jpa.hibernate.ddl-auto=update,
-- Hibernate can create/update these tables automatically after the database exists.

CREATE TABLE IF NOT EXISTS harvest_batches (
    id                  BIGSERIAL PRIMARY KEY,
    farm_id             BIGINT,
    collection_point_id BIGINT,
    quantity            NUMERIC(10,2) NOT NULL,
    harvest_time        TIMESTAMP NOT NULL,
    temperature         NUMERIC(5,2) NOT NULL,
    humidity            NUMERIC(5,2) NOT NULL,
    risk_score          NUMERIC(10,4),
    status              VARCHAR(20) NOT NULL DEFAULT 'ready'
);

CREATE TABLE IF NOT EXISTS algorithm_test_results (
    id                  BIGSERIAL PRIMARY KEY,
    module              VARCHAR(50) NOT NULL,
    algorithm           VARCHAR(50) NOT NULL,
    input_size          INTEGER NOT NULL,
    execution_time_ms   DOUBLE PRECISION NOT NULL,
    memory_mb           DOUBLE PRECISION,
    solution_metric     VARCHAR(255),
    run_at              TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_harvest_batches_status
    ON harvest_batches(status);

CREATE INDEX IF NOT EXISTS idx_harvest_batches_risk_score
    ON harvest_batches(risk_score DESC);

CREATE INDEX IF NOT EXISTS idx_algorithm_results_module
    ON algorithm_test_results(module);
