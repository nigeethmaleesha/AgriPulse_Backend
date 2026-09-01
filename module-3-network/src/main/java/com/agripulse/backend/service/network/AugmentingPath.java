package com.agripulse.backend.service.network;

import java.util.List;

public record AugmentingPath(List<String> path, long addedFlowKgPerDay) {
    public AugmentingPath {
        path = List.copyOf(path);
    }
}
