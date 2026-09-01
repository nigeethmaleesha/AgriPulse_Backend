package com.agripulse.backend.service.network;

import java.util.List;

public record FlowNetwork(List<String> nodeCodes, List<FlowEdge> edges) {
    public FlowNetwork {
        nodeCodes = List.copyOf(nodeCodes);
        edges = List.copyOf(edges);
    }
}
