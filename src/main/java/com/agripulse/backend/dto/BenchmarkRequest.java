package com.agripulse.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record BenchmarkRequest(
        @Min(2) @Max(1000) int nodeCount,
        @Min(1) @Max(10000) int edgeCount,
        Long seed,
        @Min(1) Long minCapacityKgPerDay,
        @Min(2) Long maxCapacityKgPerDay,
        Boolean saveResult
) {
}
