package com.agripulse.backend.dto;

public record SaturatedLinkResponse(
        String fromCode,
        String toCode,
        long capacityKgPerDay,
        long flowKgPerDay,
        long residualCapacityKgPerDay,
        double utilizationPercent
) {
}
