# AgriPulse — Irrigation Pump Allocation Module: Introduction & How It Works

This document explains, in plain language, what this module does, why it was built this way, and how data flows through the system from a farmer's request to a final allocation decision. It's meant as a companion to the README (technical reference) and the benchmarking endpoints.

---

## 1. The Real-World Problem

During dry seasons or unexpected droughts, smallholder tea farms urgently need water to keep their crops alive. The regional agricultural body has a supply of motorized irrigation pumps to lend out, but there is a **limited inventory**. There are always more farms desperate for water than there are pumps available.

Someone has to decide: **which farms get a pump, and which are left waiting?**

If we just hand them out "first come, first served," a farm with only mild dryness might take a pump away from a farm whose entire crop is 24 hours away from dying. This module automates the decision, using an algorithm to guarantee that the farms in the most critical condition always get the equipment first.

---

## 2. Why This Is a Max-Heap (Priority Queue) Problem

Imagine an Emergency Room at a hospital. When patients walk in, they aren't treated in the exact order they arrived. Instead, a triage nurse assesses how critical they are. A patient with a paper cut who arrived at 8:00 AM will wait if a patient with a heart attack arrives at 9:00 AM. The hospital effectively maintains a "Priority Queue," where the most severe cases automatically float to the very front of the line.

This irrigation pump allocation problem is exactly the same shape:

| Hospital Triage | Pump Allocation |
|---|---|
| Available Doctors | Total motorized pumps available |
| A waiting patient | A single farm's pump request |
| Severity of illness | Farm's priority/need score |
| Treat the patient immediately | Allocate a pump to the farm |

Because we need to constantly pluck the "highest priority" farm from the crowd until we run out of pumps, this problem is perfectly solved by a **Max-Heap** (a data structure that automatically keeps the highest value at the top).

---

## 3. What the System Actually Does, Step by Step

### Step 1 — Farms register
Before a farm can request a pump, it is registered in the system with its unique contact number, region, and land size. (This is shared data across the whole AgriPulse system).

### Step 2 — Farms submit pump requests
A registered farm is evaluated for a pump. The request includes:
- An **eligibility status** (e.g., they don't already have a broken pump they haven't returned).
- A **priority score** — a calculated number representing how critical their water need is (higher = more desperate for a pump).

### Step 3 — An allocation round is run
When the coordinator is ready to distribute a batch of pumps (say, 10 pumps), they trigger the allocation algorithm. The system then:
1. Filters out any farms that are not eligible.
2. Loads all eligible farms into the Max-Heap.
3. Automatically "pops" the highest-scoring farms off the top of the heap, one by one, handing them a pump until the inventory hits zero.

### Step 4 — Results are returned
The system reports back exactly which farms were allocated a pump and how fast the algorithm made the decision.

---

## 4. How the Algorithm Actually Decides — In Plain Terms

The brilliant thing about a Max-Heap is that it **does not waste time sorting the whole line**.

If you have 2,000 farms asking for a pump, but you only have 10 pumps to give out, you only care about finding the top 10 most desperate farms. A Max-Heap is like a self-organizing pyramid. The farm with the absolute highest score is pushed to the very peak. Once you hand them a pump and remove them, the pyramid instantly shifts to put the *next* highest farm at the peak. It never bothers to perfectly sort the 1,990 farms at the bottom who aren't getting a pump anyway!

### A worked example
Say we only have **2 pumps**, but 5 farms want one:

| Farm | Priority Score |
|---|---|
| Farm-A | 45.0 |
| Farm-B | 92.0 |
| Farm-C | 15.0 |
| Farm-D | 88.0 |
| Farm-E | 99.0 |

The Max-Heap grabs them and instantly puts **Farm-E (99.0)** at the peak.
1. We give Pump #1 to Farm-E.
2. The heap shifts, and **Farm-B (92.0)** floats to the peak.
3. We give Pump #2 to Farm-B.
4. We are out of pumps. The algorithm stops instantly. It never wasted a single millisecond figuring out if Farm-A or Farm-C was worse off, because neither of them was getting a pump regardless.

---

## 5. Why Two Other Algorithms Are Also in the System

To prove that the Max-Heap is the best tool for the job, two alternative algorithms are included **only for comparison and evaluation**:

- **Full Sort (TimSort / Merge Sort):** This approach takes all 2,000 farms and perfectly sorts them from highest score to lowest score before handing out the 10 pumps. While it makes the exact same (correct) decisions as the Max-Heap, it is highly inefficient because it wastes computer memory and time perfectly organizing the 1,990 farms at the bottom of the list.

- **Greedy / First-Come First-Served:** This is a naive approach that just looks down the list of farms and hands out pumps to the first eligible ones it sees until it runs out. It is lightning-fast, but yields terrible agricultural outcomes—it completely ignores the priority score, meaning desperately dry farms get ignored just because they were at the bottom of the spreadsheet.

---

## 6. Why Synthetic Data Is Used for Testing

Real farm priority data isn't available for this coursework, so the system includes a **synthetic data generator** that creates fake farms and priority scores. This lets the system be tested at small scale (20 requests), medium scale (200), and large scale (2,000) to observe how the execution time changes as the problem grows.

---

## 7. Where This Fits in the Bigger AgriPulse System

This module is a critical early step in the Intelligent Decision Support System for tea smallholder supply chains. Irrigation pump allocation happens during the growing season. By ensuring water reaches the most critical farms, this module ensures that a crop survives to become a "harvest batch" (which feeds into the later modules like Spoilage-Risk Ranking and Urgent Collection Dispatch).

---

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

## 8. Summary

In short: when a limited number of water pumps become available, this module uses a highly efficient Priority Queue (Max-Heap) to guarantee that the life-saving equipment goes instantly to the farms with the most critical need, without wasting computer processing power sorting farms that won't receive equipment anyway.
