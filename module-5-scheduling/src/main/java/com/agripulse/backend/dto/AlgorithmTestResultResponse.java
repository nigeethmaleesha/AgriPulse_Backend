package com.agripulse.backend.dto;

import java.time.LocalDateTime;

public record AlgorithmTestResultResponse(
        Long id,
        String module,
        String algorithm,
        int inputSize,
        int edgeCount,
        double executionTimeMs,
        double memoryMb,
        long solutionMetric,
        LocalDateTime createdAt
) {
}
