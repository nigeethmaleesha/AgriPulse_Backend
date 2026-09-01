package com.agripulse.backend.dto;

public record PerformanceResponse(
        int nodeCount,
        int edgeCount,
        int augmentingPathCount,
        double executionTimeMs,
        double estimatedAlgorithmMemoryMb
) {
}
