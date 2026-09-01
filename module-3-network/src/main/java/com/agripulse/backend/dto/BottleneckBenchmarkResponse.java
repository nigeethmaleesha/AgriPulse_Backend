package com.agripulse.backend.dto;

import java.util.List;

public record BottleneckBenchmarkResponse(
        int nodeCount,
        int edgeCount,
        long seed,
        long baselineMaximumFlowKgPerDay,
        List<BottleneckBenchmarkMethodResponse> methods,
        String note
) {
    public BottleneckBenchmarkResponse {
        methods = List.copyOf(methods);
    }
}
