# AgriPulse — Fertilizer Allocation Module (Task 2A)

## Overview

This module is part of the **AgriPulse Intelligent Decision Support System**, developed for
the PDSA coursework. It solves the fertilizer allocation problem for tea smallholder farms:
given a limited supply of fertilizer bags and multiple farm requests, the system decides which
requests to approve so that total agricultural benefit is maximized without exceeding
available stock.

| | |
|---|---|
| **Module** | Task 2A — Fertilizer Allocation |
| **Primary algorithm** | 0/1 Knapsack (Dynamic Programming) |
| **Author / role** | Member 3 |
| **Branch** | `danuja_dev` |

---

## Problem Definition

Farms submit fertilizer requests, but total demand typically exceeds available supply. Each
request is treated as **indivisible** — a farm either receives its full requested amount or
none at all, since partial delivery does not meaningfully serve a farm's actual seasonal need.
This constraint maps the problem directly onto the classic **0/1 Knapsack** formulation:

| Knapsack concept | System equivalent |
|---|---|
| Knapsack capacity | Total fertilizer bags available |
| Item | A single fertilizer request |
| Item weight | Requested bag count |
| Item value | Benefit / priority score |
| Include or exclude (no splitting) | Request approved or rejected as a whole |

**Inputs:** A list of pending fertilizer requests (each with a requested bag count and benefit
score), and a total fertilizer capacity for the allocation round.

**Outputs:** The subset of requests selected for allocation, the subset rejected, total benefit
achieved, capacity utilization, and demand fulfillment percentage.

**Constraints:** Requested bags and capacity are non-negative integers; each request is
all-or-nothing; capacity is fixed for a single allocation round.

**Assumptions:** One allocation round is evaluated at a time (no rolling/continuous
allocation); benefit score is treated as a pre-computed input rather than derived within this
module.

---

## Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot
- **Database:** PostgreSQL 17.1 (via pgAdmin 4)
- **Persistence:** Spring Data JPA / Hibernate
- **Build tool:** Maven
- **API testing:** Postman

---

## Package Structure

```
com.agripulse
│
├── controller/            REST endpoints (Farm, FertilizerRequest, Allocation, Benchmark)
├── service/
│   └── allocation/         Algorithm implementations (DP + comparison baselines)
├── repository/             Spring Data JPA repositories
├── model/                   Entity classes (Farm, FertilizerRequest)
├── dto/                      Request/response data transfer objects
├── exception/               Custom exceptions
└── util/                     Synthetic data generator (reproducible test datasets)
```

---

## Core Entities

### `Farm`

| Field | Description |
|---|---|
| `id` | Primary key |
| `farmName` | Display name of the farm |
| `contactNumber` | **Unique identifier** for the farm (names alone can repeat) |
| `region` | Farm's district/region |
| `cropType` | e.g. Tea, Rubber, Coconut |
| `landSize` | Land size in acres |
| `registeredAt` | Registration timestamp |

### `FertilizerRequest`

| Field | Description |
|---|---|
| `id` | Primary key |
| `farm` | `@ManyToOne` reference to the requesting `Farm` |
| `fertilizerType` | e.g. Urea, NPK, Compost, TSP, MOP |
| `requestedBags` | Bags requested — knapsack **weight** |
| `benefitScore` | Priority/need score — knapsack **value** |
| `urgencyLevel` | LOW / MEDIUM / HIGH |
| `status` | PENDING / ALLOCATED / REJECTED |
| `allocatedBags` | Bags actually granted after allocation runs |
| `requestDate`, `createdAt` | Timestamps for auditing |

A `Farm` can have many `FertilizerRequest`s, avoiding duplicated farm data across multiple
requests from the same farm.

---

## Algorithms Implemented

Per the coursework requirement to investigate **3+ candidate algorithms** for the assigned
problem:

### 1. 0/1 Knapsack (Dynamic Programming) — **Production algorithm**

Builds a 2D DP table `dp[i][w]` representing the maximum achievable benefit using the first
`i` requests within capacity `w`, then backtracks through the table to identify exactly which
requests were selected.

- **Time complexity:** O(n × W), where n = number of requests, W = total capacity
- **Space complexity:** O(n × W)
- **Optimality:** Guarantees the exact optimal solution given the indivisibility constraint
- **Used by:** `POST /api/fertilizer/allocate` (persists results to the database)

### 2. Fractional Knapsack (Greedy) — Comparison baseline

Sorts requests by benefit-to-weight ratio and greedily fills capacity, allowing partial
allocation of the last item that doesn't fully fit. Included only for academic comparison —
**not used for real allocation**, since splitting a farm's request does not reflect a
realistic, usable outcome for that farm.

- **Time complexity:** O(n log n)
- **Optimality:** Optimal only if item splitting were valid
- **Used by:** `POST /api/fertilizer/allocate/fractional` (evaluation only, not persisted)

### 3. Greedy Priority Allocation — Comparison baseline

Sorts requests by benefit score alone and greedily takes whole requests until capacity is
exhausted, skipping any that don't fit. Simple and fast, but does not guarantee an optimal
combination — a single high-value, high-weight request can block a better combination of
smaller requests.

- **Time complexity:** O(n log n)
- **Optimality:** Not guaranteed
- **Used by:** `POST /api/fertilizer/allocate/greedy` (evaluation only, not persisted)

---

## API Endpoints

### Farm management

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/farms` | Register a new farm |
| `GET` | `/api/farms` | List all registered farms |
| `GET` | `/api/farms/{id}` | Get a single farm by ID |

### Fertilizer requests

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/fertilizer/requests` | Submit a new fertilizer request (requires `farmId`) |
| `GET` | `/api/fertilizer/requests` | List all requests |
| `GET` | `/api/fertilizer/requests/pending` | List only PENDING requests |

### Allocation

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/fertilizer/allocate` | Run 0/1 Knapsack DP allocation (production, persists results) |
| `POST` | `/api/fertilizer/allocate/fractional` | Run Fractional Knapsack baseline (evaluation only) |
| `POST` | `/api/fertilizer/allocate/greedy` | Run Greedy Priority baseline (evaluation only) |
| `GET` | `/api/fertilizer/benchmark` | Run all 3 algorithms across 20 / 200 / 2,000 synthetic requests |

---

## Example Usage

### 1. Register a farm

```
POST /api/farms
Content-Type: application/json

{
  "farmName": "Farm-A",
  "contactNumber": "0710000301",
  "region": "Kandy",
  "cropType": "Tea",
  "landSize": 3.5
}
```

### 2. Submit a fertilizer request

```
POST /api/fertilizer/requests
Content-Type: application/json

{
  "farmId": 1,
  "fertilizerType": "Urea",
  "requestedBags": 20,
  "benefitScore": 60.0,
  "urgencyLevel": "MEDIUM"
}
```

### 3. Run allocation

```
POST /api/fertilizer/allocate
Content-Type: application/json

{
  "totalCapacity": 50
}
```

**Response**

```json
{
  "totalCapacity": 50,
  "usedCapacity": 50,
  "totalRequestedBags": 85,
  "totalAllocatedBags": 50,
  "totalRequestsCount": 4,
  "allocatedRequestsCount": 2,
  "rejectedRequestsCount": 2,
  "totalBenefitAchieved": 160.0,
  "capacityUtilizationPercent": 100.0,
  "demandFulfillmentPercent": 58.82,
  "allocatedRequests": [ "..." ],
  "rejectedRequests": [ "..." ],
  "executionTimeMillis": 0
}
```

---

## Synthetic Data Generation

`FertilizerDataGenerator` produces reproducible `Farm` + `FertilizerRequest` test datasets
using a **fixed random seed (`42L`)**, so results can be regenerated exactly for benchmarking
or independent verification. Supports dataset sizes of **20, 200, and 2,000** requests,
matching the coursework's experimental testing plan for Task 2.

```java
List<FertilizerRequest> data = FertilizerDataGenerator.generate(200);
```

---

## Experimental Results

Benchmarked at 20 / 200 / 2,000 synthetic requests, with capacity fixed at **40% of total
requested bags** for each dataset size (so the allocation problem stays meaningfully
constrained as it scales).

| Dataset Size | Algorithm | Benefit Achieved | Capacity Used | Requests Allocated | Time (ms) |
|---|---|---|---|---|---|
| 20 | 0/1 Knapsack (DP) | 762.41 | 99.57% | 11/20 | 0 |
| 20 | Fractional Knapsack | 775.27 | 100% | 12/20 | 2 |
| 20 | Greedy Priority | 742.92 | 99.57% | 9/20 | 1 |
| 200 | 0/1 Knapsack (DP) | 7,647.26 | 100% | 110/200 | 10 |
| 200 | Fractional Knapsack | 7,649.37 | 100% | 111/200 | 1 |
| 200 | Greedy Priority | 6,966.07 | 99.95% | 87/200 | 0 |
| 2,000 | 0/1 Knapsack (DP) | 73,614.46 | 100% | 1091/2000 | 282 |
| 2,000 | Fractional Knapsack | 73,614.81 | 100% | 1091/2000 | 4 |
| 2,000 | Greedy Priority | 64,954.70 | 100% | 808/2000 | 2 |

### Key findings

- **0/1 Knapsack is near-optimal:** it achieves total benefit within ~0.0005% of the
  theoretical fractional optimum at 2,000 requests, while remaining realistic (no split
  requests).
- **0/1 Knapsack clearly outperforms naive greedy:** roughly a 13% higher total benefit than
  Greedy Priority Allocation at scale (73,614 vs 64,954 at 2,000 requests), and allocates more
  requests overall (1091 vs 808).
- **Runtime cost of exactness:** DP's execution time grows from 0ms → 10ms → 282ms across the
  three dataset sizes, consistent with its O(n × W) pseudo-polynomial complexity, since both
  request count and capacity scaled together. Both greedy baselines stayed in the single-digit
  milliseconds throughout, reflecting their O(n log n) complexity.

---

## Known Limitations

- **Capacity underutilization:** 0/1 Knapsack can leave stock unused when no combination of
  whole requests exactly fills remaining capacity — unlike Fractional Knapsack, which can
  always use 100% of capacity by splitting the last item.
- **Computational cost at scale:** the 2D DP table's O(n × W) time and space cost grows
  significantly as both request count and capacity increase, which may become impractical for
  very large capacities.
- **Single allocation round:** the current design evaluates one fixed capacity/request batch
  at a time; it does not yet support rolling or continuously updating allocation.

---

## Testing Performed

- **Correctness verification:** DP table construction and backtracking traced and verified by
  hand on multiple small (3–4 item) datasets, confirming the algorithm selects the true optimal
  combination rather than just the highest-value individual items.
- **API testing:** all endpoints tested via Postman, including edge cases:
  - Zero total capacity
  - A single request exceeding total capacity
  - Multiple requests with identical weights but different benefit scores
- **Benchmark testing:** automated comparison of all three algorithms across 20 / 200 / 2,000
  synthetic requests via `/api/fertilizer/benchmark`.

---

## Future Work

- Add Bean Validation (`@Valid`) and a global `@ControllerAdvice` for cleaner API error
  responses.
- Add automated JUnit test coverage for edge cases currently verified manually.
- Extend `Farm` with `soilHealth` if required by shared entity design across other modules.
- Integrate with Task 2B (Irrigation Pump Allocation, Member 4) if resource coordination
  between fertilizer and pump allocation is required at the system level.