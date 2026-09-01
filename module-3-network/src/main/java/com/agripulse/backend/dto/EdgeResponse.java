package com.agripulse.backend.dto;

public record EdgeResponse(
        Long id,
        String fromCode,
        String toCode,
        long capacityKgPerDay,
        boolean active,
        String label
) {
}
