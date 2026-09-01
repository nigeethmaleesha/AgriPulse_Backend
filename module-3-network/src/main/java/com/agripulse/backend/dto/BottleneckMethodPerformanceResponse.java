package com.agripulse.backend.dto;

public record BottleneckMethodPerformanceResponse(
        String method,
        String purpose,
        String complexityNote,
        long executionTimeNanos,
        double executionTimeMs,
        double estimatedPeakAlgorithmMemoryMb,
        int resultCount,
        boolean exactThroughputImpact
) {
}
