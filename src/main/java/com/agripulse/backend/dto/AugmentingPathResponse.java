package com.agripulse.backend.dto;

import java.util.List;

public record AugmentingPathResponse(List<String> path, long addedFlowKgPerDay) {
}
