package com.agripulse.backend.dto;

public record HeapBottleneckResponse(
        int rank,
        String fromCode,
        String toCode,
        long capacityKgPerDay,
        long flowKgPerDay,
        long residualCapacityKgPerDay,
        double utilizationPercent,
        boolean saturated
) {
}
