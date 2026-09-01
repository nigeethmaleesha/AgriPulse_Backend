package com.agripulse.backend.dto;

public record EdgeFlowResponse(
        String fromCode,
        String toCode,
        long capacityKgPerDay,
        long flowKgPerDay,
        long residualCapacityKgPerDay,
        double utilizationPercent
) {
}
