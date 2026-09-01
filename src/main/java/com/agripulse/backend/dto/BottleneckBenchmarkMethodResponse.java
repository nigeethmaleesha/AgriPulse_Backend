package com.agripulse.backend.dto;

public record BottleneckBenchmarkMethodResponse(
        String method,
        double executionTimeMs,
        double estimatedPeakAlgorithmMemoryMb,
        int resultCount,
        long baselineMaximumFlowKgPerDay
) {
}
