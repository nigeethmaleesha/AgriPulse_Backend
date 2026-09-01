package com.agripulse.backend.dto;

import java.util.List;

public record MaxFlowResponse(
        String sourceCode,
        String sinkCode,
        long maximumFlowKgPerDay,
        List<EdgeFlowResponse> edgeFlows,
        List<AugmentingPathResponse> augmentingPaths,
        PerformanceResponse performance
) {
}
