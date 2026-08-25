# AgriPulse Module 4 - Member 8 A-to-Z Run Guide (Singlish)

## 1. Oyage exact scope
Member 8 = **Real-Time Priority Queue for Urgent Batches**.
Member 7 risk score calculate/rank karanawa. Member 8 e same `harvest_batches` rows use karala custom **Max-Heap** ekak maintain karanawa.

## 2. Member 7 + Member 8 relationship
- Shared source of truth: PostgreSQL `harvest_batches` table.
- Member 7 owns `RiskRankingService` and `risk_score` formula.
- Member 8 reuses that service; formula duplicate karanne naha.
- Member 8 heap eka in-memory. Separate priority-queue table ekak naha.
- Restart/reload unama heap eka `ready` database batches walin rebuild karanawa.

## 3. Conflict avoid karana setup
Earlier Module 3 project port 8080 / database `agripulse` use karanna puluwan.
Me Module 4 project default:
- database: `agripulse_module4`
- port: `8081`

Final team integration ekedi env variables walin same shared DB/port walata change karanna puluwan.

## 4. PostgreSQL database create karanna
pgAdmin -> postgres database -> Query Tool:

```sql
CREATE DATABASE agripulse_module4;
```

Database eka create unama tables Hibernate eken auto-create wenawa (`ddl-auto=update`). Manual schema one nam `database/module4_schema.sql` run karanna.

## 5. IntelliJ environment variables
Run -> Edit Configurations -> SpoilageRiskRankingApplication -> Environment variables:

```text
DB_HOST=localhost;DB_PORT=5432;DB_NAME=agripulse_module4;DB_USER=postgres;DB_PASSWORD=YOUR_REAL_PASSWORD;SERVER_PORT=8081
```

Real password eka Git ekata commit karanna epa.

## 6. IntelliJ JDK / Maven
- Project SDK: Java 21 is fine (project compiles to Java 17 bytecode).
- Maven Runner JRE: Project SDK / Java 21.
- Maven panel -> Reload All Maven Projects.
- Lifecycle -> clean -> BUILD SUCCESS.
- Lifecycle -> install -> BUILD SUCCESS.

## 7. Run
Run `SpoilageRiskRankingApplication`.
Expected:

```text
Tomcat started on port 8081
Started SpoilageRiskRankingApplication
```

## 8. First Postman flow
Base URL: `http://localhost:8081`

### A. Add a Member 7 shared batch
POST `/api/spoilage/batches`

```json
{
  "farmId": 1,
  "collectionPointId": 1,
  "quantity": 120.5,
  "harvestTime": "2026-08-25T00:00:00",
  "temperature": 29.0,
  "humidity": 88.0
}
```

### B. Run Member 7 ranking once
GET `/api/spoilage/ranking?method=merge`
This calculates and persists `riskScore`.

### C. Build Member 8 max-heap from the SAME rows
POST `/api/spoilage/priority/reload`

### D. Read highest-risk batch
GET `/api/spoilage/priority/top`

### E. Add a new live incoming batch directly through Member 8
POST `/api/spoilage/priority/batches`

```json
{
  "farmId": 5,
  "collectionPointId": 3,
  "quantity": 105.0,
  "harvestTime": "2026-08-24T20:00:00",
  "temperature": 32.0,
  "humidity": 92.0
}
```

It is saved to PostgreSQL, scored using Member 7's formula, and inserted into the heap in O(log n).

### F. Check top again
GET `/api/spoilage/priority/top`

### G. View heap / full priority order
GET `/api/spoilage/priority/heap`
GET `/api/spoilage/priority/ordered`

### H. Pop current top from live queue
POST `/api/spoilage/priority/pop`
Note: this removes it from the live heap only; database status is not changed.

## 9. Benchmark
Presets:
GET `/api/spoilage/priority/benchmark/presets`

Small first:
POST `/api/spoilage/priority/benchmark?sizes=100&incomingOperations=50`

Then:
POST `/api/spoilage/priority/benchmark?sizes=10000&incomingOperations=50`

Then large only after smaller tests are stable:
POST `/api/spoilage/priority/benchmark?sizes=100000&incomingOperations=50`

Results:
GET `/api/spoilage/priority/benchmark/results`

Algorithms compared:
- `max_heap`
- `timsort_full_resort`
- `insertion_order`

## 10. Complexity for viva
- Max-heap build: O(n)
- Insert: O(log n)
- Peek highest risk: O(1)
- Pop highest risk: O(log n)
- Update known persisted batch: O(log n) using id->heap-index map
- Space: O(n)

## 11. Member 7 code preservation
Member 7 Java algorithm/controller/model files are retained. Member 8 is added as new controller/service/data-structure files. Shared configuration was made environment-based so no database password is hard-coded and Module 4 can run without colliding with Module 3.
