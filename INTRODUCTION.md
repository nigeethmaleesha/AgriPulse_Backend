# AgriPulse — Fertilizer Allocation Module: Introduction & How It Works

This document explains, in plain language, what this module does, why it was built this way,
and how data flows through the system from a farmer's request to a final allocation decision.
It's meant as a companion to the README (technical reference) and the Postman collection
(testable examples).

---

## 1. The Real-World Problem

Smallholder tea farms regularly need fertilizer to keep their land productive, but the
government or coordinating body distributing fertilizer only has a **limited number of bags**
available at any given time. More farms request fertilizer than can actually be supplied.

Someone has to decide: **which farms get their fertilizer, and which don't?**

Doing this fairly and effectively — not just "first come, first served," and not just "give it
to whoever asks loudest" — is what this module automates. It uses an algorithm to make the
mathematically best decision given the constraints, rather than a manual or arbitrary process.

---

## 2. Why This Is a Knapsack Problem

Imagine a farmer packing a suitcase (a "knapsack") with limited space. They have several
items they could bring, each with a weight and a value to them, and they want to bring the
combination of items that gives them the most value without exceeding what they can carry.

This fertilizer allocation problem is exactly the same shape:

| Suitcase-packing | Fertilizer allocation |
|---|---|
| Suitcase space limit | Total fertilizer bags available |
| An item you could pack | A single farm's fertilizer request |
| Item's weight | Bags that farm requested |
| Item's value | How much that request matters (benefit score) |
| Pack it or leave it — no cutting items in half | Approve the request fully, or reject it entirely |

Because a farm's request is either fully approved or not (you can't meaningfully give someone
"63% of a fertilizer delivery"), this is specifically the **0/1 Knapsack** version of the
problem — "0/1" meaning each item is either included (1) or excluded (0), never partial.

---

## 3. What the System Actually Does, Step by Step

### Step 1 — Farms register

Before a farm can request fertilizer, it's registered once in the system with its name, a
unique contact number (used as its real identifier, since farm names can repeat), region, crop
type, and land size. This is a one-time setup per farm — you don't re-enter farm details every
time they make a request.

### Step 2 — Farms submit fertilizer requests

A registered farm submits a request specifying:
- How many bags it needs (`requestedBags`)
- How urgent the need is (`urgencyLevel`)
- What type of fertilizer (`fertilizerType`)
- A **benefit score** — a number representing how much value/priority this request carries
  (higher = more important to fulfill)

Every new request starts with status `PENDING` — it hasn't been decided yet.

### Step 3 — An allocation round is run

When the resource coordinator has a fixed amount of fertilizer to distribute (say, 500 bags),
they trigger an allocation round by specifying that total capacity. The system then:

1. Pulls in every `PENDING` request.
2. Runs the 0/1 Knapsack algorithm to work out the exact best combination of requests to
   approve — the combination that uses up available bags while maximizing total benefit.
3. Marks each request as either `ALLOCATED` (approved, full amount) or `REJECTED` (not this
   round).
4. Saves these decisions back to the database.

### Step 4 — Results are returned

The system reports back exactly which farms got fertilizer, which didn't, how many bags were
used out of the total available, and how much total "benefit" was achieved — giving the
coordinator (or, in a real deployment, a dashboard) a clear picture of the outcome.

---

## 4. How the Algorithm Actually Decides — In Plain Terms

The system doesn't just approve the highest-value requests one by one — that would be too
simplistic and can lead to a worse outcome overall (a big request might block two smaller ones
that together are worth more). Instead, it works out, mathematically, the single best
*combination* of requests that fits.

It does this by building a table of "what's the best I could achieve with these first few
requests, at this amount of capacity?" — filling that table in gradually, request by request,
capacity level by capacity level — and then, once the table is complete, tracing back through
it to figure out exactly which requests made up that best answer.

This is more computational work than a simple sort-and-pick approach, but it guarantees the
mathematically optimal outcome given the rule that requests can't be split.

### A worked example

Say there are 4 requests and 50 bags of capacity:

| Farm | Requested bags | Benefit score |
|---|---|---|
| Farm-A | 20 | 60 |
| Farm-B | 30 | 100 |
| Farm-C | 10 | 30 |
| Farm-D | 25 | 70 |

At first glance, Farm-D (70) looks more valuable than Farm-A (60). But the algorithm checks
every valid combination and finds that **Farm-A + Farm-B together** (20+30 = 50 bags, exactly
fitting) gives a combined benefit of 160 — higher than any combination involving Farm-D, which
would force something else out to make room. So the system correctly picks Farm-A and Farm-B,
using all 50 bags, and rejects Farm-C and Farm-D — even though Farm-D alone looked more
"valuable" than Farm-A alone.

This is the core strength of the approach: it reasons about combinations, not just individual
scores.

---

## 5. Why Two Other Algorithms Are Also in the System

The coursework this module was built for requires comparing the chosen algorithm against
alternatives, to justify why it's the right one for this problem. Two additional algorithms
are included **only for comparison and evaluation** — they are not used to make real
allocation decisions:

- **Fractional Knapsack** — a faster method that *would* allow splitting a request (e.g.
  giving a farm 17 out of 25 requested bags) to use up 100% of available capacity. It's
  included to show what's theoretically possible if partial delivery were acceptable — and to
  demonstrate that the 0/1 Knapsack's "all or nothing" result comes very close to this
  theoretical best anyway, without the unrealistic partial delivery.

- **Greedy Priority Allocation** — an even simpler method that just approves requests in order
  of benefit score until capacity runs out, without checking combinations at all. It's
  included to show that this "obvious" simpler approach genuinely performs worse — it wastes
  capacity and achieves less total benefit than the 0/1 Knapsack, especially as the number of
  requests grows.

Real measured results (from running all three on datasets of 20, 200, and 2,000 synthetic
requests) confirmed this: 0/1 Knapsack came within a fraction of a percent of Fractional
Knapsack's theoretical best, while clearly outperforming Greedy Priority Allocation by a wide
margin at larger scales.

---

## 6. Why Synthetic Data Is Used for Testing

Real fertilizer request data isn't available for this coursework, so the system includes a
**synthetic data generator** that creates realistic-looking fake farms and requests — random
but repeatable, using a fixed "seed" so the exact same test data can be regenerated at any
time. This lets the system be tested at small scale (20 requests, easy to check by hand),
medium scale (200), and large scale (2,000, to observe how performance changes as the problem
grows) — all without needing access to confidential or real farm data.

---

## 7. Where This Fits in the Bigger AgriPulse System

This module is one piece of a larger Intelligent Decision Support System for tea smallholder
supply chains. Fertilizer allocation happens early in the overall workflow — before harvest,
spoilage-risk ranking, collection dispatch, or factory processing come into play. Its output
(which farms received fertilizer) is a self-contained decision that feeds into the smallholder
data available to the rest of the system, but does not directly depend on later stages.

---

## 8. Summary

In short: farms register, they submit fertilizer requests, and when a limited batch of
fertilizer becomes available, the system works out — using a proven optimization algorithm,
not guesswork — exactly which combination of requests to approve so that the available
fertilizer does the most good possible, while being upfront and honest about which farms
couldn't be served this round.
