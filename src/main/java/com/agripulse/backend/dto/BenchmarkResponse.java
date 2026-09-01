package com.agripulse.backend.dto;

public record BenchmarkResponse(
        int nodeCount,
        int edgeCount,
        long seed,
        long maximumFlowKgPerDay,
        double executionTimeMs,
        double estimatedAlgorithmMemoryMb,
        int augmentingPathCount
) {
}
