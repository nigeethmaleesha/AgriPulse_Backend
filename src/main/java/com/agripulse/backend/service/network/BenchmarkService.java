package com.agripulse.backend.service.network;

import com.agripulse.backend.dto.BenchmarkRequest;
import com.agripulse.backend.dto.BenchmarkResponse;
import com.agripulse.backend.dto.BottleneckBenchmarkMethodResponse;
import com.agripulse.backend.dto.BottleneckBenchmarkResponse;
import com.agripulse.backend.model.AlgorithmTestResult;
import com.agripulse.backend.repository.AlgorithmTestResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared Module 3 benchmark service.
 *
 * Member 5 uses run(...) to benchmark the Ford-Fulkerson max-flow engine.
 * Member 6 uses runBottleneck(...) to compare bottleneck-analysis methods on
 * the same synthetic-network generator and the same Ford-Fulkerson engine.
 */
@Service
public class BenchmarkService {

    private static final String MEMBER5_MODULE = "Task 3A - Capacity Graph & Max Flow";
    private static final String MEMBER5_ALGORITHM = "Ford-Fulkerson (synthetic benchmark)";
    private static final String MEMBER6_MODULE = "Task 3B - Bottleneck & Capacity Scenarios";

    private final SyntheticNetworkGenerator generator;
    private final FordFulkersonService fordFulkersonService;
    private final BottleneckService bottleneckService;
    private final AlgorithmTestResultRepository testResultRepository;

    public BenchmarkService(
            SyntheticNetworkGenerator generator,
            FordFulkersonService fordFulkersonService,
            BottleneckService bottleneckService,
            AlgorithmTestResultRepository testResultRepository
    ) {
        this.generator = generator;
        this.fordFulkersonService = fordFulkersonService;
        this.bottleneckService = bottleneckService;
        this.testResultRepository = testResultRepository;
    }

    /** Member 5 benchmark: one Ford-Fulkerson run on a synthetic graph. */
    @Transactional
    public BenchmarkResponse run(BenchmarkRequest request) {
        BenchmarkInput input = normalize(request);

        FlowNetwork network = generator.generate(
                request.nodeCount(),
                request.edgeCount(),
                input.seed(),
                input.minCapacity(),
                input.maxCapacity()
        );

        MaxFlowComputation result = fordFulkersonService.computeMaxFlow(
                network,
                "SOURCE",
                "FACTORY"
        );

        if (input.saveResult()) {
            testResultRepository.save(new AlgorithmTestResult(
                    MEMBER5_MODULE,
                    MEMBER5_ALGORITHM,
                    request.nodeCount(),
                    request.edgeCount(),
                    result.executionTimeMs(),
                    result.estimatedAlgorithmMemoryMb(),
                    result.maximumFlowKgPerDay()
            ));
        }

        return new BenchmarkResponse(
                request.nodeCount(),
                request.edgeCount(),
                input.seed(),
                result.maximumFlowKgPerDay(),
                round4(result.executionTimeMs()),
                round4(result.estimatedAlgorithmMemoryMb()),
                result.augmentingPaths().size()
        );
    }

    /**
     * Member 6 benchmark: compare the three assigned bottleneck-analysis
     * approaches on exactly the same generated graph.
     */
    @Transactional
    public BottleneckBenchmarkResponse runBottleneck(BenchmarkRequest request) {
        BenchmarkInput input = normalize(request);
        int topN = request.topN() == null ? 10 : request.topN();

        FlowNetwork network = generator.generate(
                request.nodeCount(),
                request.edgeCount(),
                input.seed(),
                input.minCapacity(),
                input.maxCapacity()
        );

        MaxFlowComputation baseline = fordFulkersonService.computeMaxFlow(
                network,
                "SOURCE",
                "FACTORY"
        );

        List<BottleneckBenchmarkMethodResponse> methods = new ArrayList<>();

        long linearStarted = System.nanoTime();
        int saturatedCount = bottleneckService.linearSaturatedScan(baseline).size();
        long linearElapsed = System.nanoTime() - linearStarted;
        methods.add(new BottleneckBenchmarkMethodResponse(
                "Linear saturated-edge scan",
                nanosToMs(linearElapsed),
                round4(estimateListMemoryMb(saturatedCount)),
                saturatedCount,
                baseline.maximumFlowKgPerDay()
        ));

        long heapStarted = System.nanoTime();
        int heapCount = bottleneckService.heapRankByUtilization(baseline, topN).size();
        long heapElapsed = System.nanoTime() - heapStarted;
        methods.add(new BottleneckBenchmarkMethodResponse(
                "Max-heap utilization ranking",
                nanosToMs(heapElapsed),
                round4(estimateHeapMemoryMb(network.edges().size())),
                heapCount,
                baseline.maximumFlowKgPerDay()
        ));

        long exactStarted = System.nanoTime();
        int exactCount = bottleneckService.exactClosureImpactRanking(
                network,
                "SOURCE",
                "FACTORY",
                baseline,
                network.edges().size()
        ).size();
        long exactElapsed = System.nanoTime() - exactStarted;
        methods.add(new BottleneckBenchmarkMethodResponse(
                "Ford-Fulkerson closure scenario reruns",
                nanosToMs(exactElapsed),
                round4(baseline.estimatedAlgorithmMemoryMb()
                        + estimateListMemoryMb(network.edges().size())),
                exactCount,
                baseline.maximumFlowKgPerDay()
        ));

        if (input.saveResult()) {
            saveBottleneckBenchmark(
                    request.nodeCount(), request.edgeCount(), baseline.maximumFlowKgPerDay(),
                    "Linear saturated-edge scan", linearElapsed,
                    estimateListMemoryMb(saturatedCount)
            );
            saveBottleneckBenchmark(
                    request.nodeCount(), request.edgeCount(), baseline.maximumFlowKgPerDay(),
                    "Max-heap utilization ranking", heapElapsed,
                    estimateHeapMemoryMb(network.edges().size())
            );
            saveBottleneckBenchmark(
                    request.nodeCount(), request.edgeCount(), baseline.maximumFlowKgPerDay(),
                    "Ford-Fulkerson closure scenario reruns", exactElapsed,
                    baseline.estimatedAlgorithmMemoryMb()
                            + estimateListMemoryMb(network.edges().size())
            );
        }

        return new BottleneckBenchmarkResponse(
                request.nodeCount(),
                request.edgeCount(),
                input.seed(),
                baseline.maximumFlowKgPerDay(),
                methods,
                "All Member 6 candidate methods are run on the same synthetic graph. "
                        + "Closure-impact ranking is exact but slower because maximum flow is rerun for each edge."
        );
    }

    private BenchmarkInput normalize(BenchmarkRequest request) {
        long seed = request.seed() == null ? 42L : request.seed();
        long minCapacity = request.minCapacityKgPerDay() == null
                ? 100L : request.minCapacityKgPerDay();
        long maxCapacity = request.maxCapacityKgPerDay() == null
                ? 2_000L : request.maxCapacityKgPerDay();
        boolean save = request.saveResult() == null || request.saveResult();

        if (maxCapacity < minCapacity) {
            throw new IllegalArgumentException(
                    "maxCapacityKgPerDay must be greater than or equal to minCapacityKgPerDay"
            );
        }

        return new BenchmarkInput(seed, minCapacity, maxCapacity, save);
    }

    private void saveBottleneckBenchmark(
            int nodeCount,
            int edgeCount,
            long baselineMaximumFlow,
            String algorithm,
            long executionNanos,
            double estimatedMemoryMb
    ) {
        testResultRepository.save(new AlgorithmTestResult(
                MEMBER6_MODULE,
                algorithm,
                nodeCount,
                edgeCount,
                executionNanos / 1_000_000.0,
                estimatedMemoryMb,
                baselineMaximumFlow
        ));
    }

    private double estimateListMemoryMb(int entries) {
        return (entries * 64.0) / (1024.0 * 1024.0);
    }

    private double estimateHeapMemoryMb(int entries) {
        return (entries * 80.0) / (1024.0 * 1024.0);
    }

    private double nanosToMs(long nanos) {
        return round4(nanos / 1_000_000.0);
    }

    private double round4(double value) {
        return Math.round(value * 10_000.0) / 10_000.0;
    }

    private record BenchmarkInput(
            long seed,
            long minCapacity,
            long maxCapacity,
            boolean saveResult
    ) {
    }
}
