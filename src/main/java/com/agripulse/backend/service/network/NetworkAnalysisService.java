package com.agripulse.backend.service.network;

import com.agripulse.backend.dto.*;
import com.agripulse.backend.model.AlgorithmTestResult;
import com.agripulse.backend.repository.AlgorithmTestResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NetworkAnalysisService {

    private static final String MODULE = "Task 3A - Capacity Graph & Max Flow";
    private static final String ALGORITHM = "Ford-Fulkerson (DFS augmenting path)";

    private final SupplyNetworkService supplyNetworkService;
    private final FordFulkersonService fordFulkersonService;
    private final AlgorithmTestResultRepository testResultRepository;

    public NetworkAnalysisService(SupplyNetworkService supplyNetworkService,
                                  FordFulkersonService fordFulkersonService,
                                  AlgorithmTestResultRepository testResultRepository) {
        this.supplyNetworkService = supplyNetworkService;
        this.fordFulkersonService = fordFulkersonService;
        this.testResultRepository = testResultRepository;
    }

    @Transactional
    public MaxFlowResponse runMaxFlow(RunMaxFlowRequest request) {
        FlowNetwork network = supplyNetworkService.buildActiveFlowNetwork();
        MaxFlowComputation computation = fordFulkersonService.computeMaxFlow(
                network, request.sourceCode(), request.sinkCode()
        );

        boolean save = request.saveBenchmark() == null || request.saveBenchmark();
        if (save) {
            testResultRepository.save(new AlgorithmTestResult(
                    MODULE,
                    ALGORITHM,
                    network.nodeCodes().size(),
                    network.edges().size(),
                    computation.executionTimeMs(),
                    computation.estimatedAlgorithmMemoryMb(),
                    computation.maximumFlowKgPerDay()
            ));
        }

        return toResponse(computation, network.nodeCodes().size(), network.edges().size());
    }

    @Transactional(readOnly = true)
    public List<AlgorithmTestResultResponse> getRecentResults() {
        return testResultRepository.findTop100ByOrderByCreatedAtDesc().stream()
                .map(result -> new AlgorithmTestResultResponse(
                        result.getId(), result.getModule(), result.getAlgorithm(),
                        result.getInputSize(), result.getEdgeCount(), result.getExecutionTimeMs(),
                        result.getMemoryMb(), result.getSolutionMetric(), result.getCreatedAt()
                ))
                .toList();
    }

    private MaxFlowResponse toResponse(MaxFlowComputation c, int nodeCount, int edgeCount) {
        List<EdgeFlowResponse> edgeFlows = c.edgeFlows().stream()
                .map(edge -> new EdgeFlowResponse(
                        edge.fromCode(), edge.toCode(), edge.capacityKgPerDay(), edge.flowKgPerDay(),
                        edge.residualCapacityKgPerDay(), round(edge.utilizationPercent(), 2)
                ))
                .toList();

        List<AugmentingPathResponse> paths = c.augmentingPaths().stream()
                .map(path -> new AugmentingPathResponse(path.path(), path.addedFlowKgPerDay()))
                .toList();

        return new MaxFlowResponse(
                c.sourceCode(), c.sinkCode(), c.maximumFlowKgPerDay(), edgeFlows, paths,
                new PerformanceResponse(
                        nodeCount,
                        edgeCount,
                        c.augmentingPaths().size(),
                        round(c.executionTimeMs(), 4),
                        round(c.estimatedAlgorithmMemoryMb(), 4)
                )
        );
    }

    private double round(double value, int places) {
        double factor = Math.pow(10, places);
        return Math.round(value * factor) / factor;
    }
}
