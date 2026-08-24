# Member 6 Handoff - Bottleneck & Capacity Scenario Analyzer

Member 5 implementation intentionally keeps the reusable maximum-flow engine separate from Member 6 logic.

## Where Member 6 should add code

Create:

- `src/main/java/com/agripulse/backend/service/network/BottleneckService.java`
- Member 6 DTOs under `src/main/java/com/agripulse/backend/dto/`
- Member 6 endpoints in a new `BottleneckController.java` or under `/api/network/scenarios`
- Member 6 tests under `src/test/java/com/agripulse/backend/service/network/`

## Reuse these Member 5 classes

- `FordFulkersonService` - rerun max flow after changing capacities.
- `FlowNetwork` and `FlowEdge` - immutable in-memory network input.
- `MaxFlowComputation` - baseline/scenario max flow, edge flow, residual capacities.
- `SupplyNetworkService.buildActiveFlowNetwork()` - load the current PostgreSQL graph.

## Recommended Member 6 flow

1. Load baseline `FlowNetwork`.
2. Run `FordFulkersonService.computeMaxFlow(...)` and store baseline max flow.
3. Copy the edge list in memory; DO NOT permanently overwrite the database for temporary scenarios.
4. Apply one scenario:
   - reduced capacity,
   - closed link (remove edge or set in-memory capacity to zero by excluding it),
   - increased/upgraded capacity.
5. Rerun Ford-Fulkerson.
6. Compare `scenarioMaxFlow - baselineMaxFlow`.
7. Saturated link = edge where `flowKgPerDay == capacityKgPerDay` in the result.
8. Rank candidate bottlenecks by throughput impact from closure/reduction tests.

This keeps Member 5 and Member 6 commits/contributions clearly identifiable.
