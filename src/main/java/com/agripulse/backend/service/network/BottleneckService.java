package com.agripulse.backend.service.network;

import com.agripulse.backend.dto.*;
import com.agripulse.backend.service.network.EdgeFlow;
import com.agripulse.backend.service.network.FlowEdge;
import com.agripulse.backend.service.network.FlowNetwork;
import com.agripulse.backend.service.network.FordFulkersonService;
import com.agripulse.backend.service.network.MaxFlowComputation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;

/**
 * Member 6 core implementation for Task 3B.
 *
 * It deliberately reuses Member 5's FordFulkersonService and immutable
 * FlowNetwork/FlowEdge model. All capacity scenarios are created only in
 * memory, so Member 6 does not change Member 5's database graph or classes.
 */
@Service
public class BottleneckService {

    private final FordFulkersonService fordFulkersonService;

    public BottleneckService(FordFulkersonService fordFulkersonService) {
        this.fordFulkersonService = fordFulkersonService;
    }

    /**
     * Runs the three Member 6 investigation approaches on one network:
     * 1) linear saturated-edge scan,
     * 2) max-heap utilization ranking,
     * 3) exact closure-impact ranking using repeated Ford-Fulkerson runs.
     */
    public BottleneckAnalysisResponse analyze(
            FlowNetwork network,
            String sourceCode,
            String sinkCode,
            int requestedTopN
    ) {
        int topN = normalizeTopN(requestedTopN, network.edges().size());

        MaxFlowComputation baseline = fordFulkersonService.computeMaxFlow(
                network, sourceCode, sinkCode
        );

        long linearStarted = System.nanoTime();
        List<SaturatedLinkResponse> saturatedLinks = linearSaturatedScan(baseline);
        long linearElapsed = System.nanoTime() - linearStarted;

        long heapStarted = System.nanoTime();
        List<HeapBottleneckResponse> heapLinks = heapRankByUtilization(baseline, topN);
        long heapElapsed = System.nanoTime() - heapStarted;

        long exactStarted = System.nanoTime();
        List<BottleneckImpactResponse> exactRanking = exactClosureImpactRanking(
                network, sourceCode, sinkCode, baseline, topN
        );
        long exactElapsed = System.nanoTime() - exactStarted;

        double matrixMemory = baseline.estimatedAlgorithmMemoryMb();
        double linearExtraMemory = estimateListMemoryMb(saturatedLinks.size());
        double heapExtraMemory = estimateHeapMemoryMb(network.edges().size());
        double exactExtraMemory = estimateListMemoryMb(network.edges().size());

        List<BottleneckMethodPerformanceResponse> performance = List.of(
                new BottleneckMethodPerformanceResponse(
                        "Linear saturated-edge scan",
                        "Scans Member 5's baseline edge-flow result and selects links with zero residual capacity.",
                        "O(E) after the baseline max-flow result exists",
                        linearElapsed,
                        nanosToMs(linearElapsed),
                        round4(linearExtraMemory),
                        saturatedLinks.size(),
                        false
                ),
                new BottleneckMethodPerformanceResponse(
                        "Max-heap utilization ranking",
                        "Inserts edge-flow records into a max-priority queue and retrieves the most highly utilized links.",
                        "O(E log E) to build/rank with Java PriorityQueue in this implementation",
                        heapElapsed,
                        nanosToMs(heapElapsed),
                        round4(heapExtraMemory),
                        heapLinks.size(),
                        false
                ),
                new BottleneckMethodPerformanceResponse(
                        "Ford-Fulkerson closure scenario reruns",
                        "Removes each link in memory, reruns Member 5's max-flow engine, and measures exact factory-throughput loss.",
                        "O(E × T_FF), where T_FF is one Ford-Fulkerson execution",
                        exactElapsed,
                        nanosToMs(exactElapsed),
                        round4(matrixMemory + exactExtraMemory),
                        exactRanking.size(),
                        true
                )
        );

        return new BottleneckAnalysisResponse(
                sourceCode,
                sinkCode,
                network.nodeCodes().size(),
                network.edges().size(),
                baseline.maximumFlowKgPerDay(),
                saturatedLinks,
                heapLinks,
                exactRanking,
                performance
        );
    }

    /**
     * Candidate approach 1: sequential/linear scan.
     * Saturated means flow == capacity, equivalently residual == 0.
     */
    public List<SaturatedLinkResponse> linearSaturatedScan(MaxFlowComputation baseline) {
        List<SaturatedLinkResponse> result = new ArrayList<>();

        for (EdgeFlow edge : baseline.edgeFlows()) {
            if (edge.residualCapacityKgPerDay() == 0L
                    && edge.capacityKgPerDay() > 0L) {
                result.add(new SaturatedLinkResponse(
                        edge.fromCode(),
                        edge.toCode(),
                        edge.capacityKgPerDay(),
                        edge.flowKgPerDay(),
                        edge.residualCapacityKgPerDay(),
                        round2(edge.utilizationPercent())
                ));
            }
        }

        return List.copyOf(result);
    }

    /**
     * Candidate approach 2: max heap / priority queue.
     * This is a heuristic ranking, not an exact throughput-impact ranking.
     */
    public List<HeapBottleneckResponse> heapRankByUtilization(
            MaxFlowComputation baseline,
            int requestedTopN
    ) {
        int topN = Math.max(1, Math.min(requestedTopN, baseline.edgeFlows().size()));

        PriorityQueue<EdgeFlow> maxHeap = new PriorityQueue<>(
                Comparator.comparingDouble(EdgeFlow::utilizationPercent)
                        .reversed()
                        .thenComparing(Comparator.comparingLong(EdgeFlow::flowKgPerDay).reversed())
                        .thenComparing(Comparator.comparingLong(EdgeFlow::capacityKgPerDay).reversed())
                        .thenComparing(EdgeFlow::fromCode)
                        .thenComparing(EdgeFlow::toCode)
        );

        maxHeap.addAll(baseline.edgeFlows());

        List<HeapBottleneckResponse> result = new ArrayList<>();
        for (int rank = 1; rank <= topN && !maxHeap.isEmpty(); rank++) {
            EdgeFlow edge = maxHeap.poll();
            result.add(new HeapBottleneckResponse(
                    rank,
                    edge.fromCode(),
                    edge.toCode(),
                    edge.capacityKgPerDay(),
                    edge.flowKgPerDay(),
                    edge.residualCapacityKgPerDay(),
                    round2(edge.utilizationPercent()),
                    edge.residualCapacityKgPerDay() == 0L
            ));
        }

        return List.copyOf(result);
    }

    /**
     * Candidate approach 3 / selected exact impact method.
     * Every edge is closed one at a time in a copied in-memory network.
     */
    public List<BottleneckImpactResponse> exactClosureImpactRanking(
            FlowNetwork network,
            String sourceCode,
            String sinkCode,
            MaxFlowComputation baseline,
            int requestedTopN
    ) {
        List<BottleneckImpactResponse> unranked = new ArrayList<>();

        for (FlowEdge edge : network.edges()) {
            FlowNetwork closedNetwork = withCapacityChange(
                    network,
                    edge.fromCode(),
                    edge.toCode(),
                    0L
            );

            MaxFlowComputation closed = fordFulkersonService.computeMaxFlow(
                    closedNetwork,
                    sourceCode,
                    sinkCode
            );

            EdgeFlow baselineEdge = findEdgeFlow(
                    baseline,
                    edge.fromCode(),
                    edge.toCode()
            );

            long loss = Math.max(
                    0L,
                    baseline.maximumFlowKgPerDay() - closed.maximumFlowKgPerDay()
            );

            double impactPercent = baseline.maximumFlowKgPerDay() == 0L
                    ? 0.0
                    : (loss * 100.0) / baseline.maximumFlowKgPerDay();

            unranked.add(new BottleneckImpactResponse(
                    0,
                    edge.fromCode(),
                    edge.toCode(),
                    edge.capacityKgPerDay(),
                    baselineEdge.flowKgPerDay(),
                    baselineEdge.residualCapacityKgPerDay(),
                    round2(baselineEdge.utilizationPercent()),
                    baselineEdge.residualCapacityKgPerDay() == 0L,
                    closed.maximumFlowKgPerDay(),
                    loss,
                    round2(impactPercent),
                    impactLevel(impactPercent)
            ));
        }

        unranked.sort(
                Comparator.comparingLong(BottleneckImpactResponse::throughputLossIfClosedKgPerDay)
                        .reversed()
                        .thenComparing(BottleneckImpactResponse::saturatedInBaseline, Comparator.reverseOrder())
                        .thenComparing(Comparator.comparingDouble(BottleneckImpactResponse::baselineUtilizationPercent).reversed())
                        .thenComparing(BottleneckImpactResponse::fromCode)
                        .thenComparing(BottleneckImpactResponse::toCode)
        );

        int topN = normalizeTopN(requestedTopN, unranked.size());
        List<BottleneckImpactResponse> ranked = new ArrayList<>();

        for (int i = 0; i < topN; i++) {
            BottleneckImpactResponse item = unranked.get(i);
            ranked.add(new BottleneckImpactResponse(
                    i + 1,
                    item.fromCode(),
                    item.toCode(),
                    item.capacityKgPerDay(),
                    item.baselineFlowKgPerDay(),
                    item.baselineResidualCapacityKgPerDay(),
                    item.baselineUtilizationPercent(),
                    item.saturatedInBaseline(),
                    item.maximumFlowIfClosedKgPerDay(),
                    item.throughputLossIfClosedKgPerDay(),
                    item.throughputImpactPercent(),
                    item.impactLevel()
            ));
        }

        return List.copyOf(ranked);
    }

    public CapacityScenarioResultResponse runScenario(
            FlowNetwork network,
            String sourceCode,
            String sinkCode,
            CapacityScenarioRequest scenario
    ) {
        MaxFlowComputation baseline = fordFulkersonService.computeMaxFlow(
                network, sourceCode, sinkCode
        );
        return runScenarioWithBaseline(network, sourceCode, sinkCode, baseline, scenario);
    }

    public CapacityScenarioBatchResponse runScenarios(
            FlowNetwork network,
            String sourceCode,
            String sinkCode,
            List<CapacityScenarioRequest> scenarios
    ) {
        MaxFlowComputation baseline = fordFulkersonService.computeMaxFlow(
                network, sourceCode, sinkCode
        );

        List<CapacityScenarioResultResponse> results = new ArrayList<>();
        for (CapacityScenarioRequest scenario : scenarios) {
            results.add(runScenarioWithBaseline(
                    network, sourceCode, sinkCode, baseline, scenario
            ));
        }

        return new CapacityScenarioBatchResponse(
                sourceCode,
                sinkCode,
                baseline.maximumFlowKgPerDay(),
                results
        );
    }

    private CapacityScenarioResultResponse runScenarioWithBaseline(
            FlowNetwork network,
            String sourceCode,
            String sinkCode,
            MaxFlowComputation baseline,
            CapacityScenarioRequest scenario
    ) {
        FlowEdge target = findFlowEdge(
                network,
                scenario.fromCode(),
                scenario.toCode()
        );

        long newCapacity = calculateScenarioCapacity(
                target.capacityKgPerDay(),
                scenario
        );

        FlowNetwork scenarioNetwork = withCapacityChange(
                network,
                target.fromCode(),
                target.toCode(),
                newCapacity
        );

        long started = System.nanoTime();
        MaxFlowComputation scenarioResult = fordFulkersonService.computeMaxFlow(
                scenarioNetwork,
                sourceCode,
                sinkCode
        );
        long elapsed = System.nanoTime() - started;

        long throughputChange = scenarioResult.maximumFlowKgPerDay()
                - baseline.maximumFlowKgPerDay();
        long gain = Math.max(0L, throughputChange);
        long loss = Math.max(0L, -throughputChange);

        double changePercent = baseline.maximumFlowKgPerDay() == 0L
                ? 0.0
                : (throughputChange * 100.0) / baseline.maximumFlowKgPerDay();

        EdgeFlow scenarioEdge = findEdgeFlowOrZero(
                scenarioResult,
                target.fromCode(),
                target.toCode(),
                newCapacity
        );

        return new CapacityScenarioResultResponse(
                scenario.name(),
                scenario.type(),
                target.fromCode(),
                target.toCode(),
                target.capacityKgPerDay(),
                newCapacity,
                baseline.maximumFlowKgPerDay(),
                scenarioResult.maximumFlowKgPerDay(),
                throughputChange,
                gain,
                loss,
                round2(changePercent),
                scenarioEdge.flowKgPerDay(),
                scenarioEdge.residualCapacityKgPerDay(),
                round2(scenarioEdge.utilizationPercent()),
                newCapacity > 0L && scenarioEdge.residualCapacityKgPerDay() == 0L,
                nanosToMs(elapsed)
        );
    }

    /**
     * Creates a new immutable network. A new capacity of zero represents a
     * closed link, so the edge is omitted because Member 5's FlowEdge requires
     * positive capacities.
     */
    public FlowNetwork withCapacityChange(
            FlowNetwork network,
            String fromCode,
            String toCode,
            long newCapacityKgPerDay
    ) {
        if (newCapacityKgPerDay < 0L) {
            throw new IllegalArgumentException("New capacity cannot be negative");
        }

        List<FlowEdge> changedEdges = new ArrayList<>();
        boolean found = false;

        for (FlowEdge edge : network.edges()) {
            if (sameCode(edge.fromCode(), fromCode)
                    && sameCode(edge.toCode(), toCode)) {
                found = true;
                if (newCapacityKgPerDay > 0L) {
                    changedEdges.add(new FlowEdge(
                            edge.fromCode(),
                            edge.toCode(),
                            newCapacityKgPerDay
                    ));
                }
            } else {
                changedEdges.add(edge);
            }
        }

        if (!found) {
            throw new IllegalArgumentException(
                    "Capacity link not found: " + fromCode + " -> " + toCode
            );
        }

        return new FlowNetwork(network.nodeCodes(), changedEdges);
    }

    private long calculateScenarioCapacity(
            long originalCapacity,
            CapacityScenarioRequest scenario
    ) {
        return switch (scenario.type()) {
            case CLOSE_LINK -> 0L;

            case SET_CAPACITY -> {
                if (scenario.newCapacityKgPerDay() == null) {
                    throw new IllegalArgumentException(
                            "SET_CAPACITY requires newCapacityKgPerDay"
                    );
                }
                yield scenario.newCapacityKgPerDay();
            }

            case REDUCE_BY_PERCENT -> {
                double percent = requiredPercent(scenario);
                if (percent > 100.0) {
                    throw new IllegalArgumentException(
                            "REDUCE_BY_PERCENT requires percent from 0 to 100"
                    );
                }
                yield Math.max(
                        0L,
                        Math.round(originalCapacity * (1.0 - percent / 100.0))
                );
            }

            case INCREASE_BY_PERCENT -> {
                double percent = requiredPercent(scenario);
                yield Math.round(
                        originalCapacity * (1.0 + percent / 100.0)
                );
            }
        };
    }

    private double requiredPercent(CapacityScenarioRequest scenario) {
        if (scenario.percent() == null) {
            throw new IllegalArgumentException(
                    scenario.type() + " requires percent"
            );
        }
        return scenario.percent();
    }

    private FlowEdge findFlowEdge(
            FlowNetwork network,
            String fromCode,
            String toCode
    ) {
        return network.edges().stream()
                .filter(edge -> sameCode(edge.fromCode(), fromCode)
                        && sameCode(edge.toCode(), toCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Capacity link not found: " + fromCode + " -> " + toCode
                ));
    }

    private EdgeFlow findEdgeFlow(
            MaxFlowComputation result,
            String fromCode,
            String toCode
    ) {
        return result.edgeFlows().stream()
                .filter(edge -> sameCode(edge.fromCode(), fromCode)
                        && sameCode(edge.toCode(), toCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Edge-flow result not found: " + fromCode + " -> " + toCode
                ));
    }

    private EdgeFlow findEdgeFlowOrZero(
            MaxFlowComputation result,
            String fromCode,
            String toCode,
            long scenarioCapacity
    ) {
        return result.edgeFlows().stream()
                .filter(edge -> sameCode(edge.fromCode(), fromCode)
                        && sameCode(edge.toCode(), toCode))
                .findFirst()
                .orElse(new EdgeFlow(
                        fromCode,
                        toCode,
                        scenarioCapacity,
                        0L,
                        scenarioCapacity
                ));
    }

    private int normalizeTopN(int requestedTopN, int size) {
        if (size == 0) {
            return 0;
        }
        return Math.max(1, Math.min(requestedTopN, size));
    }

    private String impactLevel(double impactPercent) {
        // Project-defined dashboard labels. They are not agricultural standards.
        if (impactPercent >= 25.0) {
            return "CRITICAL";
        }
        if (impactPercent >= 10.0) {
            return "HIGH";
        }
        if (impactPercent > 0.0) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private boolean sameCode(String a, String b) {
        return normalize(a).equals(normalize(b));
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private double estimateListMemoryMb(int entries) {
        // Transparent rough estimate for comparison only, not total JVM memory.
        return (entries * 64.0) / (1024.0 * 1024.0);
    }

    private double estimateHeapMemoryMb(int entries) {
        // Approximate priority-queue references + candidate object/reference cost.
        return (entries * 80.0) / (1024.0 * 1024.0);
    }

    private double nanosToMs(long nanos) {
        return round4(nanos / 1_000_000.0);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double round4(double value) {
        return Math.round(value * 10_000.0) / 10_000.0;
    }
}
