package com.agripulse.backend.dto;

import java.util.List;

public record BottleneckAnalysisResponse(
        String sourceCode,
        String sinkCode,
        int nodeCount,
        int edgeCount,
        long baselineMaximumFlowKgPerDay,
        List<SaturatedLinkResponse> linearSaturatedLinks,
        List<HeapBottleneckResponse> heapRankedLinks,
        List<BottleneckImpactResponse> exactClosureImpactRanking,
        List<BottleneckMethodPerformanceResponse> methodPerformance
) {
    public BottleneckAnalysisResponse {
        linearSaturatedLinks = List.copyOf(linearSaturatedLinks);
        heapRankedLinks = List.copyOf(heapRankedLinks);
        exactClosureImpactRanking = List.copyOf(exactClosureImpactRanking);
        methodPerformance = List.copyOf(methodPerformance);
    }
}
