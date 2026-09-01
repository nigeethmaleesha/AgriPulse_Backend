package com.agripulse.backend.dto;

import java.util.List;

public record CapacityScenarioBatchResponse(
        String sourceCode,
        String sinkCode,
        long baselineMaximumFlowKgPerDay,
        List<CapacityScenarioResultResponse> scenarios
) {
    public CapacityScenarioBatchResponse {
        scenarios = List.copyOf(scenarios);
    }
}
