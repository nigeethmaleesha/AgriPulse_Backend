package com.agripulse.backend.service.network;

import java.util.List;

public record MaxFlowComputation(
        String sourceCode,
        String sinkCode,
        long maximumFlowKgPerDay,
        List<EdgeFlow> edgeFlows,
        List<AugmentingPath> augmentingPaths,
        long executionTimeNanos,
        double estimatedAlgorithmMemoryMb
) {
    public MaxFlowComputation {
        edgeFlows = List.copyOf(edgeFlows);
        augmentingPaths = List.copyOf(augmentingPaths);
    }

    public double executionTimeMs() {
        return executionTimeNanos / 1_000_000.0;
    }
}
