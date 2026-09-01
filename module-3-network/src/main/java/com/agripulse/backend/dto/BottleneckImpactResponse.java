package com.agripulse.backend.dto;

public record BottleneckImpactResponse(
        int rank,
        String fromCode,
        String toCode,
        long capacityKgPerDay,
        long baselineFlowKgPerDay,
        long baselineResidualCapacityKgPerDay,
        double baselineUtilizationPercent,
        boolean saturatedInBaseline,
        long maximumFlowIfClosedKgPerDay,
        long throughputLossIfClosedKgPerDay,
        double throughputImpactPercent,
        String impactLevel
) {
}
