AgriPulse --- Irrigation Pump Allocation Module (Task 2B)
=======================================================

Overview
--------

This module is part of the **AgriPulse Intelligent Decision Support System**, developed for the PDSA coursework.

It solves the irrigation pump allocation problem for tea smallholder farms. Given a strictly limited inventory of motorized water pumps and multiple farm requests, the system determines which eligible farms should receive a pump so that the most critical agricultural needs are met first.

|  |                                        |
| --- |----------------------------------------|
| **Module** | Task 2B --- Irrigation Pump Allocation |
| **Primary Algorithm** | Max-Heap (Priority Queue)              |
| **Author / Role** | Member 4                               |
| **Branch** | `sasindu`                              |

* * * * *

Problem Definition
------------------

Farms submit requests for motorized water pumps during dry conditions, but total demand typically exceeds the available pump inventory.

Each request is treated as **indivisible** --- a farm either receives one pump or does not receive one.

Because the system must always prioritize the most critical eligible farm, the problem maps directly to a **Priority Queue (Max-Heap)** formulation.

| Max-Heap Concept | System Equivalent |
| --- | --- |
| Heap capacity | Dynamic; stores all eligible requesting farms |
| Item / Node | A single farm's pump request |
| Item value / Key | Farm's priority / need score |
| Extract-Max operation | Allocating one pump to the most critical farm |
| Stop condition | Available pump inventory reaches zero |

### Inputs

-   A list of pending pump requests.

-   An eligibility flag for each request.

-   A pre-calculated priority score for each request.

-   The total inventory of available pumps.

### Outputs

-   Farms allocated a pump.

-   Farms rejected because of insufficient inventory.

-   Execution time.

-   Algorithm used.

### Constraints

-   Available pumps must be non-negative integers.

-   Each pump allocation is all-or-nothing.

-   Only eligible farms can receive a pump.

### Assumptions

-   One allocation round is evaluated at a time.

-   Priority scores are calculated before requests enter the heap.

* * * * *

Tech Stack
----------

| Technology | Version / Purpose |
| --- | --- |
| **Language** | Java 21 |
| **Framework** | Spring Boot |
| **Database** | PostgreSQL 17.1 |
| **Database Administration** | pgAdmin 4 |
| **Persistence** | Spring Data JPA / Hibernate |
| **Build Tool** | Maven |
| **API Testing** | Postman / Web Browser |

* * * * *

Package Structure
-----------------

```
com.agripulse
│
├── controller/
│   └── PumpController
│       REST endpoints
│
├── service/
│   └── allocation/
│       Algorithm implementations
│       ├── Max-Heap
│       ├── Full Sort
│       └── Greedy / FCFS
│
├── repository/
│   Spring Data JPA repositories
│
├── model/
│   Entity classes
│   ├── Farm
│   └── PumpRequest
│
├── dto/
│   Request / response data transfer objects
│   └── PumpAllocationResultDto
│
├── exception/
│   Custom exceptions
│
└── util/
    Synthetic data generator
    Reproducible benchmark datasets

```

* * * * *

Core Entities
-------------

### `Farm`

| Field | Description |
| --- | --- |
| `id` | Primary key |
| `farmName` | Display name of the farm |
| `contactNumber` | **Unique identifier** for the farm; names alone can repeat |
| `region` | Farm's district / region |
| `cropType` | Crop type, e.g. Tea, Rubber, Coconut |
| `landSize` | Land size in acres |
| `registeredAt` | Registration timestamp |

### `PumpRequest`

| Field | Description |
| --- | --- |
| `id` | Primary key |
| `farm` | `@ManyToOne` reference to the requesting `Farm` |
| `priorityScore` | Calculated need score; higher values indicate greater urgency |
| `isEligible` | Boolean indicating whether the farm is eligible |
| `status` | `PENDING`, `ALLOCATED`, or `REJECTED` |
| `requestDate` | Date the pump was requested |
| `createdAt` | Record creation timestamp |

A single `Farm` can have multiple historical `PumpRequest` records. This avoids duplicating farm information across the system.

* * * * *

Algorithms Implemented
----------------------

As required by the coursework, **three candidate algorithms** were investigated and implemented for comparison.

### 1\. Max-Heap Priority Allocation --- Production Algorithm

The primary allocation algorithm loads all eligible farm requests into a Java `PriorityQueue` configured as a **Max-Heap** based on priority score.

The algorithm repeatedly removes the highest-priority request until the available pump inventory is exhausted.

**Time Complexity:** `O(N + K log N)`

Where:

-   `N` = number of eligible requests

-   `K` = number of available pumps

**Space Complexity:** `O(N)`

**Optimality:** Guarantees that pumps are allocated to the highest-priority eligible farms without sorting the entire dataset.

**Used by:** `/api/pumps/benchmark`

* * * * *

### 2\. Full Sort --- Comparison Baseline

The algorithm filters eligible farms into a list and uses Java's built-in `Collections.sort()` to sort the entire list in descending order of priority.

Pumps are then allocated to the highest-priority farms.

**Time Complexity:** `O(N log N)`

**Space Complexity:** `O(N)`

**Optimality:** Produces the same optimal allocation as the Max-Heap, but performs unnecessary sorting on farms that cannot receive a pump because the inventory is limited.

**Used by:** `/api/pumps/benchmark`

* * * * *

### 3\. Simple Greedy / First-Come First-Served --- Comparison Baseline

The algorithm iterates through the request list sequentially.

Whenever it encounters an eligible farm, it assigns a pump until the available inventory is exhausted.

Ineligible farms are skipped.

**Time Complexity:** `O(N)`

**Space Complexity:** `O(1)` additional space

**Optimality:** Not guaranteed. The algorithm ignores priority scores and may allocate pumps to less-critical farms before more-critical farms.

**Used by:** `/api/pumps/benchmark`

* * * * *

Algorithm Comparison
--------------------

| Algorithm | Time Complexity | Priority Aware | Optimal Allocation | Purpose |
| --- | --: | :-: | :-: | --- |
| **Max-Heap** | `O(N + K log N)` | Yes | Yes | Production |
| **Full Sort** | `O(N log N)` | Yes | Yes | Comparison |
| **Greedy / FCFS** | `O(N)` | No | No | Comparison |

Where:

-   `N` = number of eligible requests

-   `K` = number of available pumps

* * * * *

API Endpoints
-------------

### Pump Allocation & Benchmarking

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/pumps/benchmark` | Runs all three algorithms against synthetic data and compares their execution performance. |

### Query Parameters

| Parameter | Description | Example |
| --- | --- | --- |
| `numberOfFarms` | Number of synthetic farm requests | `2000` |
| `availablePumps` | Number of pumps available for allocation | `50` |

* * * * *

Example Usage
-------------

### Run Allocation Benchmark

```
GET /api/pumps/benchmark?numberOfFarms=2000&availablePumps=50

```

### Example Response

```
[
  {
    "algorithmUsed": "Max-Heap (Primary)",
    "totalPumpsAvailable": 50,
    "pumpsAllocated": 50,
    "executionTimeMillis": 2,
    "allocatedFarms": [
      {
        "farmId": 1432,
        "priorityScore": 99.8,
        "eligible": true
      }
    ]
  },
  {
    "algorithmUsed": "Full Sort (Comparison)",
    "totalPumpsAvailable": 50,
    "pumpsAllocated": 50,
    "executionTimeMillis": 8,
    "allocatedFarms": [
      ...
    ]
  }
]

```

* * * * *

Synthetic Data Generation
-------------------------

The `PumpController` uses a built-in synthetic data generator:

```
generateDummyRequests(int count)

```

The generator uses a fixed random seed:

```
42L

```

Using a fixed seed ensures that benchmark datasets can be reproduced consistently for:

-   Performance testing

-   Algorithm comparison

-   Coursework evaluation

-   Independent verification

The benchmark supports the following dataset sizes:

-   **20 requests**

-   **200 requests**

-   **2,000 requests**

These sizes correspond to the experimental testing plan for Task 2.

* * * * *

Experimental Results
--------------------

## Experimental Results
## Experimental Results

Benchmarked at 20 / 200 / 2,000 synthetic requests, with available pump inventory fixed at **40% of total requesting farms** for each dataset size (so resource competition remains meaningfully constrained as it scales).

| Dataset Size | Algorithm | Total Priority Score | Pumps Allocated | Time (ms) |
|---|---|---|---|---|
| 20 | Max-Heap (Priority Queue) | 625.8 | 8 / 20 | 2 |
| 20 | Full Sort (TimSort) | 625.8 | 8 / 20 | 0 |
| 20 | Greedy / FCFS | 421.1 | 8 / 20 | 0 |
| 200 | Max-Heap (Priority Queue) | 6,397.8 | 80 / 200 | 0 |
| 200 | Full Sort (TimSort) | 6,397.8 | 80 / 200 | 0 |
| 200 | Greedy / FCFS | 4,233.2 | 80 / 200 | 0 |
| 2,000 | Max-Heap (Priority Queue) | 63,770.0 | 800 / 2,000 | 4 |
| 2,000 | Full Sort (TimSort) | 63,770.0 | 800 / 2,000 | 7 |
| 2,000 | Greedy / FCFS | 41,763.6 | 800 / 2,000 | 0 |

### Key findings
- **Max-Heap is the most efficient targeted approach:** By only extracting the top *K* items, the Max-Heap avoids the `O(N log N)` overhead of a full sort. This becomes highly noticeable when N (total farms) is very large, but K (available pumps) is limited (4ms vs 7ms at 2,000 requests).
- **Full Sort does unnecessary work:** While TimSort produces the exact same optimal allocation as the Max-Heap, its execution time grows faster because it wastes CPU cycles sorting the lowest-priority farms perfectly, which provides zero business value since they do not get a pump.
- **Greedy is fast but ineffective:** The O(N) Greedy baseline executes instantly, but completely fails the business objective of aiding the most at-risk farms (achieving a score of only ~41k compared to ~63k at scale), rendering it useless for actual production.

Known Limitations
-----------------

### 1\. Single Allocation Round

The current implementation evaluates one fixed request batch and pump capacity at a time.

It does not currently support continuous or rolling allocation as pumps are returned to inventory.

### 2\. Tied Priority Scores

When multiple farms have exactly the same priority score, the current heap implementation does not guarantee a deterministic secondary ordering.

A future implementation could introduce secondary tie-breaking criteria such as:

-   Request waiting time

-   Request date

-   Farm risk level

-   Previous allocation history

* * * * *

Testing Performed
-----------------

### Correctness Testing

Verified that the **Max-Heap** and **Full Sort** algorithms identify the same highest-priority eligible farms for allocation.

### API Testing

The benchmark endpoint was tested using both **Postman** and a web browser.

The following edge cases were tested:

-   Zero available pumps

-   More available pumps than eligible farms

-   More available pumps than total farms

-   Large request lists

-   20,000+ synthetic farm requests

### Benchmark Testing

All three algorithms were automatically compared using:

-   20 requests

-   200 requests

-   2,000 requests

The benchmark was executed through:

```
GET /api/pumps/benchmark

```

* * * * *

Future Work
-----------

### Database Persistence

Persist pump allocation results to PostgreSQL by linking `PumpRequest` records to the central `Farm` entity using Spring Data JPA.

### Automated Unit Testing

Add comprehensive JUnit test coverage for:

-   Zero pump inventory

-   Empty request lists

-   All requests being ineligible

-   More pumps than requests

-   Equal priority scores

-   Large datasets

-   Correct allocation ordering

### Improved Tie-Breaking

Introduce deterministic secondary priority rules for farms with identical priority scores.

### System Integration

Integrate Task 2B with **Task 2A --- Fertilizer Allocation** to create a unified resource allocation dashboard for field coordinators.

* * * * *

Conclusion
----------

The experimental results demonstrate that the **Max-Heap Priority Queue** is the most suitable algorithm for the AgriPulse irrigation pump allocation problem.

It provides:

-   Priority-based allocation

-   Optimal selection of eligible farms

-   Better scalability than full sorting when pump inventory is limited

-   Efficient extraction of the highest-priority requests

-   A clear mapping between the agricultural allocation problem and a standard data structure

The **Greedy / FCFS** approach provides the lowest computational overhead but does not satisfy the core business requirement of prioritizing the most critical farms.

The **Full Sort** approach provides correct priority-based allocation but performs unnecessary sorting when only a small number of pumps are available.

Therefore, the **Max-Heap implementation is selected as the production algorithm for Task 2B**.