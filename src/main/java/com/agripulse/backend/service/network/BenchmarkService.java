package com.agripulse.backend.service.network;

import com.agripulse.backend.dto.BenchmarkRequest;
import com.agripulse.backend.dto.BenchmarkResponse;
import com.agripulse.backend.model.AlgorithmTestResult;
import com.agripulse.backend.repository.AlgorithmTestResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BenchmarkService {

    private static final String MODULE = "Task 3A - Capacity Graph & Max Flow";
    private static final String ALGORITHM = "Ford-Fulkerson (synthetic benchmark)";

    private final SyntheticNetworkGenerator generator;
    private final FordFulkersonService fordFulkersonService;
    private final AlgorithmTestResultRepository testResultRepository;

    public BenchmarkService(SyntheticNetworkGenerator generator,
                            FordFulkersonService fordFulkersonService,
                            AlgorithmTestResultRepository testResultRepository) {
        this.generator = generator;
        this.fordFulkersonService = fordFulkersonService;
        this.testResultRepository = testResultRepository;
    }

    @Transactional
    public BenchmarkResponse run(BenchmarkRequest request) {
        long seed = request.seed() == null ? 42L : request.seed();
        long minCapacity = request.minCapacityKgPerDay() == null ? 100L : request.minCapacityKgPerDay();
        long maxCapacity = request.maxCapacityKgPerDay() == null ? 2_000L : request.maxCapacityKgPerDay();

        FlowNetwork network = generator.generate(
                request.nodeCount(), request.edgeCount(), seed, minCapacity, maxCapacity
        );
        MaxFlowComputation result = fordFulkersonService.computeMaxFlow(network, "SOURCE", "FACTORY");

        boolean save = request.saveResult() == null || request.saveResult();
        if (save) {
            testResultRepository.save(new AlgorithmTestResult(
                    MODULE, ALGORITHM, request.nodeCount(), request.edgeCount(),
                    result.executionTimeMs(), result.estimatedAlgorithmMemoryMb(), result.maximumFlowKgPerDay()
            ));
        }

        return new BenchmarkResponse(
                request.nodeCount(), request.edgeCount(), seed, result.maximumFlowKgPerDay(),
                round(result.executionTimeMs(), 4), round(result.estimatedAlgorithmMemoryMb(), 4),
                result.augmentingPaths().size()
        );
    }

    private double round(double value, int places) {
        double factor = Math.pow(10, places);
        return Math.round(value * factor) / factor;
    }
}
