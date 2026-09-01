# AgriPulse Module 4 - Member 7 + Member 8 Complete Backend

This project preserves the existing Member 7 spoilage-risk ranking implementation and adds Member 8 real-time urgent-batch priority using a custom max-heap.

## Integration contract

`harvest_batches` is shared. Member 7 calculates/persists `riskScore`; Member 8 consumes the same `HarvestBatch` entity and reuses `RiskRankingService` so there is one risk formula and no duplicated decision logic.

The live heap is intentionally in memory. PostgreSQL remains the source of truth; `/api/spoilage/priority/reload` rebuilds the heap from `status='ready'` rows.

## Member 8 endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/spoilage/priority/reload` | Rebuild heap from ready DB batches |
| GET | `/api/spoilage/priority/top` | O(1) current highest-risk batch |
| POST | `/api/spoilage/priority/pop` | O(log n) remove current top from live heap |
| POST | `/api/spoilage/priority/batches` | Persist + score + live-insert incoming batch |
| POST | `/api/spoilage/priority/enqueue/{id}` | Insert/update existing ready DB batch |
| PUT | `/api/spoilage/priority/refresh/{id}` | Re-score and reposition existing batch |
| GET | `/api/spoilage/priority/heap` | Internal heap-array view |
| GET | `/api/spoilage/priority/ordered` | Full priority-order copy for demonstration |
| GET | `/api/spoilage/priority/status` | Queue size/top summary |
| DELETE | `/api/spoilage/priority/clear` | Clear live heap only |
| GET | `/api/spoilage/priority/benchmark/presets` | 100 / 10,000 / 100,000 Task 4 sizes |
| POST | `/api/spoilage/priority/benchmark` | Compare max-heap vs TimSort vs insertion order |
| GET | `/api/spoilage/priority/benchmark/results` | Saved Member 8 benchmark evidence |

See `docs/MEMBER8_A_TO_Z_SINGLISH.md` for setup, database, IntelliJ and Postman steps.
