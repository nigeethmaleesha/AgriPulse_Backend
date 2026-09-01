# AgriPulse Backend - PDSA2 Module 3 / Members 5-6

> Integrated runtime: port `8080`, database `agripulse_module3_network`, PostgreSQL user `postgres`, password `1234`. Start the shared PostgreSQL service from the project-root `docker-compose.yml`.

This repository contains the backend implementation for **Module 3: Tea Supply Network Capacity & Bottleneck Analysis - Member 5: Capacity Graph & Ford-Fulkerson Engine**.

## Stack

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA / Hibernate
- PostgreSQL
- JUnit
- Postman

## Member 5 scope implemented

- Directed `SOURCE -> FARM -> HUB -> FACTORY` capacity graph stored in PostgreSQL.
- Ford-Fulkerson Maximum Flow.
- Residual-capacity matrix.
- Stack/DFS augmenting-path search.
- Maximum tea throughput in kg/day.
- Per-edge flow, residual capacity and utilization returned for reuse by Member 6.
- Baseline runtime and matrix-memory estimate.
- Synthetic benchmark generator for 20/30, 100/250, and 500/2000 Task 3 sizes.
- Persistent `algorithm_test_results` evidence.
- CRUD endpoints for network nodes and capacity edges.
- Demo graph seeded automatically on an empty database.

## Quick start

### Option A - PostgreSQL with Docker

```bash
docker compose up -d
```

Then run `AgriPulseApplication` from IntelliJ, or:

```bash
mvn spring-boot:run
```

### Option B - Local PostgreSQL

Create database `agripulse` and use these defaults, or override them with environment variables:

```text
DB_HOST=localhost
DB_PORT=5432
DB_NAME=agripulse
DB_USER=postgres
DB_PASSWORD=postgres
```

## Important endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/network/graph` | Get nodes + capacity edges |
| GET/POST | `/api/network/nodes` | List/create nodes |
| PUT/DELETE | `/api/network/nodes/{id}` | Update/delete node |
| GET/POST | `/api/network/edges` | List/create capacity edges |
| PUT/DELETE | `/api/network/edges/{id}` | Update/delete edge |
| POST | `/api/network/max-flow` | Run Member 5 Ford-Fulkerson |
| GET | `/api/network/max-flow/results` | Get saved benchmark runs |
| GET | `/api/network/benchmark/presets` | Coursework Task 3 test sizes |
| POST | `/api/network/benchmark` | Generate synthetic graph and benchmark |

## Run the demo max-flow

```http
POST /api/network/max-flow
Content-Type: application/json

{
  "sourceCode": "SOURCE",
  "sinkCode": "FACTORY",
  "saveBenchmark": true
}
```

Expected maximum flow for the seeded demo graph: **1350 kg/day**.

## Member 6 integration

See `docs/MEMBER6_HANDOFF.md`. Member 6 should create `BottleneckService` and reuse the Member 5 `FordFulkersonService` without rewriting the max-flow engine.

## Singlish guide

See `docs/MEMBER5_A_TO_Z_SINGLISH.md`.
