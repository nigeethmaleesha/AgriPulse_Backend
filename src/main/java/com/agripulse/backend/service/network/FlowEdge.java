package com.agripulse.backend.service.network;

public record FlowEdge(String fromCode, String toCode, long capacityKgPerDay) {
    public FlowEdge {
        if (fromCode == null || fromCode.isBlank()) {
            throw new IllegalArgumentException("fromCode cannot be blank");
        }
        if (toCode == null || toCode.isBlank()) {
            throw new IllegalArgumentException("toCode cannot be blank");
        }
        if (capacityKgPerDay <= 0) {
            throw new IllegalArgumentException("capacityKgPerDay must be greater than 0");
        }
    }
}
