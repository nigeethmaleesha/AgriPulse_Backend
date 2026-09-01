package com.agripulse.backend.dto;

public record CapacityScenarioResultResponse(
        String name,
        CapacityScenarioType type,
        String fromCode,
        String toCode,
        long originalCapacityKgPerDay,
        long scenarioCapacityKgPerDay,
        long baselineMaximumFlowKgPerDay,
        long scenarioMaximumFlowKgPerDay,
        long throughputChangeKgPerDay,
        long throughputGainKgPerDay,
        long throughputLossKgPerDay,
        double throughputChangePercent,
        long scenarioFlowOnModifiedLinkKgPerDay,
        long scenarioResidualOnModifiedLinkKgPerDay,
        double scenarioUtilizationPercent,
        boolean modifiedLinkSaturatedAfterScenario,
        double executionTimeMs
) {
}
