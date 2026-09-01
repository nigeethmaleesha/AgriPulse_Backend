package com.agripulse.spoilage_risk_ranking.controller;

import com.agripulse.spoilage_risk_ranking.dto.PriorityBenchmarkResponse;
import com.agripulse.spoilage_risk_ranking.dto.PriorityQueueStatusResponse;
import com.agripulse.spoilage_risk_ranking.model.AlgorithmTestResult;
import com.agripulse.spoilage_risk_ranking.model.HarvestBatch;
import com.agripulse.spoilage_risk_ranking.service.PriorityQueueBenchmarkService;
import com.agripulse.spoilage_risk_ranking.service.PriorityQueueService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** REST API for Member 8 without changing Member 7 endpoints. */
@RestController
@RequestMapping("/api/spoilage/priority")
public class PriorityQueueController {

    private final PriorityQueueService priorityQueueService;
    private final PriorityQueueBenchmarkService benchmarkService;

    public PriorityQueueController(PriorityQueueService priorityQueueService,
                                   PriorityQueueBenchmarkService benchmarkService) {
        this.priorityQueueService = priorityQueueService;
        this.benchmarkService = benchmarkService;
    }

    /** Load ready batches from the shared harvest_batches table and build the max-heap. */
    @PostMapping("/reload")
    public PriorityQueueStatusResponse reload() {
        return priorityQueueService.reloadFromDatabase();
    }

    /** O(1): current highest-risk batch. */
    @GetMapping("/top")
    public HarvestBatch top() {
        return priorityQueueService.peekHighestRisk();
    }

    /** O(log n): remove and return top item from the LIVE queue only. */
    @PostMapping("/pop")
    public HarvestBatch pop() {
        return priorityQueueService.popHighestRisk();
    }

    /** Add a new incoming batch to PostgreSQL and the live max-heap. */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/batches")
    public HarvestBatch createAndEnqueue(@RequestBody HarvestBatch batch) {
        return priorityQueueService.createAndEnqueue(batch);
    }

    /** Add/update an existing ready database batch in the live heap. */
    @PostMapping("/enqueue/{batchId}")
    public HarvestBatch enqueueExisting(@PathVariable Long batchId) {
        return priorityQueueService.enqueueExisting(batchId);
    }

    /** Recalculate Member 7's risk score and reposition the batch in O(log n). */
    @PutMapping("/refresh/{batchId}")
    public HarvestBatch refresh(@PathVariable Long batchId) {
        return priorityQueueService.refreshPriority(batchId);
    }

    /** Internal heap-array order; index 0 is guaranteed to be the maximum. */
    @GetMapping("/heap")
    public List<HarvestBatch> heapOrder() {
        return priorityQueueService.heapOrder();
    }

    /** Fully ordered copy for demonstration/report screenshots; live heap is unchanged. */
    @GetMapping("/ordered")
    public List<HarvestBatch> ordered() {
        return priorityQueueService.priorityOrder();
    }

    @GetMapping("/status")
    public PriorityQueueStatusResponse status() {
        return priorityQueueService.status();
    }

    @DeleteMapping("/clear")
    public PriorityQueueStatusResponse clear() {
        return priorityQueueService.clear();
    }

    /** Coursework Task 4 benchmark presets from the supplied blueprint. */
    @GetMapping("/benchmark/presets")
    public Map<String, Object> benchmarkPresets() {
        return Map.of(
                "small", 100,
                "medium", 10_000,
                "large", 100_000,
                "keyMetrics", List.of("insertion time", "top-risk retrieval time", "memory")
        );
    }

    /**
     * Example: POST /api/spoilage/priority/benchmark?sizes=100,10000,100000&incomingOperations=50
     */
    @PostMapping("/benchmark")
    public PriorityBenchmarkResponse benchmark(@RequestParam String sizes,
                                               @RequestParam(required = false) Integer incomingOperations) {
        int[] parsed = Arrays.stream(sizes.split(","))
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .toArray();
        return benchmarkService.run(parsed, incomingOperations);
    }

    @GetMapping("/benchmark/results")
    public List<AlgorithmTestResult> benchmarkResults() {
        return benchmarkService.savedResults();
    }
}
