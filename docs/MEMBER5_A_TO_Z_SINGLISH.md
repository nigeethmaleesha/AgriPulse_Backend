# Member 5 A-Z Guide (Singlish)

## Oyata assign wela thiyenne mokakda?

Oyata thiyenne **Module 3 - Member 5: Capacity Graph & Ford-Fulkerson Engine**.
Main idea eka: `SOURCE -> FARMS -> COLLECTION HUBS -> FACTORY` directed capacity graph ekak hadala, tea kg/day maximum kochchara factory ekata yanna puluwanda kiyala Ford-Fulkerson walin calculate karana eka.

## Project eka run karanna one dewal

1. Java 17+ install karanna.
2. IntelliJ IDEA install karanna.
3. PostgreSQL + pgAdmin install karanna, naththam Docker Desktop use karanna.
4. Maven available wenna one. IntelliJ Maven project ekak widihata `pom.xml` open kalama dependencies download wenawa.
5. Postman install karanna API test karanna.

## PostgreSQL easiest setup

Docker thiyenawanam project root eke:

```bash
docker compose up -d
```

Database details:

- database: `agripulse`
- username: `postgres`
- password: `postgres`
- port: `5432`

Docker nathi nam pgAdmin walin `agripulse` database ekak create karala `application.yml` / environment variables update karanna.

## Backend run karana order

1. PostgreSQL start karanna.
2. IntelliJ eken project open karanna.
3. Maven dependencies load wenna denna.
4. `AgriPulseApplication` run karanna.
5. Backend: `http://localhost:8080`
6. First run eke sample `SOURCE/FARM/HUB/FACTORY` graph eka auto-seed wenawa.

## Main API test

POST `http://localhost:8080/api/network/max-flow`

```json
{
  "sourceCode": "SOURCE",
  "sinkCode": "FACTORY",
  "saveBenchmark": true
}
```

Demo dataset eke expected maximum flow = **1350 kg/day**.

## Member 5 code eka balanna one main file

`src/main/java/com/agripulse/backend/service/network/FordFulkersonService.java`

Me file eke:

- directed capacity matrix build karanawa,
- residual matrix copy karanawa,
- stack/DFS augmenting path search karanawa,
- path bottleneck capacity hoyanawa,
- forward residual capacity adu karanawa,
- reverse residual capacity wadi karanawa,
- max flow ekata path flow add karanawa,
- path nathi wenakan repeat karanawa.

## PostgreSQL tables

- `supply_nodes` - SOURCE / FARM / HUB / FACTORY nodes.
- `capacity_edges` - directed links saha `capacity_kg_per_day`.
- `algorithm_test_results` - runtime, estimated algorithm memory, maximum flow evidence.

## Benchmark plan

Coursework plan ekata match wena presets:

- Small: 20 nodes / 30 edges
- Medium: 100 nodes / 250 edges
- Large: 500 nodes / 2000 edges

GET `/api/network/benchmark/presets`

POST `/api/network/benchmark`

Example:

```json
{
  "nodeCount": 20,
  "edgeCount": 30,
  "seed": 42,
  "minCapacityKgPerDay": 100,
  "maxCapacityKgPerDay": 2000,
  "saveResult": true
}
```

Same seed use karoth repeatable synthetic dataset ekak labenawa. Final report eke planning-document example results copy karanna epa; oyage real run results use karanna.

## Member 6 koheda code karanne?

`docs/MEMBER6_HANDOFF.md` balanna. Partner `BottleneckService` create karala oyage `FordFulkersonService` reuse karanna one. Eya database graph eka temporary copy ekak widihata modify karala reduced/closed/upgraded capacity scenarios rerun karanna one.
